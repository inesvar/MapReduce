package src;

import src.messages.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class SlaveManager extends Thread {

    // almost final variables (written only once before the Worker starts)
    public static int NB_SLAVES;
    public static Integer[] SLAVES;
    public static Integer[] PORT;
    public static String[] IP;
    public static Integer ID;
    public static Integer MASTER = 0;

    // variables that will be accessed by multiple threads
    private volatile static int receivedData = 0;
    private volatile static ArrayList<Map.Entry<String, Long>> wordList = new ArrayList<>();
    private volatile static ArrayList<Map.Entry<Long, ArrayList<String>>> occurenceList = new ArrayList<>();
    private static ArrayList<String> fileInput = new ArrayList<>();
    private static SlaveWorker worker;
    private volatile static boolean oneReduceDone = false;

    private static Listener ml;

    public static void main(String[] args) {
        // get the number of slaves
        NB_SLAVES = Integer.valueOf(args[0]);
        SLAVES = new Integer[NB_SLAVES];
        for (int i = 0; i < NB_SLAVES; i++) {
            SLAVES[i] = i+1;
        }
        // get the id of the slave
        ID = Integer.valueOf(args[1]);

        // get the name of the files
        for (int i = 2; i < args.length; i++) {
            fileInput.add(args[i]);
        }

        int PORT0 = 50000;
        PORT = new Integer[NB_SLAVES + 1];
        for (int i = 0; i < NB_SLAVES + 1; i++) {
            PORT[i] = PORT0 + i;
        }
        // Start the listener
        ml = new Listener();
        ml.start();

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
        // Start the slave
        worker = new SlaveWorker(ID, IP, fileInput);
        worker.start();
    }

    private Socket socket;
    
    public SlaveManager(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            ObjectInputStream oin = new ObjectInputStream(this.socket.getInputStream());
            Object obj = oin.readObject(); 
            if (obj instanceof MapReady) {
                worker.startMap();
            } else if (obj instanceof ShuffleReady) {
                worker.startShuffle();
            } else if (obj instanceof ReduceReady) {
                synchronized(this) { 
                    if (!oneReduceDone) {
                        worker.startReduceWordCount(wordList);
                        oneReduceDone = true;
                    } else {
                        worker.startReduceWordOccurences(occurenceList);
                    }
                }
            } else if (obj instanceof ShuffleWordCount) {
                ShuffleWordCount sd = (ShuffleWordCount) obj;
                int rr;
                synchronized(this) {
                    wordList.addAll(sd.getEntries());
                    rr = ++receivedData;
                }
                if (rr == NB_SLAVES) {
                    // NOTIFY THE MASTER THAT ALL THE PACKETS HAVE BEEN RECEIVED
                    Sender senderRR = new Sender(MASTER, new ReduceReady());
                    senderRR.start();
                }
            } else if (obj instanceof ShuffleWordOccurences) {
                ShuffleWordOccurences sd = (ShuffleWordOccurences) obj;
                int rr;
                synchronized(this) {
                    occurenceList.addAll(sd.getEntries());
                    rr = ++receivedData;
                }
                if (rr == 2 * NB_SLAVES) {
                    // NOTIFY THE MASTER THAT ALL THE PACKETS HAVE BEEN RECEIVED
                    Sender senderRR = new Sender(MASTER, new ReduceReady());
                    senderRR.start();
                }
            } else if (obj instanceof Kill) {
                System.out.println("slave is shutting down ml");
                // Kill the listener
                ml.interrupt();
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
