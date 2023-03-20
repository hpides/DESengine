package De.Hpi.Deco.MessageManager;

import De.Hpi.Deco.Configure.Configuration;
import De.Hpi.Deco.Dao.Tuple;
import De.Hpi.Deco.Dao.WindowCollection;
import De.Hpi.Deco.Message.MessageResult;
import org.msgpack.MessagePack;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LocalPublishMessage implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<Tuple> intermediateResultQueue;
    private ZMQ.Socket socketPub;

    public LocalPublishMessage(ConcurrentLinkedQueue<Tuple> intermediateResultQueue, ZMQ.Socket socketPub, Configuration conf) {
        this.conf =conf;
        this.intermediateResultQueue =intermediateResultQueue;
        this.socketPub = socketPub;
    }

    public void run() {
//        System.out.println("Starting RequestThread ----localNode");
        MessagePack msgpack = new MessagePack();

//        int infoCounter = 0;
//        float infoAll = 0;

//        Long networkOverhead = 0l;
//        long begintime = System.currentTimeMillis();
        long endtime = System.currentTimeMillis();
        while (true) {
            if(!intermediateResultQueue.isEmpty()) {
                Tuple tuple = (Tuple) intermediateResultQueue.poll();

                //debug latency
//                if(windowCollection.nodeId == 0 )
//                    continue;
//                infoAll += windowCollection.nodeId;
//                infoCounter++;

                tuple.EVENT += 10 * conf.getNodeId();
                //the message type now it the data
//                MessageResult messageResult = new MessageResult();
//                messageResult.setNodeId(conf.getNodeId());
//                messageResult.setMessageType(conf.MESSAGERESULT);
//                messageResult.setMessageLevel(conf.LOCALNODEMESSAGE);
//                messageResult.windowCollection = windowCollection;
                //for debug
                tuple.DATA = System.currentTimeMillis();
                try {
                    byte[] raw = msgpack.write(tuple);
//                    for(int i = 0; i < conf.queryModes; i++)
                    socketPub.send(raw);

                    if(conf.DEBUGMODE_LOCAL) {
//                        networkOverhead += getNetworkOverhead(raw.length);
                        if (System.currentTimeMillis() - endtime >= conf.BenchMarkDebugFrequency) {
//                        if (System.currentTimeMillis() - endtime > 1000000) {
                                endtime = System.currentTimeMillis();
                                System.out.println("LocalNode--" + conf.getNodeId() + "--PreAggregation"
//                                        + "  WindowCounter:  " + messageResult.windowCollection.sliceCounter
//                                        + (!messageResult.windowCollection.windowList.isEmpty() ?
//                                        ( "  QueryId:  " + messageResult.windowCollection.windowList.get(0).queryId
//                                        + "  WindowId:  " + messageResult.windowCollection.windowList.get(0).windowId
                                        + "  nodeId:  " + tuple.EVENT / 10
                                        + "  result:  " + tuple.DATA
                                        + "  time:  " + tuple.TIME
                                        + "  event:  " + tuple.EVENT
//                                        + "  WindowList:  " + messageResult.windowCollection.windowList.size()
//                                        + "  TupleList:  " + (messageResult.windowCollection.tuples != null ?
//                                                messageResult.windowCollection.tuples.size() : 0)
//                                        + "  Queue:  " + intermediateResultQueue.size()
//                                        + "  Latency:  " + latencyAll / latencyCounter
//                                        + "  Slices:  " + infoAll / infoCounter
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

}
