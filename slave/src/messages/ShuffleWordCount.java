package src.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class ShuffleWordCount extends Message  {
    private ArrayList<Map.Entry<String, Long>> data = new ArrayList<>();
    private int id;

    public ShuffleWordCount(int id) {
        this.id = id;
    }

    public void addData(Map.Entry<String, Long> wordCount) {
        this.data.add(wordCount);
    }

    public ArrayList<Map.Entry<String, Long>> getEntries() {
        return this.data;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.data.size());
        out.writeObject(id);
        for (Map.Entry<String, Long> entry: this.data) {
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
            long value = (long)in.readObject();
            AbstractMap.SimpleEntry<String, Long> entry = new AbstractMap.SimpleEntry<String, Long>(key, value);
            this.data.add(entry);
        }
    }

    private void readObjectNoData() throws ObjectStreamException {
        this.id = -1;
    }
}
