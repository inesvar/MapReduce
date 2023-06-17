package src;
import static src.Manager.*;

import src.messages.ShuffleData;
import src.messages.ReduceReady;
import src.messages.ShuffleReady;
import src.messages.ReduceResult;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class does the MAP, the SHUFFLE, notifies the Master
public class SlaveWorker extends Thread {
    private Integer id;
    private String fileInput;
    private HashMap<String, Integer> hashmap;
    private ArrayList<Map.Entry<String, Integer>> entryList;

    public SlaveWorker(Integer id, String[] IPs) {
        this.id = id;
        // construct the file name of the text that is going to be mapped
        this.fileInput = "S"+id.toString()+".txt";
    }

    public synchronized void startReduce(ArrayList<Map.Entry<String, Integer>> entryList) {
        this.entryList = entryList;
        this.notify();
    }

    public synchronized void startShuffle() {
        this.notify();
    }

    public synchronized void run() {
        try {

            // MAPPING
            List<Map.Entry<String, Integer>> entries = SortedWordCount.countWords(fileInput);
            System.out.println();
            for (int i = 0; i < 3; i++) {
                System.out.println(entries.get(i).toString());
            }

            // NOTIFY THE MASTER
            System.out.println("trying to connect to " + IPs[MASTER] + " port " + MASTER_PORT.toString());
            Sender senderSR = new Sender(IPs[MASTER], MASTER_PORT, new ShuffleReady(id));
            senderSR.start();

            wait();
            System.out.println("worker "+id.toString()+" starting shuffle");

            // SHUFFLE
            for (Map.Entry<String, Integer> entry: entries) {
                int slave = entry.getKey().hashCode()%NB_SLAVES;
                Sender senderS = new Sender(IPs[slave], SLAVE_PORT, new ShuffleData(id, entry));
                senderS.start();
            }

            // NOTIFY THE MASTER
            System.out.println("trying to connect to " + IPs[MASTER] + " port " + MASTER_PORT.toString());
            Sender senderRR = new Sender(IPs[MASTER], MASTER_PORT, new ReduceReady(id));
            senderRR.start();

            wait();
            System.out.println("worker "+id.toString()+" starting reduce");
            System.out.println("worker "+id.toString()+" mappedData : "+entryList.toString());

            //REDUCE
            hashmap = new HashMap<>();
            for (Map.Entry<String, Integer> word : entryList) {
                Integer count = Integer.valueOf(word.getValue());
                int c = hashmap.getOrDefault(word, 0);
                hashmap.put(word.getKey(), count + c);
            }

            // SEND THE RESULTS TO THE MASTER
            Sender sender = new Sender(IPs[MASTER], MASTER_PORT, new ReduceResult(id, hashmap));
            sender.start();
            System.out.println("worker "+id.toString()+" sent results to master");
            Thread.sleep(1000);
            System.exit(0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalMonitorStateException e) {
            e.printStackTrace();
        }
    }
}