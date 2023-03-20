package De.Hpi.DesisCen.Message;


import De.Hpi.DesisCen.Dao.Window;
import org.msgpack.annotation.Message;

@Message
public class MessageResult {

    private int nodeId;
    private int messageType;
    private int messageLevel;
    public Window window;


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
