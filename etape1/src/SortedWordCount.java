package src;
import java.lang.Math;
import java.io.*;
import java.util.*;

public class SortedWordCount {
    static private List<Map.Entry<String, Integer>> entries;
    public static List<Map.Entry<String, Integer>> countWords(String filename) throws IOException {
        
        File inputFile = new File(filename);
        long fileLength = inputFile.length();
        System.out.println("the size of the file is " + fileLength);


        // je me base sur la régression suivante : https://docs.google.com/spreadsheets/d/1sRl5x2Gj2o79t6C_cjSijqdmXbPOs-jOEo7ymaTNo2A/edit?usp=sharing
        int estimatedSize = (int)Math.exp(Math.log(fileLength) * 0.8313674369 -1.135090197);   

        // the estimated size of the hashmap is 3 to 4 million key value mappings for a CC file
        // the initial capacity should be equal to the size of the hashmap divided by the load factor (0.75 by default)
        // so I use an initial capacity of 5 million
        HashMap<String, Integer> wordCount = new HashMap<>((int) (estimatedSize / 0.75));

        long startTime = System.currentTimeMillis();

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+"); // splits by whitespaces
                for (String word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    wordCount.merge(word, 1, Integer::sum);
                }
            }
        }

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Counting total time : " + totalTime + "ms");
        System.out.println("Total size of the hashmap : " + wordCount.size());
        startTime   = System.currentTimeMillis();

        SortedWordCount.entries = new ArrayList<>(wordCount.entrySet());
        SortedWordCount.comparingByValueThenKey(entries);

        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println("Ordering total time : " + totalTime + "ms");

        return entries;
    }

    private static <K extends Comparable<K>, V extends Comparable<V>> void comparingByValueThenKey(
        List<Map.Entry<String, Integer>> entries) {

        entries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()) == 0
                                ? o1.getKey().compareTo(o2.getKey())
                                : o2.getValue().compareTo(o1.getValue()));
    }
}


