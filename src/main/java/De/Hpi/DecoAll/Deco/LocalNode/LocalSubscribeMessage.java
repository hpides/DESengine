package De.Hpi.DecoAll.Deco.LocalNode;

import De.Hpi.DecoAll.Deco.Configure.Configuration;
import De.Hpi.DecoAll.Deco.Message.MessageToLocal;
import org.msgpack.MessagePack;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LocalSubscribeMessage implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<MessageToLocal> messageToLocalQueue;
    private ZMQ.Socket socketSub;
//    private ConcurrentHashMap<Integer, LocalTask> taskQueue;

    public LocalSubscribeMessage(Configuration conf, ConcurrentLinkedQueue<MessageToLocal> messageToLocalQueue, ZMQ.Socket socketSub) {
        this.conf = conf;
        this.socketSub = socketSub;
        this.messageToLocalQueue = messageToLocalQueue;
    }

    public void run() {
        int queryCounter = 0;
//        System.out.println("Starting ResponseThread ----localNode");
        MessagePack msgpack = new MessagePack();
        socketSub.subscribe("".getBytes());
        while (true) {
            try {
                byte[] raw = socketSub.recv(1);
                if(raw!=null) {
                    //start generator
                    MessageToLocal messageToLocal = msgpack.read(raw,
                            MessageToLocal.class);
                    if(messageToLocal.getNodeId() == 0 || messageToLocal.getNodeId() == conf.getNodeId()) {
                        messageToLocalQueue.offer(messageToLocal);
                        if (conf.DEBUGMODE_LOCAL) {
                            System.out.println("localNode--" + conf.getNodeId() + "--receiving message----"
                                    + "  nodeId:  " + messageToLocal.getNodeId()
                                    + "  Type:  " + messageToLocal.getMessageType()
                                    + "  LocalWindowSize:  " + messageToLocal.localWindowSize
                            );
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
