package De.Hpi.DesisAll.DesisMultipleKeys.MessageManager;

import De.Hpi.DesisAll.DesisMultipleKeys.Dao.Query;
import De.Hpi.DesisAll.DesisMultipleKeys.Configure.Configuration;
import De.Hpi.DesisAll.DesisMultipleKeys.Message.MessageQuery;
import org.msgpack.MessagePack;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IntermediateSubscribeAndPublishMessage implements Runnable {

    private Thread t;

    private ZMQ.Socket socketUpperSub;
    private ZMQ.Socket socketLowerPub;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private Configuration conf;

    public IntermediateSubscribeAndPublishMessage(ZMQ.Socket socketUpperSub, ZMQ.Socket socketLowerPub,
                                                  ConcurrentLinkedQueue<Query> queryQueue,
                                                  Configuration conf) {
        this.queryQueue = queryQueue;
        this.socketUpperSub = socketUpperSub;
        this.socketLowerPub = socketLowerPub;
        this.conf = conf;
    }

    public void run() {
        int queryCounter = 0;
        MessagePack msgpack = new MessagePack();
        socketUpperSub.subscribe("".getBytes());
        while (true) {
            byte[] raw = socketUpperSub.recv(1);
            if(raw!=null) {
                try {
                    MessageQuery messageQuery = msgpack.read(raw,
                            MessageQuery.class);
                    queryQueue.offer(messageQuery.query);
                    socketLowerPub.send(raw);
                    if(conf.DEBUGMODE_INTER) {
                        System.out.println("inteNode--" + conf.getNodeId() + "--transfer----" + messageQuery.query.getQueryId());
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
