package src.messages;

public class ShuffleReady extends Message  {
    private int slaveId;

    public ShuffleReady(int id) {
        slaveId = id;
    }

    public int getId() {
        return slaveId;
    }
}