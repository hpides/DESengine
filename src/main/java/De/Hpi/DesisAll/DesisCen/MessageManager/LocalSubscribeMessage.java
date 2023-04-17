package De.Hpi.DesisAll.DesisCen.MessageManager;

import De.Hpi.DesisAll.DesisCen.Configure.Configuration;
import De.Hpi.DesisAll.DesisCen.Dao.Query;
import De.Hpi.DesisAll.DesisCen.Message.MessageQuery;
import org.msgpack.MessagePack;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LocalSubscribeMessage implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private ZMQ.Socket socketSub;
//    private ConcurrentHashMap<Integer, LocalTask> taskQueue;

    public LocalSubscribeMessage(Configuration conf, ConcurrentLinkedQueue<Query> queryQueue, ZMQ.Socket socketSub) {
        this.conf = conf;
        this.socketSub = socketSub;
        this.queryQueue = queryQueue;
    }

    public void run() {
//        System.out.println("Starting ResponseThread ----localNode");
        MessagePack msgpack = new MessagePack();
        socketSub.subscribe("".getBytes());
        while (true) {
            try {
                byte[] raw = socketSub.recv(0);
                MessageQuery messageQuery = msgpack.read(raw,
                        MessageQuery.class);
                queryQueue.offer(messageQuery.query);
                if(conf.DEBUGMODE) {
                    System.out.println("localNode--" + conf.getNodeId() + "--receive----" + messageQuery.query.getQueryId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
