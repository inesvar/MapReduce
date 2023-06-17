package src;

import src.messages.ShuffleReady;
import src.SlaveWorker;
import src.messages.ReduceReady;
import src.messages.ReduceResult;
import src.messages.ShuffleData;
import src.Sender;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manager extends Thread {

    // almost final variables
    public static int NB_SLAVES;
    public static int MASTER;
    public static Integer MASTER_PORT = 10000;
    public static Integer SLAVE_PORT = MASTER_PORT + 1;
    public static String[] IPs;
    public static Integer ID;

    // variables that will be accessed by multiple threads
    private volatile static int dataReceived = 0;
    private volatile static ArrayList<Map.Entry<String, Integer>> wordList = new ArrayList<>();

    // will be written by only one thread
    private volatile static boolean readyForShuffle = false;
    private volatile static boolean readyForReduce = false;

    private static SlaveWorker worker;

    public static void main(String[] args) {
        // get the number of slaves
        NB_SLAVES = Integer.valueOf(args[0]);
        MASTER = NB_SLAVES;

        // get the id of the slave
        ID = Integer.valueOf(args[1]);

        if (args.length >= 3) {
            SLAVE_PORT = Integer.valueOf(args[2]);
            MASTER_PORT = SLAVE_PORT + 1;
        }

        // Start the listener
        Listener ml = new Listener();
        ml.start();

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

        // Start the slaves
        worker = new SlaveWorker(ID, IPs);
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
                readyForShuffle = true;
                worker.startShuffle();
            } else if (obj instanceof ReduceReady) {
                synchronized(this) { 
                    readyForReduce = true; 
                    worker.startReduce(wordList);
                }
            } else if (obj instanceof ShuffleData) {
                ShuffleData sd = (ShuffleData) obj;
                synchronized(this) {
                    wordList.add(sd.getEntry());
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
