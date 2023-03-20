package De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer;

import De.Hpi.DesisMultipleKeys.Configure.Configuration;
import De.Hpi.DesisMultipleKeys.Dao.Query;
import De.Hpi.DesisMultipleKeys.Dao.Tuple;
import De.Hpi.DesisMultipleKeys.Dao.Window;
import De.Hpi.DesisMultipleKeys.Generator.DataGenerator;
import De.Hpi.DesisMultipleKeys.LocalNode.LocalParseAddress;
import De.Hpi.DesisMultipleKeys.MessageManager.LocalSubscribeMessage;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class BaselineNOLocal {

    private Configuration conf;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private ConcurrentLinkedQueue<Window> intermediateResultQueue;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;

    private ZContext context;
    private ZMQ.Socket socketPub;
    private ZMQ.Socket socketInterSub;
    private LocalParseAddress localParseAddress;

    private ArrayList<Thread> threadsList;

    public BaselineNOLocal(Configuration conf, int nodeId){
        this.conf = conf;
        this.conf.setNodeId(nodeId);
        this.threadsList = new ArrayList<>();
        this.queryQueue = new ConcurrentLinkedQueue<Query>();
        this.intermediateResultQueue = new ConcurrentLinkedQueue<Window>();
        this.dataQueue = new ConcurrentLinkedQueue<ArrayList<Tuple>>();

        this.context = new ZContext();
        this.localParseAddress = new LocalParseAddress();

        initialLocalode();
        startLocalNode();
    }

    public void initialLocalode() {
        //initialize the publish-subscribe mode
        socketPub = context.createSocket(SocketType.PUB);
        socketPub.bind(localParseAddress.getLocalPubAddress(conf, conf.getNodeId()));

        socketInterSub = context.createSocket(SocketType.SUB);
        socketInterSub.connect(localParseAddress.getInterPubAddress(conf, conf.getNodeId()));


//        threadsList.add(new Thread(new BaselineNOComputationEngine(conf, intermediateResultQueue, queryQueue, dataQueue)));

        //thread initialization
        //receive data from intermedia node
        threadsList.add(new Thread(new LocalSubscribeMessage(conf, queryQueue, socketInterSub)));

        //send the data to intermedia node
//        threadsList.add(new Thread(new LocalPublishMessage(intermediateResultQueue, socketPub, conf)));


//        Thread MultipleQueryProcessing = new Thread(new MultipleQueryProcessing(resultQueue, taskQueue));
//        MultipleQueryProcessing.start();

        //this for test
//        Thread SingleQueryProcessing = new Thread(new SingleQueryProcessing(queryQueue, resultQueue));
//        SingleQueryProcessing.start();

        //generate data
        threadsList.add(new Thread(new DataGenerator(conf, dataQueue, new AtomicLong())));

    }

    public void startLocalNode(){
        threadsList.forEach( thread -> thread.start());
    }

    public void stopLocalNode(){
        threadsList.forEach( thread -> thread.interrupt());
    }

}
