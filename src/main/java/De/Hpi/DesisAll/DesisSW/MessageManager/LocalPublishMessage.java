package De.Hpi.DesisAll.DesisSW.MessageManager;

import De.Hpi.DesisAll.DesisSW.Configure.Configuration;
import De.Hpi.DesisAll.DesisSW.Dao.WindowCollection;
import De.Hpi.DesisAll.DesisSW.Message.MessageResult;
import org.msgpack.MessagePack;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LocalPublishMessage implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<WindowCollection> intermediateResultQueue;
    private ZMQ.Socket socketPub;

    public LocalPublishMessage(ConcurrentLinkedQueue<WindowCollection> intermediateResultQueue, ZMQ.Socket socketPub, Configuration conf) {
        this.conf =conf;
        this.intermediateResultQueue =intermediateResultQueue;
        this.socketPub = socketPub;
    }

    public void run() {
//        System.out.println("Starting RequestThread ----localNode");
        MessagePack msgpack = new MessagePack();

        int[] infoList = new int[1000];

        float infoCounter = 0;
        int infoId = 0;
        int infoAll = 0;

//        Long networkOverhead = 0l;
//        long begintime = System.currentTimeMillis();
        long endtime = System.currentTimeMillis();
        while (true) {
            if(!intermediateResultQueue.isEmpty()) {
                WindowCollection windowCollection = (WindowCollection) intermediateResultQueue.poll();

                if(windowCollection.nodeId == 0 )
                    continue;
                infoId = windowCollection.nodeId;
                infoAll = windowCollection.sliceId;
                infoList[infoId-1] = infoList[infoId-1] + infoAll;
                infoCounter++;

                windowCollection.nodeId = conf.getNodeId();
                //the message type now it the data
                MessageResult messageResult = new MessageResult();
                messageResult.setNodeId(conf.getNodeId());
                messageResult.setMessageType(conf.MESSAGERESULT);
                messageResult.setMessageLevel(conf.LOCALNODEMESSAGE);
                messageResult.windowCollection = windowCollection;
                try {
                    byte[] raw = msgpack.write(messageResult);
//                    socketPub.send(raw);
                    if(messageResult.windowCollection.tuples == null)
                        messageResult.windowCollection.tuples = new ArrayList<>();
                    if(conf.DEBUGMODE_LOCAL) {
//                        networkOverhead += getNetworkOverhead(raw.length);
                        if (System.currentTimeMillis() - endtime > conf.BenchMarkDebugFrequency) {
//                        if (System.currentTimeMillis() - endtime > 1000000) {
                                endtime = System.currentTimeMillis();
                                System.out.println("LocalNode--" + messageResult.getNodeId() + "--PreAggregation"
                                        + "  WindowCounter:  " + messageResult.windowCollection.sliceCounter
                                        + (!messageResult.windowCollection.windowList.isEmpty() ?
                                        ( "  QueryId:  " + messageResult.windowCollection.windowList.get(0).queryId
                                        + "  WindowId:  " + messageResult.windowCollection.windowList.get(0).windowId
                                        + "  result:  " + messageResult.windowCollection.windowList.get(0).result
                                        + "  count:  " + messageResult.windowCollection.windowList.get(0).count) : "")
                                        + "  WindowList:  " + messageResult.windowCollection.windowList.size()
                                        + "  TupleList:  " + (messageResult.windowCollection.tuples != null ?
                                                messageResult.windowCollection.tuples.size() : 0)
                                        + "  Queue:  " + intermediateResultQueue.size()
//                                        + "  Id:  " + infoId
                                        + "  Slices:  " + Arrays.stream(Arrays.stream(infoList).toArray()).sum() / infoCounter
//                                        + "  Throughput:  " + messageResult.window.tupleCounter / ((endtime - begintime) / 1000.0)
//                                        + "  NetworkOverhead:  " + networkOverhead
//                                        + "  Allcounter:  " + messageResult.window.tupleCounter
//                                        + "  Time:  " + (endtime - begintime) / 1000.0
//                                        + "  GCTime:  " + getGarbageCollectionTime()
//                                        + "  GC/Time:  " + (double) getGarbageCollectionTime() / (endtime - begintime)
                                );

                            }
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //in case send too slow
//                if(intermediateResultQueue.size() > conf.DATAGENERATORMAXIMIUMBUFFER)
//                    intermediateResultQueue.clear();
            }
        }
    }

//    private static long getGarbageCollectionTime() {
//        long collectionTime = 0;
//        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
//            collectionTime += garbageCollectorMXBean.getCollectionTime();
//        }
//        return collectionTime;
//    }
//
//    private static long getNetworkOverhead(int rawSize) {
//        return (rawSize / (9000 - 46) + 1) * 46 + (45 + 45) + rawSize;
//    }
}
