package De.Hpi.DesisCen.Message;

import De.Hpi.DesisCen.Dao.Query;
import org.msgpack.annotation.Message;

@Message
public class MessageQuery {

    private int nodeId;
    private int messageType;
    private int messageLevel;
    public Query query;

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
    public int getMessageLevel() {
        return messageLevel;
    }
    public void setMessageLevel(int messageLevel) {
        this.messageLevel = messageLevel;
    }

}
