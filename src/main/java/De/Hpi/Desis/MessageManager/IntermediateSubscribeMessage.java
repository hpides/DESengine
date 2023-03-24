package De.Hpi.Desis.MessageManager;

import De.Hpi.Desis.Configure.Configuration;
import De.Hpi.Desis.Dao.Window;
import De.Hpi.Desis.Dao.WindowCollection;
import De.Hpi.Desis.Message.MessageResult;
import org.msgpack.MessagePack;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IntermediateSubscribeMessage implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<WindowCollection> resultQueueFromLocal;
    private ZMQ.Socket socketSub;
    private long counter;

    public IntermediateSubscribeMessage(ConcurrentLinkedQueue<WindowCollection> resultQueueFromLocal
            , Configuration conf, ZMQ.Socket socketSub) {
        this.resultQueueFromLocal =resultQueueFromLocal;
        this.socketSub = socketSub;
        this.conf = conf;
        this.counter = counter;
    }

    public void run() {
//        System.out.println("Starting LowerResponseThread ----intermediaNode");
        MessagePack msgpack = new MessagePack();
        socketSub.subscribe("".getBytes());
        long tupleCounter = 0;
        long networkOverheadL = 0;
        long networkOverheadI = 0;
        long begintime = System.currentTimeMillis();
        long endtime = System.currentTimeMillis();

        //to test network latency
//        long counterL = 0l;
//        long networklatency = 0l;

        while (true) {
            try {
                if(resultQueueFromLocal.size() < conf.DATAGENERATORMAXIMIUMBUFFER) {
                    byte[] raw = socketSub.recv(1);
                    if(raw!=null) {
                        MessageResult messageResult = msgpack.read(raw,
                                MessageResult.class);
//                        counterL++;
//                        networklatency += (System.currentTimeMillis() - messageResult.windowCollection.windowList.get(0).count);
                        //to test network latency
                        resultQueueFromLocal.offer(messageResult.windowCollection);
                        if(conf.DEBUGMODE_INTER) {
                            if(tupleCounter == 0){
                                tupleCounter++;
                                networkOverheadL = getNetworkOverheadL(raw.length);
                                networkOverheadI = getNetworkOverheadI(raw.length);
                                begintime = System.currentTimeMillis();
                                endtime = System.currentTimeMillis();
                                continue;
                            }
                            tupleCounter++;
                            networkOverheadL+=getNetworkOverheadL(raw.length);
                            networkOverheadI+=getNetworkOverheadI(raw.length);
                            if (System.currentTimeMillis() - endtime > conf.BenchMarkOutputFrequency) {
                                endtime = System.currentTimeMillis();
                                System.out.println("InterNode--" + conf.getNodeId() + "--INFO"
                                        + "  Throughput:  " + tupleCounter / ((endtime - begintime) / 1000.0)
                                        + "  BandWidth(Inter):  " + networkOverheadI  / ((endtime - begintime) / 1000.0)
                                        + "  BandWidth(Local):  " + networkOverheadL  / ((endtime - begintime) / 1000.0)
                                        + "  Allcounter:  " + tupleCounter
                                        + "  counter:  " + (tupleCounter - counter)
                                        + "  NetworkOverhead(Inter):  " + networkOverheadI
                                        + "  NetworkOverhead(Local):  " + networkOverheadL
                                        + "  Time:  " + (endtime - begintime) / 1000.0
                                        + "  GCTime:  " + getGarbageCollectionTime()
                                        + "  GC/Time-Ratio:  " + (double) getGarbageCollectionTime() / (endtime - begintime)
                                        + "  Queue:  " + resultQueueFromLocal.size()
//                                        + "  NetworkLatency:  " + (System.currentTimeMillis() - messageResult.windowCollection.windowList.get(0).count)
//                                        + "  AverageNetworkLatency:  " + networklatency/counterL
                                );
                                counter = tupleCounter;
                            }
                        }

                    }
                }else {
//                    System.out.println("WARNING!!!!:  " + resultQueueFromLocal.size());
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
    private static long getNetworkOverheadL(int rawSize) {
        return (rawSize / (9000 - 46) + 1) * (46) + (45 + 45) + rawSize;
    }

    private static long getNetworkOverheadI(int rawSize) {
        return (rawSize / (9000 - 46) + 1) * (44) + (44 + 44);
    }
}
