package De.Hpi.DesisCen.RootNode;

import De.Hpi.DesisCen.Configure.Configuration;
import De.Hpi.DesisCen.Dao.Tuple;
import De.Hpi.DesisCen.Message.MessageTuple;
import org.msgpack.MessagePack;
import org.zeromq.ZMQ;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BaselineCentralReceiver implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;
    private ArrayList<Tuple> tupleList;
    private ZMQ.Socket socketSub;

    public BaselineCentralReceiver(ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue,
                                   Configuration conf, ZMQ.Socket socketSub) {
        this.conf =conf;
        this.dataQueue =dataQueue;
        this.tupleList = new ArrayList<>();
        this.socketSub = socketSub;
    }

    public void run() {
        MessagePack msgpack = new MessagePack();
        socketSub.subscribe("".getBytes());
        long networkOverhead = 0;
        long tupleCounter = 0;
        long begintime = System.currentTimeMillis();
        long endtime = System.currentTimeMillis();

        while (true) {
            try {
                if(dataQueue.size() < conf.DATAGENERATORMAXIMIUMBUFFER) {
                    byte[] raw = socketSub.recv(1);
                    if(raw!=null) {
                        MessageTuple messageTuple = msgpack.read(raw,
                                MessageTuple.class);
//                        dataQueue.add(messageTuple.tupleLinkedList);
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
//                                        + "  GCTime:  " + getGarbageCollectionTime()
//                                        + "  GC/Time-Ratio:  " + (double) getGarbageCollectionTime() / (endtime - begintime)
                                        + "  QueueSize:  " + dataQueue.size()
                                );
                            }
                        }
                     }
                }else {
//                    System.out.println("WARNING!!!!:  " + dataQueue.size());
                    Thread.sleep(conf.DATAGENERATORFREQUENCY);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
//        return rawSize;
        return (rawSize / (9000 - 46) + 1) * (46 + 44) + (45 + 45 + 44 + 44) + rawSize;
    }
}
