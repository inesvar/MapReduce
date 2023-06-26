package src;
import static src.SlaveManager.*;

import src.messages.ShuffleWordCount;
import src.messages.ShuffleWordOccurences;
import src.messages.ShuffleReady;
import src.messages.MapReady;
import src.messages.ReduceResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
// import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

// This class does the MAP, the SHUFFLE, notifies the Master
public class SlaveWorker extends Thread {
    private Integer id;
    private ArrayList<String> fileInput = new ArrayList<>();
    private HashMap<String, Long> wordCountHashmap = new HashMap<>();
    private TreeMap<Long, ArrayList<String>> wordOccurencesHashmap = new TreeMap<>();
    private ArrayList<Map.Entry<String, Long>> entryList;
    private ArrayList<Map.Entry<Long, ArrayList<String>>> occurencesList;

    public SlaveWorker(Integer id, String[] IPs, ArrayList<String> fileInput) {
        this.id = id;
        // construct the file name of the text that is going to be mapped
        this.fileInput = fileInput;
    }

    public synchronized void startReduceWordCount(ArrayList<Map.Entry<String, Long>> entryList) {
        this.entryList = entryList;
        this.notify();
    }

    public synchronized void startReduceWordOccurences(ArrayList<Map.Entry<Long, ArrayList<String>>> occurencesList) {
        this.occurencesList = occurencesList;
        this.notify();
    }

    public synchronized void startShuffle() {
        this.notify();
    }

    public synchronized void startMap() {
        this.notify();
        System.out.println("worker "+id.toString()+" is notified to start map");
    }

    public synchronized void run() {
        try {
            // NOTIFY THE MASTER
            System.out.println(id.toString()+" trying to connect to " + IP[MASTER] + " port " + PORT[MASTER].toString());
            Sender senderSM = new Sender(MASTER, new MapReady(id));
            senderSM.start();

            System.out.println("worker "+id.toString()+" waiting for map");
            this.wait();
            System.out.println("worker "+id.toString()+" starting map");

            // MAPPING
            ArrayList<Map.Entry<String, Long>> entries = WordCount.countWords(fileInput);
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
            ShuffleWordCount[] data = new ShuffleWordCount[NB_SLAVES];
            for (int i = 0; i < NB_SLAVES; i++) {
                data[i] = new ShuffleWordCount(id);
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
                long c = wordCountHashmap.getOrDefault(word.getKey(), (long)0);
                wordCountHashmap.put(word.getKey(), count + c);
            }

            // NOTIFY THE MASTER
            System.out.println("trying to connect to " + IP[MASTER] + " port " + PORT[MASTER].toString());
            senderSM = new Sender(MASTER, new MapReady(id));
            senderSM.start();

            wait();
            System.out.println("worker "+id.toString()+" starting second map");

            // 2ND MAPPING
            ArrayList<Map.Entry<Long, ArrayList<String>>> entries2 = WordOccurence.groupOccurences(wordCountHashmap);
            System.out.println();
            for (int i = 0; i < 2; i++) {
                System.out.println(entries.get(i).toString());
            }

            // NOTIFY THE MASTER
            System.out.println(id.toString()+"trying to connect to " + IP[MASTER] + " port " + PORT[MASTER].toString());
            senderSR = new Sender(MASTER, new ShuffleReady(id));
            senderSR.start();

            wait();
            System.out.println("worker "+id.toString()+" starting shuffle");

            // 2ND SHUFFLE
            ShuffleWordOccurences[] data2 = new ShuffleWordOccurences[NB_SLAVES];
            for (int i = 0; i < NB_SLAVES; i++) {
                data2[i] = new ShuffleWordOccurences(id);
            }
            for (Map.Entry<Long, ArrayList<String>> entry: entries2) {
                int slave = entry.getKey().hashCode()%NB_SLAVES + NB_SLAVES;
                data2[slave%NB_SLAVES].addData(entry);
            }
            for (int i = 0; i < NB_SLAVES; i++) {
                Sender sender = new Sender(SLAVES[i], data2[i]);
                sender.start();
            }

            wait();
            System.out.println("worker "+id.toString()+" starting reduce");
            
            // 2ND REDUCE
            ArrayList<String> emptyList = new ArrayList<>();
            for (Map.Entry<Long, ArrayList<String>> entry : occurencesList) {
                ArrayList<String> entry_words = entry.getValue();
                ArrayList<String> stored_words = wordOccurencesHashmap.getOrDefault(entry.getKey(), emptyList);
                entry_words.addAll(stored_words);
                wordOccurencesHashmap.put(entry.getKey(), entry_words);
            }

            // NOTIFY THE MASTER
            System.out.println("trying to send results to " + IP[MASTER] + " port " + PORT[MASTER].toString());
            Sender senderRR = new Sender(MASTER, new ReduceResult(id, wordOccurencesHashmap));
            senderRR.start();

            System.out.println("worker "+id.toString()+" finished");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalMonitorStateException e) {
            e.printStackTrace();
        }
    }
}