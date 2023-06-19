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
/*     private ArrayList<String> key = new ArrayList<>();
    private ArrayList<Integer> value = new ArrayList<>();
    private int length = 0; */
    private ArrayList<Map.Entry<String, Integer>> data = new ArrayList<>();
    private int id;

    public ShuffleData(int id) {
        this.id = id;
        /* this.key.add(wordCount.getKey());
        this.value.add(wordCount.getValue());
        length++; */
    }

    public void addData(Map.Entry<String, Integer> wordCount) {
       /*  this.key.add(wordCount.getKey());
        this.value.add(wordCount.getValue());
        length++; */
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
        System.out.println("length : "+ length);
        this.id = (int)in.readObject();
        System.out.println("id : "+ id);
        for (int i = 0; i < length; i++) {
            System.out.println("start of loop"+i);
            String key = (String)in.readObject();
            System.out.println("key : "+ key);
            int value = (int)in.readObject();
            System.out.println("value : "+ value);
            AbstractMap.SimpleEntry<String, Integer> entry = new AbstractMap.SimpleEntry<String,Integer>(key, value);
            this.data.add(entry);
        }
        System.out.println("ended");
    }

    private void readObjectNoData() throws ObjectStreamException {
        this.id = -1;
    }
}
