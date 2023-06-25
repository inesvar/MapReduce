package src.messages;

import java.util.ArrayList;
import java.util.TreeMap;

public class ReduceResult extends Message  {
    private TreeMap<Long, ArrayList<String>> wordOccurences;
    private int id;

    public ReduceResult(int id, TreeMap<Long, ArrayList<String>> wordOccurences) {
        this.id = id;
        this.wordOccurences = wordOccurences;
    }

    public TreeMap<Long, ArrayList<String>> getWordOccurences() {
        return wordOccurences;
    }

    public int getId() {
        return id;
    }
}
