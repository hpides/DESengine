package De.Hpi.DesisSW.MessageManager;

import De.Hpi.DesisSW.Configure.Configuration;
import De.Hpi.DesisSW.Dao.WindowCollection;
import De.Hpi.DesisSW.Message.MessageResult;
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
        long endtime = System.currentTimeMillis();
        while (true) {
            if(!resultQueue.isEmpty()) {
                WindowCollection windowCollection = resultQueue.poll();
//                window.setNodeId(conf.getNodeId());
                //the message type now it the data
                MessageResult resultFromIntermediaToRoot = new MessageResult();
                resultFromIntermediaToRoot.setNodeId(conf.getNodeId());
                resultFromIntermediaToRoot.setMessageType(conf.MESSAGERESULT);
                resultFromIntermediaToRoot.setMessageLevel(conf.INTERMEDIATENODEMESSAGE);
                resultFromIntermediaToRoot.windowCollection = windowCollection;
                try {
                    byte[] raw = msgpack.write(resultFromIntermediaToRoot);
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
