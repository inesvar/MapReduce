package src;

import src.messages.ShuffleReady;
import src.messages.ReduceReady;
import src.messages.ShuffleData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class Manager extends Thread {

    // almost final variables
    public static int NB_SLAVES;
    public static Integer[] SLAVES;
    public static Integer[] PORT;
    public static String[] IP;
    public static Integer ID;
    public static Integer MASTER = 0;

    // variables that will be accessed by multiple threads
    private volatile static int receivedData = 0;
    private volatile static ArrayList<Map.Entry<String, Integer>> wordList = new ArrayList<>();

    private static SlaveWorker worker;

    public static void main(String[] args) {
        // get the number of slaves
        NB_SLAVES = Integer.valueOf(args[0]);
        SLAVES = new Integer[NB_SLAVES];
        for (int i = 0; i < NB_SLAVES; i++) {
            SLAVES[i] = i+1;
        }

        // get the id of the slave
        ID = Integer.valueOf(args[1]);
        System.out.println("36");
        int PORT0;
        if (args.length >= 3) {
            PORT0 = Integer.valueOf(args[2]);
        } else {
            PORT0 = 10000;
        }
        PORT = new Integer[NB_SLAVES + 1];
        for (int i = 0; i < NB_SLAVES + 1; i++) {
            PORT[i] = PORT0 + i;
        }
        System.out.println("47");
        // Start the listener
        Listener ml = new Listener();
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
        System.out.println("63");
        // Start the slaves
        worker = new SlaveWorker(ID, IP);
        worker.start();
    }

    private Socket socket;
    
    public Manager(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            ObjectInputStream oin = new ObjectInputStream(this.socket.getInputStream());
            Object obj = oin.readObject(); 
            if (obj instanceof ShuffleReady) {
                worker.startShuffle();
                System.out.println("the slaves started the shuffle");
            } else if (obj instanceof ReduceReady) {
                synchronized(this) { worker.startReduce(wordList); }
            } else if (obj instanceof ShuffleData) {
                ShuffleData sd = (ShuffleData) obj;
                int rr;
                synchronized(this) {
                    wordList.addAll(sd.getEntries());
                    rr = ++receivedData;
                }
                if (rr == NB_SLAVES) {
                    // NOTIFY THE MASTER THAT ALL THE PACKETS HAVE BEEN RECEIVED
                    System.out.println("trying to connect to " + IP[MASTER] + " port " + PORT[MASTER].toString());
                    Sender senderRR = new Sender(MASTER, new ReduceReady(ID));
                    senderRR.start();
                }
                System.out.println("slave " + ID.toString() + " received entry");
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
