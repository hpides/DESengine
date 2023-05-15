package De.Hpi.DecoAll.Debuffer.Message;

import De.Hpi.DecoAll.Debuffer.Dao.Query;
import org.msgpack.annotation.Message;

@Message
public class MessageToLocal {

    private int nodeId;
    //Type 1: query, Type, 2: event rate, 3: local window size, 4: Result
    private int messageType;
    public Query query;
    public double localWindowSize;

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
