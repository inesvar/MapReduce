package src;
import static src.Manager.*;

import src.messages.ShuffleData;
import src.messages.ShuffleReady;
import src.messages.ReduceResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

// This class does the MAP, the SHUFFLE, notifies the Master
public class SlaveWorker extends Thread {
    private Integer id;
    private String fileInput;
    private HashMap<String, Long> hashmap = new HashMap<>();
    private ArrayList<Map.Entry<String, Long>> entryList;

    public SlaveWorker(Integer id, String[] IPs, String fileInput) {
        this.id = id;
        // construct the file name of the text that is going to be mapped
        this.fileInput = fileInput;
    }

    public synchronized void startReduce(ArrayList<Map.Entry<String, Long>> entryList) {
        this.entryList = entryList;
        this.notify();
    }

    public synchronized void startShuffle() {
        this.notify();
    }

    public synchronized void run() {
        try {
            // MAPPING
            ArrayList<Map.Entry<String, Long>> entries = SortedWordCount.countWords(fileInput);
            System.out.println();
            for (int i = 0; i < 2; i++) {
                System.out.println(entries.get(i).toString());
            }

            // NOTIFY THE MASTER
            System.out.println("trying to connect to " + IP[MASTER] + " port " + PORT[MASTER].toString());
            Sender senderSR = new Sender(MASTER, new ShuffleReady(id));
            senderSR.start();

            wait();
            System.out.println("worker "+id.toString()+" starting shuffle");

            // SHUFFLE
            ShuffleData[] data = new ShuffleData[NB_SLAVES];
            for (int i = 0; i < NB_SLAVES; i++) {
                data[i] = new ShuffleData(id);
            }
            for (Map.Entry<String, Long> entry: entries) {
                int slave = entry.getKey().hashCode()%NB_SLAVES + NB_SLAVES;
                data[slave%NB_SLAVES].addData(entry);
            }
            for (int i = 0; i < NB_SLAVES; i++) {
                Sender sender = new Sender(SLAVES[i], data[i]);
                sender.start();
            }

            wait();
            System.out.println("worker "+id.toString()+" starting reduce");

            //REDUCE
            for (Map.Entry<String, Long> word : entryList) {
                Long count = Long.valueOf(word.getValue());
                long c = hashmap.getOrDefault(word.getKey(), (long)0);
                hashmap.put(word.getKey(), count + c);
            }


            // SEND THE RESULTS TO THE MASTER
            /* Sender sender = new Sender(MASTER, new ReduceResult(id, hashmap));
            sender.start(); */
            System.out.println("worker "+id.toString()+" terminated");
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