package src;

import src.messages.ShuffleReady;
import src.messages.ReduceReady;
import src.messages.ReduceResult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;

public class Manager extends Thread {

    // almost final variables
    public static int NB_SLAVES;
    public static Integer MASTER_PORT = 10000;
    public static Integer SLAVE_PORT = MASTER_PORT + 1;
    private static String[] IPs;

    // variables that will be accessed by multiple threads
    private volatile static int readyForShuffle = 0;
    private volatile static int readyForReduce = 0;
    private volatile static int dataReceived = 0;
    private volatile static HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

    // will be written by only one thread
    private static volatile boolean done = false;

    public static void main(String[] args) {
        // get the number of slaves
        NB_SLAVES = Integer.valueOf(args[0]);

        if (args.length >= 2) {
            MASTER_PORT = Integer.valueOf(args[1]);
            SLAVE_PORT = MASTER_PORT + 1;
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
        System.out.println("result " + wordCount.toString());
        System.exit(0);
    }

    private Socket socket;
    
    public Manager(Socket socket) {
        this.socket = socket;
    }

    public void startShuffle() {
        // read the IPs from the file IPfile.txt
        IPs = new String[NB_SLAVES + 1];
        try {
            BufferedReader br = new BufferedReader(new FileReader("IPs.txt"));
            for (int i = 0; i < NB_SLAVES + 1; i++) {
            IPs[i] = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < NB_SLAVES; i++) {
            Sender ms = new Sender(IPs[i], new ShuffleReady(NB_SLAVES));
            ms.start();
        };
    }

    public void startReduce() {
        for (int i = 0; i < NB_SLAVES; i++) {
            Sender ms = new Sender(Manager.IPs[i], new ReduceReady(NB_SLAVES));
            ms.start();
        };
    }

    public void run() {
        try {
            ObjectInputStream oin = new ObjectInputStream(this.socket.getInputStream());
            Object obj = oin.readObject(); 
            if (obj instanceof ShuffleReady) {
                boolean ready = false;
                Integer rs;
                synchronized(this) {
                    rs = ++readyForShuffle;
                    ready = readyForShuffle == NB_SLAVES;
                }
                System.out.println(rs.toString() + " slave(s) ready for shuffle");
                if (ready) {
                    startShuffle();
                }
            } else if (obj instanceof ReduceReady) {
                boolean ready = false;
                Integer rr;
                synchronized(this) {
                    rr = ++readyForReduce;
                    ready = readyForReduce == NB_SLAVES;
                }
                System.out.println(rr.toString() + " slave(s) ready for reduce");
                if (ready) {
                    startReduce();
                }
            } else if (obj instanceof ReduceResult) {
                ReduceResult rr = (ReduceResult) obj;
                Integer dr;
                synchronized(this) {
                    dr = ++dataReceived;
                    wordCount.putAll(rr.getWordCount());
                    if (dataReceived == NB_SLAVES) {
                        done = true;
                    }
                }
                System.out.println(dr.toString() + " slave(s) done");
            } else {
                System.out.println("the message was not recognized");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
