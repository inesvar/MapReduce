package src.messages;

public class ReduceReady extends Message  {
    private int slaveId;

    public ReduceReady(int id) {
        slaveId = id;
    }

    public int getId() {
        return slaveId;
    }
}