package De.Hpi.Desis.Message;

import De.Hpi.Desis.Dao.Tuple;
import org.msgpack.annotation.Message;

import java.util.ArrayList;

@Message
public class MessageTuple {

    private int nodeId;
    private int messageType;
    private int messageLevel;
    public ArrayList<Tuple> tupleLinkedList;


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
