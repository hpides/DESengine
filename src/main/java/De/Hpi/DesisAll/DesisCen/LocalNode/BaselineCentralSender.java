package De.Hpi.DesisAll.DesisCen.LocalNode;

import De.Hpi.DesisAll.DesisCen.Configure.Configuration;
import De.Hpi.DesisAll.DesisCen.Dao.Tuple;
import De.Hpi.DesisAll.DesisCen.Message.MessageTuple;
import org.msgpack.MessagePack;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BaselineCentralSender implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;
    private ArrayList<Tuple> tupleList;
    private ZMQ.Socket socketPub;

    public BaselineCentralSender(ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue, Configuration conf) {
        this.conf =conf;
        this.dataQueue =dataQueue;
        this.tupleList = new ArrayList<Tuple>();
        ZContext context = new ZContext();
        socketPub = context.createSocket(SocketType.PUB);
        socketPub.bind(new LocalParseAddress().getLocalPubAddress(conf, conf.getNodeId()));
    }

    public void run() {
//        System.out.println("Starting RequestThread ----localNode");
        MessagePack msgpack = new MessagePack();
        long tupleCounter = 0;
        long networkOverhead = 0;
        long begintime = System.currentTimeMillis();
        long endtime = System.currentTimeMillis();
        while (true) {
            if(!dataQueue.isEmpty()) {
                //control speed
                tupleList = (ArrayList<Tuple>) dataQueue.poll();
                MessageTuple messageTuple = new MessageTuple();
                messageTuple.setNodeId(conf.getNodeId());
                messageTuple.setMessageType(conf.MESSAGERESULT);
                messageTuple.setMessageLevel(conf.INTERMEDIATENODEMESSAGE);
                messageTuple.tupleLinkedList = tupleList;

                if((tupleCounter / ((System.currentTimeMillis() - begintime + 1) / 1000.0)) > conf.SendingSpeed){
                    continue;
                }

                try {
                    byte[] raw = msgpack.write(messageTuple);
                    socketPub.send(raw);
                    if (conf.DEBUGMODE) {
                        tupleCounter+=messageTuple.tupleLinkedList.size();
                        networkOverhead+=getNetworkOverhead(raw.length);
                        if (System.currentTimeMillis() - endtime > conf.BenchMarkOutputFrequency) {
                            endtime = System.currentTimeMillis();
                            System.out.println("INFO--"
                                    + "Throughput:  " + tupleCounter / ((endtime - begintime) / 1000.0)
                                    + "  BandWidth(B):  " + networkOverhead  / ((endtime - begintime) / 1000.0)
                                    + "  Allcounter:  " + tupleCounter
                                    + "  NetworkOverhead(B):  " + networkOverhead
                                    + "  Time:  " + (endtime - begintime) / 1000.0
//                                    + "  GCTime:  " + getGarbageCollectionTime()
//                                    + "  GC/Time-Ratio:  " + (double) getGarbageCollectionTime() / (endtime - begintime)
                            );
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static long getGarbageCollectionTime() {
        long collectionTime = 0;
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            collectionTime += garbageCollectorMXBean.getCollectionTime();
        }
        return collectionTime;
    }
    private static long getNetworkOverhead(int rawSize) {
        return (rawSize / (9000 - 46) + 1) * 46 + (45 + 45) + rawSize;
    }

}
