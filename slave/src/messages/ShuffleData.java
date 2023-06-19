package src.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class ShuffleData extends Message implements Serializable {
    private ArrayList<Map.Entry<String, Integer>> data = new ArrayList<>();
    private int id;

    public ShuffleData(int id) {
        this.id = id;
    }

    public void addData(Map.Entry<String, Integer> wordCount) {
        this.data.add(wordCount);
    }

    public ArrayList<Map.Entry<String, Integer>> getEntries() {
        return this.data;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.data.size());
        out.writeObject(id);
        for (Map.Entry<String, Integer> entry: this.data) {
            out.writeObject(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.data = new ArrayList<>();
        int length = (int)in.readObject();
        this.id = (int)in.readObject();
        for (int i = 0; i < length; i++) {
            String key = (String)in.readObject();
            int value = (int)in.readObject();
            AbstractMap.SimpleEntry<String, Integer> entry = new AbstractMap.SimpleEntry<String,Integer>(key, value);
            this.data.add(entry);
        }
    }

    private void readObjectNoData() throws ObjectStreamException {
        this.id = -1;
    }
}
