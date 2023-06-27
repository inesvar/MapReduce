package src;

import src.messages.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Map;

public class MasterManager extends Thread {

    public static final Integer ID = 0;
    // almost final variables
    public static int NB_SLAVES;
    public static Integer[] SLAVES;
    public static Integer[] PORT;
    public static String[] IP;

    // variables that will be accessed by multiple threads
    private volatile static int readyForMap = 0;
    private volatile static int readyForShuffle = 0;
    private volatile static int readyForReduce = 0;
    private volatile static int dataReceived = 0;
    private volatile static TreeMap<Long, ArrayList<String>> wordOccurences = new TreeMap<Long, ArrayList<String>>();

    // variables that will be accessed by multiple threads but not at the same time
    private volatile static long startTime, endTime, totalTime;
    // will be written by only one thread
    private static volatile boolean done = false;

    public static void main(String[] args) throws InterruptedException {
        // get the number of slaves
        NB_SLAVES = Integer.valueOf(args[0]);
        SLAVES = new Integer[NB_SLAVES];
        for (int i = 0; i < NB_SLAVES; i++) {
            SLAVES[i] = i+1;
        }

        int PORT0 = 50000;
        PORT = new Integer[NB_SLAVES + 1];
        for (int i = 0; i < NB_SLAVES + 1; i++) {
            PORT[i] = PORT0 + i;
        }
        // read the IPs from the file IPfile.txt
        IP = new String[NB_SLAVES + 1];
        try {
            BufferedReader br = new BufferedReader(new FileReader("IPs.txt"));
            for (int i = 0; i < NB_SLAVES + 1; i++) {
            IP[i] = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Listener ml = new Listener();
        ml.start();

        while (!done) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ml.interrupt();
        // write the wordOccurences to a file
        String fileOutput = "output.txt";
            try {
                PrintWriter writer = new PrintWriter(fileOutput, "UTF-8");
                for (Map.Entry<Long, ArrayList<String>> entry : wordOccurences.entrySet()) {
                    writer.println(entry.getKey() + " : " + entry.getValue().toString());
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        System.out.println("result written to file");
        for (int slave : SLAVES) {
            Sender ms = new Sender(slave, new Kill());
            ms.start();
        };
        System.out.println("master has sent kill to all slaves");
        Thread.sleep(1000);
        System.exit(0);
    }

    private Socket socket;
    
    public MasterManager(Socket socket) {
        this.socket = socket;
    }

    public void startMap() {
        for (int slave : SLAVES) {
            Sender ms = new Sender(slave, new MapReady());
            ms.start();
        };
    }

    public void startShuffle() {
        for (int slave : SLAVES) {
            Sender ms = new Sender(slave, new ShuffleReady());
            ms.start();
        };
    }

    public void startReduce() {
        for (int slave : SLAVES) {
            Sender ms = new Sender(slave, new ReduceReady());
            ms.start();
        };
    }

    public void run() {
        try {
            ObjectInputStream oin = new ObjectInputStream(this.socket.getInputStream());
            Object obj = oin.readObject(); 
            if (obj instanceof MapReady) {
                boolean ready = false;
                Integer rs;
                synchronized(this) {
                    rs = ++readyForMap;
                    ready = readyForMap % NB_SLAVES == 0;
                }
                if (ready) {
                    if (rs > NB_SLAVES) {
                        endTime = System.currentTimeMillis();
                        totalTime = endTime - startTime;
                        System.out.println("\n\nTotal time for the reduce: " + totalTime + "\n\n");
                    }
                    startTime = System.currentTimeMillis();
                    startMap();
                }
            } else if (obj instanceof ShuffleReady) {
                boolean ready = false;
                synchronized(this) {
                    readyForShuffle++;
                    ready = readyForShuffle % NB_SLAVES == 0;
                }
                if (ready) {
                    endTime = System.currentTimeMillis();
                    totalTime = endTime - startTime;
                    System.out.println("\n\nTotal time for the map: " + totalTime + "\n\n");
                    startTime = System.currentTimeMillis();
                    startShuffle();
                }
            } else if (obj instanceof ReduceReady) {
                boolean ready = false;
                synchronized(this) {
                    readyForReduce++;
                    ready = readyForReduce % NB_SLAVES == 0;
                }
                if (ready) {
                    endTime = System.currentTimeMillis();
                    totalTime = endTime - startTime;
                    System.out.println("\n\nTotal time for the shuffle: " + totalTime + "\n\n");
                    startTime = System.currentTimeMillis();
                    startReduce();
                }
            } else if (obj instanceof ReduceResult) {
                ReduceResult rr = (ReduceResult) obj;
                synchronized(this) {
                    dataReceived++;
                    wordOccurences.putAll(rr.getWordOccurences());
                    if (dataReceived == NB_SLAVES) {
                        endTime = System.currentTimeMillis();
                        totalTime = endTime - startTime;
                        System.out.println("\n\nTotal time for the reduce: " + totalTime + "\n\n");
                        startTime = System.currentTimeMillis();
                        done = true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
