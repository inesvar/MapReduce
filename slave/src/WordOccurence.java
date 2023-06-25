package src;
import java.io.*;
import java.util.*;

public class WordOccurence {
    
    public static ArrayList<Map.Entry<Long, ArrayList<String>>> groupOccurences(HashMap<String, Long> hashmap) throws IOException {
        
        long startTime = System.currentTimeMillis();
        HashMap<Long, ArrayList<String>> wordOccurence = new HashMap<>();

        for (Map.Entry<String, Long> entry : hashmap.entrySet()) { 
            ArrayList<String> words = wordOccurence.getOrDefault(entry.getValue(), new ArrayList<String>());
            words.add(entry.getKey());
            wordOccurence.put(entry.getValue(), words);
        }

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Grouping occurences total time : " + totalTime + "ms");
        
        ArrayList<Map.Entry<Long, ArrayList<String>>> entries = new ArrayList<>(wordOccurence.entrySet());

        return entries;
    }
}


