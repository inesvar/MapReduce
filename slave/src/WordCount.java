package src;
import java.io.*;
import java.util.*;

public class WordCount {
    private static ArrayList<Map.Entry<String, Long>> entries;

    public static ArrayList<Map.Entry<String, Long>> countWords(String filename) throws IOException {
        long startTime = System.currentTimeMillis();

        HashMap<String, Long> wordCount = new HashMap<>();

        File inputFile = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] words = line.split("\\s+"); // splits by whitespaces
            for (String word : words) {
                if (word.isEmpty()) {
                    continue;
                }
                word = word.toLowerCase();
                if (wordCount.containsKey(word)) {
                    long count = wordCount.get(word);
                    wordCount.put(word, count + 1);
                } else {
                    wordCount.put(word, (long)1);
                }
            }
        }
        reader.close();

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Counting total time : " + totalTime + "ms");

        startTime   = System.currentTimeMillis();

        WordCount.entries = new ArrayList<>(wordCount.entrySet());

        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println("Ordering total time : " + totalTime + "ms");

        return entries;
    }
}


