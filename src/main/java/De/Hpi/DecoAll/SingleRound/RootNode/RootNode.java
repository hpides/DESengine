package De.Hpi.DecoAll.SingleRound.RootNode;
import De.Hpi.DecoAll.SingleRound.Configure.Configuration;
import De.Hpi.DecoAll.SingleRound.Dao.Query;
import De.Hpi.DecoAll.SingleRound.Dao.Tuple;
import De.Hpi.DecoAll.SingleRound.Dao.WindowCollection;
import De.Hpi.DecoAll.SingleRound.Generator.QueryGenerator;
import De.Hpi.DecoAll.SingleRound.MessageManager.RootPublishMessage;
import De.Hpi.DecoAll.SingleRound.MessageManager.RootSubscribeMassage;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;


public class RootNode {

    private Configuration conf;
    private QueryGenerator queryGenerator;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private ConcurrentLinkedQueue<Query> queryList;
    private ConcurrentLinkedQueue<WindowCollection> resultQueue;
    private ConcurrentLinkedQueue<Tuple> resultFromIntermedia;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;

    private ZContext context;
    private ZMQ.Socket socketPub;
    private ZMQ.Socket socketSub;
    private RootParseAddress rootParseAddress;

    private ArrayList<Thread> threadsList;

    public RootNode(Configuration conf, int nodeId){
        this.conf = conf;
        this.conf.setNodeId(nodeId);
        this.threadsList = new ArrayList<>();
        this.queryQueue = new ConcurrentLinkedQueue<Query>();
        this.queryList = new ConcurrentLinkedQueue<Query>();
        this.resultQueue = new ConcurrentLinkedQueue<WindowCollection>();
        this.resultFromIntermedia = new ConcurrentLinkedQueue<Tuple>();
        this.dataQueue = new ConcurrentLinkedQueue<ArrayList<Tuple>>();
        this.queryGenerator =new QueryGenerator(queryQueue, queryList, conf);

        this.context = new ZContext();
        this.rootParseAddress = new RootParseAddress();

        initialRootNode();
        startRootNode();
    }

    public void initialRootNode(){

        //initialize the publish-subscribe mode
        socketPub = context.createSocket(SocketType.PUB);
        socketPub.bind(rootParseAddress.getRootPubAddress(conf));

        // from intermedia to root
        socketSub = context.createSocket(SocketType.SUB);
        rootParseAddress.getLocalSubAddressAll(conf).forEach(addr -> socketSub.connect(addr));


        //generate query
        queryGenerator.generate();


        //initial threads
        //send query to intermedia node
        threadsList.add(new Thread(new RootPublishMessage(conf, queryQueue, socketPub)));
        //get the data from the intermedia node
        threadsList.add(new Thread(new RootSubscribeMassage(resultFromIntermedia, conf, socketSub)));
        //perform aggregation in root node
//        threadsList.add(new Thread(new RootComputationEngineDecentral(resultFromIntermedia, conf, resultQueue, queryList)));
        //output result
        threadsList.add(new Thread(new PrintResult(resultQueue, conf)));
    }

    public void startRootNode(){
        threadsList.forEach( thread -> thread.start());
    }

    public void stopRootNode(){
        threadsList.forEach( thread -> thread.interrupt());
    }

}
