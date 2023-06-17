package src.messages;

import java.io.Serializable;

public class ShuffleReady extends Message implements Serializable {
    private int slaveId;

    public ShuffleReady(int id) {
        slaveId = id;
    }

    public int getId() {
        return slaveId;
    }
}