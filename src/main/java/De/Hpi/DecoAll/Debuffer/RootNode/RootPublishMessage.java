package De.Hpi.DecoAll.Debuffer.RootNode;

import De.Hpi.DecoAll.Debuffer.Configure.Configuration;
import De.Hpi.DecoAll.Debuffer.Message.MessageToLocal;
import org.msgpack.MessagePack;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RootPublishMessage implements Runnable {

    private Configuration conf;
    private ConcurrentLinkedQueue<MessageToLocal> messageToLocalQueue;
    private ZMQ.Socket socketPub;
    public RootPublishMessage(Configuration conf, ConcurrentLinkedQueue<MessageToLocal> messageToLocalQueue, ZMQ.Socket socketPub) {
        this.conf = conf;
        this.messageToLocalQueue =messageToLocalQueue;
        this.socketPub = socketPub;
    }

    public void run() {
//        System.out.println("Starting FromRNodeToINode ----rootNode");
        int queryCounter = 0;
        MessagePack msgpack = new MessagePack();
        while (true) {
            if(!messageToLocalQueue.isEmpty()) {
                MessageToLocal messageToLocal = (MessageToLocal) messageToLocalQueue.poll();
                    try {
                        byte[] raw = msgpack.write(messageToLocal);
//                        System.out.println(raw.length);
                          socketPub.send(raw);
                        if(conf.DEBUGMODE_ROOT) {
//                            if(messageToLocal.getMessageType() == 1)
                            System.out.println("rootNode--" + conf.getNodeId() + "--sending message----"
                                    + "  nodeId:  " + messageToLocal.getNodeId()
                                    + "  Type:  " + messageToLocal.getMessageType()
//                                    + "  Query Id:  " + messageToLocal.query.getQueryId()
                            );
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                queryCounter++;
                //end thread
//                if(queryCounter >= conf.queryNumber)
//                    break;
            }
        }
    }
}

