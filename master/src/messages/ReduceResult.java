package src.messages;

import java.io.Serializable;
import java.util.HashMap;

public class ReduceResult extends Message implements Serializable {
    private HashMap<String, Integer> wordCount;
    private int id;

    public ReduceResult(int id, HashMap<String, Integer> wordCount) {
        this.id = id;
        this.wordCount = wordCount;
    }

    public HashMap<String, Integer> getWordCount() {
        return wordCount;
    }
}
