package De.Hpi.DesisAll.Desis.MessageManager;

import De.Hpi.DesisAll.Desis.Configure.Configuration;
import De.Hpi.DesisAll.Desis.Dao.WindowCollection;
import De.Hpi.DesisAll.Desis.Message.MessageResult;
import org.msgpack.MessagePack;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IntermediatePublishMessage implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<WindowCollection> resultQueue;
    private ZMQ.Socket socketPub;

    public IntermediatePublishMessage(ConcurrentLinkedQueue<WindowCollection> resultQueue, ZMQ.Socket socketPub, Configuration conf) {
        this.conf = conf;
        this.resultQueue =resultQueue;
        this.socketPub = socketPub;
    }

    public void run() {
//        System.out.println("Starting UpperRequestThread ----intermediateNode");
        MessagePack msgpack = new MessagePack();
        int latencyCounter = 0;
        int latencyAll = 0;
        long endtime = System.currentTimeMillis();
        while (true) {
            if(!resultQueue.isEmpty()) {
                WindowCollection windowCollection = resultQueue.poll();
                latencyAll += windowCollection.nodeId;
                latencyCounter++;
//                window.setNodeId(conf.getNodeId());
                //the message type now it the data
                MessageResult resultFromIntermediaToRoot = new MessageResult();
                resultFromIntermediaToRoot.setNodeId(conf.getNodeId());
                resultFromIntermediaToRoot.setMessageType(conf.MESSAGERESULT);
                resultFromIntermediaToRoot.setMessageLevel(conf.INTERMEDIATENODEMESSAGE);
                resultFromIntermediaToRoot.windowCollection = windowCollection;
                try {
                    byte[] raw = msgpack.write(resultFromIntermediaToRoot);
//                    for(int i = 0; i < conf.queryModes; i++)
                    socketPub.send(raw);
                    if(conf.DEBUGMODE_INTER) {
                        if (System.currentTimeMillis() - endtime > conf.BenchMarkDebugFrequency) {
                            endtime = System.currentTimeMillis();
                            System.out.println("InterNode--" + resultFromIntermediaToRoot.getNodeId() + "--PreAggregation"
                                            + "  WindowCounter:  " + resultFromIntermediaToRoot.windowCollection.sliceCounter
                                            + (!resultFromIntermediaToRoot.windowCollection.windowList.isEmpty() ?
                                            ( "  QueryId:  " + resultFromIntermediaToRoot.windowCollection.windowList.get(0).queryId
                                            + "  WindowId:  " + resultFromIntermediaToRoot.windowCollection.windowList.get(0).windowId
                                            + "  result:  " + resultFromIntermediaToRoot.windowCollection.windowList.get(0).result
                                            + "  count:  " + resultFromIntermediaToRoot.windowCollection.windowList.get(0).count) : "")
                                            + "  WindowList:  " + resultFromIntermediaToRoot.windowCollection.windowList.size()
                                            + "  TupleList:  " + (resultFromIntermediaToRoot.windowCollection.tuples != null ?
                                            resultFromIntermediaToRoot.windowCollection.tuples.size() : 0)
                                            + "  Queue:  " + resultQueue.size()
                                            + "  Latency:  " + latencyAll / latencyCounter
//                                    + "  NetworkOverhead:  " + networkOverhead
//                                    + "  Throughput:  " + resultFromIntermediaToRoot.window.tupleCounter / ((endtime - begintime) / 1000.0)
                            );
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
//    private static long getNetworkOverhead(int rawSize) {
//        return (rawSize / (9000 - 46) + 1) * 46 + (45 + 45)  + (44 + 44 + 44)  * 2 + rawSize;
//    }

}
