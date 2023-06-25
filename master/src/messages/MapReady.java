package src.messages;

public class MapReady extends Message  {
    private int slaveId;

    public MapReady(int id) {
        slaveId = id;
    }

    public int getId() {
        return slaveId;
    }
}