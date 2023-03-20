package De.Hpi.DesisCen.RootNode;
import De.Hpi.DesisCen.Configure.Configuration;
import De.Hpi.DesisCen.Dao.Query;
import De.Hpi.DesisCen.Dao.Tuple;
import De.Hpi.DesisCen.Dao.Window;
import De.Hpi.DesisCen.Generator.QueryGenerator;
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
    private ConcurrentLinkedQueue<Window> resultQueue;
    private ConcurrentLinkedQueue<Window> resultFromIntermediaDecentral;
    private ConcurrentLinkedQueue<Tuple> resultFromIntermediaCentral;
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
        this.resultQueue = new ConcurrentLinkedQueue<Window>();
        this.resultFromIntermediaCentral = new ConcurrentLinkedQueue<Tuple>();
        this.resultFromIntermediaDecentral = new ConcurrentLinkedQueue<Window>();
        this.dataQueue = new ConcurrentLinkedQueue<ArrayList<Tuple>>();
        this.queryGenerator =new QueryGenerator(queryQueue, queryList, conf);

        this.context = new ZContext();
        this.rootParseAddress = new RootParseAddress();

        initialRootNode();
        startRootNode();
    }

    public void initialRootNode(){

        // from local to root
        socketSub = context.createSocket(SocketType.SUB);
        rootParseAddress.getLocalSubAddressAll(conf).forEach(addr -> socketSub.connect(addr));

        //generate query
        queryGenerator.generate();
//        threadsList.add(new Thread(new DataGenerator(conf, dataQueue)));
        threadsList.add(new Thread(new BaselineCentralReceiver(dataQueue, conf, socketSub)));
        threadsList.add(new Thread(new BaselineCentraComputation(conf, resultQueue, queryQueue, dataQueue)));
        threadsList.add(new Thread(new BaselineCentraPrintresult(resultQueue, conf)));

    }

    public void startRootNode(){
        threadsList.forEach( thread -> thread.start());
    }

    public void stopRootNode(){
        threadsList.forEach( thread -> thread.interrupt());
    }

}
