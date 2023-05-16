package De.Hpi.DecoAll.DecoAsy.Message;

import De.Hpi.DecoAll.DecoAsy.Dao.Tuple;
import org.msgpack.annotation.Message;

import java.util.ArrayList;

@Message
public class MessageToRoot {

    private int nodeId;
    //Type 1: query, Type, 2: event rate, 3: local window size, 4: Result, 5 result and event rate, 6 correction
    private int messageType;
    public double eventRate;
    public int windowId;
    public long count;
    public double result;
    public ArrayList<Tuple> bufferTupleList;

    public int getNodeId() {
        return nodeId;
    }
    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }
    public int getMessageType() {
        return messageType;
    }
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

}
