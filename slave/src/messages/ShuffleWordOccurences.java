package src.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class ShuffleWordOccurences extends Message  {
    private ArrayList<Map.Entry<Long, ArrayList<String>>> data = new ArrayList<>();
    private int id;

    public ShuffleWordOccurences(int id) {
        this.id = id;
    }

    public void addData(Map.Entry<Long, ArrayList<String>> wordCount) {
        this.data.add(wordCount);
    }

    public ArrayList<Map.Entry<Long, ArrayList<String>>> getEntries() {
        return this.data;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.data.size());
        out.writeObject(id);
        for (Map.Entry<Long, ArrayList<String>> entry: this.data) {
            out.writeObject(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.data = new ArrayList<>();
        int length = (int)in.readObject();
        this.id = (int)in.readObject();
        for (int i = 0; i < length; i++) {
            long key = (long)in.readObject();
            ArrayList<String> value = (ArrayList<String>)in.readObject();
            AbstractMap.SimpleEntry<Long, ArrayList<String>> entry = new AbstractMap.SimpleEntry<Long, ArrayList<String>>(key, value);
            this.data.add(entry);
        }
    }

    private void readObjectNoData() throws ObjectStreamException {
        this.id = -1;
    }
}
