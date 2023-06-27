package src.messages;

import java.util.ArrayList;
import java.util.TreeMap;

public class ReduceResult extends Message {
    private TreeMap<Long, ArrayList<String>> wordOccurences;

    public ReduceResult(TreeMap<Long, ArrayList<String>> wordOccurences) {
        this.wordOccurences = wordOccurences;
    }

    public TreeMap<Long, ArrayList<String>> getWordOccurences() {
        return wordOccurences;
    }
}
