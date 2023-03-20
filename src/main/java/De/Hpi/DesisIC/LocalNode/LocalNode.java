package De.Hpi.DesisIC.LocalNode;

import De.Hpi.DesisIC.Dao.Query;
import De.Hpi.DesisIC.Configure.Configuration;
import De.Hpi.DesisIC.Dao.Tuple;
import De.Hpi.DesisIC.Dao.WindowCollection;
import De.Hpi.DesisIC.Generator.InputStream;
import De.Hpi.DesisIC.MessageManager.LocalPublishMessage;
import De.Hpi.DesisIC.MessageManager.LocalSubscribeMessage;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LocalNode {

    private Configuration conf;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private ConcurrentLinkedQueue<WindowCollection> intermediateResultQueue;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;

    private ZContext context;
    private ZMQ.Socket socketPub;
    private ZMQ.Socket socketInterSub;
    private LocalParseAddress localParseAddress;
    private ArrayList<Thread> threadsList;

    public LocalNode(Configuration conf, int nodeId){
        this.conf = conf;
        this.conf.setNodeId(nodeId);
        this.threadsList = new ArrayList<>();
        this.queryQueue = new ConcurrentLinkedQueue<Query>();
        this.intermediateResultQueue = new ConcurrentLinkedQueue<WindowCollection>();
        this.dataQueue = new ConcurrentLinkedQueue<ArrayList<Tuple>>();
        this.context = new ZContext();
        this.localParseAddress = new LocalParseAddress();

        //for debug
//        this.conf.countgranularity =  conf.queryModes;

        initialLocalode();
        startLocalNode();
    }

    public void initialLocalode() {
        //initialize the publish-subscribe mode
        socketPub = context.createSocket(SocketType.PUB);
        socketPub.bind(localParseAddress.getLocalPubAddress(conf, conf.getNodeId()));

        socketInterSub = context.createSocket(SocketType.SUB);
        socketInterSub.connect(localParseAddress.getInterPubAddress(conf, conf.getNodeId()));

        //decentralized aggregation
        threadsList.add(new Thread(new LocalComputationEngineCount(conf, intermediateResultQueue, queryQueue, dataQueue)));
        //thread initialization
        //receive data from intermedia node
        threadsList.add(new Thread(new LocalSubscribeMessage(conf, queryQueue, socketInterSub)));

        //send the data to intermedia node
        threadsList.add(new Thread(new LocalPublishMessage(intermediateResultQueue, socketPub, conf)));

        //generate data
//        threadsList.add(new Thread(new DataGenerator(conf, dataQueue)));
//        threadsList.add(new Thread(new DataGenerator(conf, dataQueue)));
        threadsList.add(new Thread(new InputStream(conf, dataQueue, conf.GeneratorThreadNumber)));

    }

    public void startLocalNode(){
        threadsList.forEach( thread -> thread.start());
    }

    public void stopLocalNode(){
        threadsList.forEach( thread -> thread.interrupt());
    }

}
