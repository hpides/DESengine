package De.Hpi.DecoAll.DecoAsy.Dao;

import java.util.ArrayList;

public class PartialResult {

    private int nodeId;
    public long count;
    public double result;
    public ArrayList<Tuple> bufferTupleListStart;
    public ArrayList<Tuple> bufferTupleListEnd;
    public int counterForStep;
    public double eventRate;

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

}
