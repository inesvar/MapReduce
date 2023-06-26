package src;
import java.io.*;
import java.util.*;

public class WordCount {
    
    public static ArrayList<Map.Entry<String, Long>> countWords(ArrayList<String> filenames) throws IOException {
        long startTime = System.currentTimeMillis();

        HashMap<String, Long> wordCount = new HashMap<>();

        for (String filename : filenames) {
            System.out.println("Reading from file "+filename);
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
        }

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Counting total time : " + totalTime + "ms");

        ArrayList<Map.Entry<String, Long>> entries = new ArrayList<>(wordCount.entrySet());

        return entries;
    }
}


