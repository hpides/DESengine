package De.Hpi.DecoAll.Debuffer.LocalNode;

import De.Hpi.DecoAll.Debuffer.Configure.Configuration;
import De.Hpi.DecoAll.Debuffer.Dao.Tuple;
import De.Hpi.DecoAll.Debuffer.Generator.InputStream;
import De.Hpi.DecoAll.Debuffer.Message.MessageToLocal;
import De.Hpi.DecoAll.Debuffer.Message.MessageToRoot;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LocalNode {

    private Configuration conf;
    private ConcurrentLinkedQueue<MessageToLocal> messageToLocalQueue;
    private ConcurrentLinkedQueue<MessageToRoot> messageToRootQueue;
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
        this.messageToLocalQueue = new ConcurrentLinkedQueue<MessageToLocal>();
        this.messageToRootQueue = new ConcurrentLinkedQueue<MessageToRoot>();
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
//        socketPub.bind(conf.localPubAddr1W);

        socketInterSub = context.createSocket(SocketType.SUB);
        socketInterSub.connect(localParseAddress.getRootPubAddress(conf));

        //generate data
        InputStream inputStream = new InputStream(conf, dataQueue, conf.GeneratorThreadNumber);
//        threadsList.add(new Thread(new DataGenerator(conf, dataQueue)));
//        threadsList.add(new Thread(inputStream));

        //decentralized aggregation
        threadsList.add(new Thread(new OptimizerCount(conf, inputStream, messageToRootQueue, messageToLocalQueue, dataQueue)));

        //thread initialization
        //receive data from intermedia node
        threadsList.add(new Thread(new LocalSubscribeMessage(conf, messageToLocalQueue, socketInterSub)));

        //send the data to intermedia node
        threadsList.add(new Thread(new LocalPublishMessage(messageToRootQueue, socketPub, conf)));

    }

    public void startLocalNode(){
        threadsList.forEach( thread -> thread.start());
    }

    public void stopLocalNode(){
        threadsList.forEach( thread -> thread.interrupt());
    }

}
