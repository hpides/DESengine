package De.Hpi.DecoAll.DecoSy.Message;

import De.Hpi.DecoAll.DecoSy.Dao.Query;
import org.msgpack.annotation.Message;

@Message
public class MessageToLocal {

    private int nodeId;
    //Type 1: query, Type, 2: event rate, 3: local window size, 4: Result, 5 result and event rate, 6 correction
    private int messageType;
    public Query query;
    public double localWindowSize;
    public double correctionFactor;

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
