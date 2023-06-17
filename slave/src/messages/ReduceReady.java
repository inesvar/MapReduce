package src.messages;

import java.io.Serializable;

public class ReduceReady extends Message implements Serializable {
    private int slaveId;

    public ReduceReady(int id) {
        slaveId = id;
    }

    public int getId() {
        return slaveId;
    }
}