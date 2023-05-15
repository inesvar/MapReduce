import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Map.Entry<String, Integer>> entries = SortedWordCount.countWords(args[0]);
        System.out.println();
        for (int i = 0; i < 15; i++) {
            System.out.println(entries.get(i));
        }
    }
}