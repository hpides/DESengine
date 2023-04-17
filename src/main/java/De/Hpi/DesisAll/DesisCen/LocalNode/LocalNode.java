package De.Hpi.DesisAll.DesisCen.LocalNode;

import De.Hpi.DesisAll.DesisCen.Configure.Configuration;
import De.Hpi.DesisAll.DesisCen.Dao.Query;
import De.Hpi.DesisAll.DesisCen.Dao.Tuple;
import De.Hpi.DesisAll.DesisCen.Dao.Window;
import De.Hpi.DesisAll.DesisCen.Generator.InputStream;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LocalNode {

    private Configuration conf;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private ConcurrentLinkedQueue<Window> intermediateResultQueue;
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
        this.intermediateResultQueue = new ConcurrentLinkedQueue<Window>();
        this.dataQueue = new ConcurrentLinkedQueue<ArrayList<Tuple>>();

        this.context = new ZContext();
        this.localParseAddress = new LocalParseAddress();

        initialLocalode();
        startLocalNode();
    }

    public void initialLocalode() {

        //generate data
        threadsList.add(new Thread(new InputStream(conf, dataQueue, conf.GeneratorThreadNumber)));
//        threadsList.add(new Thread(new DataGenerator(conf, dataQueue)));
//        threadsList.add(new Thread(new DataGenerator(conf, dataQueue)));

        threadsList.add(new Thread(new BaselineCentralSender(dataQueue, conf)));

    }

    public void startLocalNode(){
        threadsList.forEach( thread -> thread.start());
    }

    public void stopLocalNode(){
        threadsList.forEach( thread -> thread.interrupt());
    }

}
