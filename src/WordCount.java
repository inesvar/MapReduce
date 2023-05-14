import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

public class WordCount {
    public static TreeMap<String, Integer> countWords(String filename) throws IOException {
        
        TreeMap<String, Integer> wordCount = new TreeMap<>();

        File inputFile = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] words = line.split("\\s+"); // splits by whitespaces
            for (String word : words) {
                if (wordCount.containsKey(word)) {
                    int count = wordCount.get(word);
                    wordCount.put(word, count + 1);
                } else {
                    wordCount.put(word, 1);
                }
            }
        }
        reader.close();
        return wordCount;
    }
}
