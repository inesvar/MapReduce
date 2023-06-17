package src.messages;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;

public class ShuffleData extends Message implements Serializable {
    private String key;
    private int value;
    private int id;

    public ShuffleData(int id, Map.Entry<String, Integer> wordCount) {
        this.id = id;
        this.key = wordCount.getKey();
        this.value = wordCount.getValue();
    }

    public Map.Entry<String, Integer> getEntry() {
        return new AbstractMap.SimpleEntry<>(key, value);
    }
}
