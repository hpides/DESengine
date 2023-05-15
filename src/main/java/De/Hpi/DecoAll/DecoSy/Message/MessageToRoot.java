package De.Hpi.DecoAll.DecoSy.Message;

import org.msgpack.annotation.Message;

@Message
public class MessageToRoot {

    private int nodeId;
    //Type 1: query, Type, 2: event rate, 3: local window size, 4: Result
    private int messageType;
    public double eventRate;
    public int windowId;
    public long count;
    public double result;

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
