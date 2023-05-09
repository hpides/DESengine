package De.Hpi.DecoAll.Debuffer.RootNode;
import De.Hpi.DecoAll.Debuffer.Configure.Configuration;
import De.Hpi.DecoAll.Debuffer.Dao.Query;
import De.Hpi.DecoAll.Debuffer.Dao.Tuple;
import De.Hpi.DecoAll.Debuffer.Dao.WindowCollection;
import De.Hpi.DecoAll.Debuffer.Generator.QueryGenerator;
import De.Hpi.DecoAll.Debuffer.Message.MessageToLocal;
import De.Hpi.DecoAll.Debuffer.Message.MessageToRoot;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;


public class RootNode {

    private Configuration conf;
    private QueryGenerator queryGenerator;
    private ConcurrentLinkedQueue<MessageToLocal> messageToLocalQueue;
    private ConcurrentLinkedQueue<Query> queryList;
    private ConcurrentLinkedQueue<WindowCollection> resultQueue;
    private ConcurrentLinkedQueue<MessageToRoot> messageToRootQueue;
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
        this.messageToLocalQueue = new ConcurrentLinkedQueue<MessageToLocal>();
        this.queryList = new ConcurrentLinkedQueue<Query>();
        this.resultQueue = new ConcurrentLinkedQueue<WindowCollection>();
        this.messageToRootQueue = new ConcurrentLinkedQueue<MessageToRoot>();
        this.dataQueue = new ConcurrentLinkedQueue<ArrayList<Tuple>>();
        this.queryGenerator =new QueryGenerator(messageToLocalQueue, queryList, conf);

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
//        socketSub.connect(conf.localPubAddr1W);


        //generate query
        queryGenerator.generate();

        //initial threads
        //send query to local node
        threadsList.add(new Thread(new RootPublishMessage(conf, messageToLocalQueue, socketPub)));
        //get the data from the intermedia node
        threadsList.add(new Thread(new RootSubscribeMassage(messageToRootQueue, conf, socketSub)));
        //perform aggregation in root node
        threadsList.add(new Thread(new RootComputationEngineDecentral(messageToRootQueue, messageToLocalQueue, conf, resultQueue, queryList)));
        //output result
//        threadsList.add(new Thread(new PrintResult(resultQueue, conf)));
    }

    public void startRootNode(){
        threadsList.forEach( thread -> thread.start());
    }

    public void stopRootNode(){
        threadsList.forEach( thread -> thread.interrupt());
    }

}
