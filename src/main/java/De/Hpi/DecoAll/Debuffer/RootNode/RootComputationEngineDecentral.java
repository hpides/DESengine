package De.Hpi.DecoAll.Debuffer.RootNode;

import De.Hpi.DecoAll.Debuffer.Configure.Configuration;
import De.Hpi.DecoAll.Debuffer.Dao.LocalEventRate;
import De.Hpi.DecoAll.Debuffer.Message.MessageToLocal;
import De.Hpi.DecoAll.Debuffer.Message.MessageToRoot;
import De.Hpi.DecoAll.Debuffer.Dao.Query;
import De.Hpi.DecoAll.Debuffer.Dao.RootTask;
import De.Hpi.DecoAll.Debuffer.Dao.WindowCollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class RootComputationEngineDecentral implements Runnable {

    private Configuration conf;
    private ConcurrentLinkedQueue<WindowCollection> resultQueue;
    private ConcurrentLinkedQueue<MessageToRoot> messageToRootQueue;
    private ConcurrentLinkedQueue<MessageToLocal> messageToLocalQueue;
    private Query query;
    private ArrayList<LocalEventRate> localEventRates;
    private int localEventRatesCounter;
    //0 in total, 1 node 1, 2 node 2
    private double[] localWindowSizes;

    private ConcurrentLinkedQueue<Query> queryQueue;
    private ArrayList<RootTask> rootTasks;
    private int currentSliceId;
    private long tupleCounter;
    private boolean countBasedFlag;
    private boolean timeBasedNonDecomposableFlag;

    //for debug
//    private long latencyOverall;
//    private long latencyCounter;

    RootComputationEngineDecentral(ConcurrentLinkedQueue<MessageToRoot> messageToRootQueue,
                                   ConcurrentLinkedQueue<MessageToLocal> messageToLocalQueue,
                                   Configuration conf,
                                   ConcurrentLinkedQueue<WindowCollection> resultQueue,
                                   ConcurrentLinkedQueue<Query> queryQueue){
        this.conf = conf;
        this.resultQueue =resultQueue;
        this.messageToRootQueue =messageToRootQueue;
        this.messageToLocalQueue =messageToLocalQueue;
        this.queryQueue = queryQueue;
        this.localEventRates = new ArrayList<>();
        this.localWindowSizes = new double[conf.localNumber + 1];

        this.rootTasks = new ArrayList<>();
        this.currentSliceId = 0;
        this.tupleCounter = 0;
        this.countBasedFlag = false;
        this.timeBasedNonDecomposableFlag = false;

        //for debug
//        this.latencyOverall = 0;
//        this.latencyCounter = 0;
        initial();

    }

    void initial(){
        //initial local event rate list
        for(int i = 1; i <= conf.localNumber; i++){
            LocalEventRate localEventRate = new LocalEventRate();
            localEventRate.setNodeId(i);
            localEventRate.setEventRates(0);
            localEventRates.add(localEventRate);
        }
        localEventRatesCounter = 0;
    }

    public void run() {
        queryPreProcess();
        while (true) {
            if (!messageToRootQueue.isEmpty()) {
                MessageToRoot messageToRoot = messageToRootQueue.poll();
                switch (messageToRoot.getMessageType()){
                    //get event rates
                    case 2:{
                        localEventRates.forEach(localEventRate -> {
                            if(localEventRate.getNodeId() == messageToRoot.getNodeId()) {
                                localEventRate.setEventRates(messageToRoot.eventRate);
                                localEventRatesCounter++;
                            }
                        });

                        //we get all event rates
                        if(localEventRatesCounter == conf.localNumber){
                            calculateLocalWindowSize();
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    private void calculateLocalWindowSize(){
        localEventRates.forEach(localEventRate -> {
            localWindowSizes[0] += localEventRate.getEventRates();
            localWindowSizes[localEventRate.getNodeId()] = localEventRate.getEventRates();
        });
        //calculate local window size, not smart but works
        localWindowSizes[1] = query.getRange();
        if(conf.localNumber >= 2) {
            for (int i = 2; i <= conf.localNumber; i++) {
                localWindowSizes[i] = (int)(localWindowSizes[i] / localWindowSizes[0] * query.getRange());
                localWindowSizes[1] -= localWindowSizes[i];
            }
        }
        //send all local window size back
        for (int i = 1; i <= conf.localNumber; i++) {
            MessageToLocal messageToLocal = new MessageToLocal();
            messageToLocal.setNodeId(i);
            messageToLocal.setMessageType(3);
            messageToLocal.localWindowSize = localWindowSizes[i];
            sendMessage(messageToLocal);
        }
        //clear
        localEventRatesCounter = 0;
    }

    void sendMessage(MessageToLocal messageToLocal){
        messageToLocalQueue.offer(messageToLocal);
    }

    private void queryPreProcess(){
        while(true){
            if(!queryQueue.isEmpty()){
                this.query = queryQueue.poll();
                break;
            }else{
                try {
                    Thread.sleep(conf.queryWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
