package De.Hpi.DesisAll.DesisQuantile.LocalNode;

import De.Hpi.DesisAll.DesisQuantile.Configure.Configuration;
import De.Hpi.DesisAll.DesisQuantile.Dao.Query;
import De.Hpi.DesisAll.DesisQuantile.Dao.Tuple;
import De.Hpi.DesisAll.DesisQuantile.Dao.WindowCollection;
import De.Hpi.DesisAll.DesisQuantile.Generator.InputStream;
import De.Hpi.DesisAll.DesisQuantile.MessageManager.LocalPublishMessage;
import De.Hpi.DesisAll.DesisQuantile.MessageManager.LocalSubscribeMessage;
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
        threadsList.add(new Thread(new Optimizer(conf, intermediateResultQueue, queryQueue, dataQueue)));
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
