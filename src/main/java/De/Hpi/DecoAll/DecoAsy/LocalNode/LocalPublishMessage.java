package De.Hpi.DecoAll.DecoAsy.LocalNode;

import De.Hpi.DecoAll.DecoAsy.Configure.Configuration;
import De.Hpi.DecoAll.DecoAsy.Message.MessageToRoot;
import org.msgpack.MessagePack;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LocalPublishMessage implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<MessageToRoot> messageToRootQueue;
    private ZMQ.Socket socketPub;

    public LocalPublishMessage(ConcurrentLinkedQueue<MessageToRoot> messageToRootQueue, ZMQ.Socket socketPub, Configuration conf) {
        this.conf =conf;
        this.messageToRootQueue =messageToRootQueue;
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
            if(!messageToRootQueue.isEmpty()) {
                MessageToRoot messageToRoot = (MessageToRoot) messageToRootQueue.poll();
                messageToRoot.setNodeId(conf.getNodeId());
                //debug latency
//                if(windowCollection.nodeId == 0 )
//                    continue;
//                infoAll += windowCollection.nodeId;
//                infoCounter++;

//                tuple.EVENT += 10 * conf.getNodeId();
                //the message type now it the data
//                MessageResult messageResult = new MessageResult();
//                messageResult.setNodeId(conf.getNodeId());
//                messageResult.setMessageType(conf.MESSAGERESULT);
//                messageResult.setMessageLevel(conf.LOCALNODEMESSAGE);
//                messageResult.windowCollection = windowCollection;
                //for debug
//                tuple.DATA = System.currentTimeMillis();
                try {
                    byte[] raw = msgpack.write(messageToRoot);
//                    System.out.println(raw.length);
//                    for(int i = 0; i < conf.queryModes; i++)

                    socketPub.send(raw);

                    if(conf.DEBUGMODE_LOCAL) {
//                        if(messageToRoot.getMessageType() == 4)
//                            continue;
//                        networkOverhead += getNetworkOverhead(raw.length);
                        if (System.currentTimeMillis() - endtime >= conf.BenchMarkDebugFrequency) {
//                        if (System.currentTimeMillis() - endtime > 1000000) {
                                endtime = System.currentTimeMillis();
                                System.out.println("LocalNode--" + conf.getNodeId() + "--sending message----"
//                                        + "  WindowCounter:  " + messageResult.windowCollection.sliceCounter
//                                        + (!messageResult.windowCollection.windowList.isEmpty() ?
//                                        ( "  QueryId:  " + messageResult.windowCollection.windowList.get(0).queryId
                                        + "  nodeId:  " + messageToRoot.getNodeId()
                                        + "  Type:  " + messageToRoot.getMessageType()
                                        + "  WindowId:  " + messageToRoot.windowId
                                        + "  EventRate:  " + messageToRoot.eventRate
                                        + "  count:  " + messageToRoot.count
                                        + "  result:  " + messageToRoot.result
//                                        + "  result:  " + 0
//                                        + "  time:  " + 0
//                                        + "  event:  " + tuple.EVENT
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
                                if(messageToRoot.bufferTupleListStart != null && messageToRoot.bufferTupleListEnd != null )
                                    System.out.println("  sizeStart:  " + messageToRoot.bufferTupleListStart.size());
                                    System.out.println("  sizeEnd:  " + messageToRoot.bufferTupleListEnd.size());

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
