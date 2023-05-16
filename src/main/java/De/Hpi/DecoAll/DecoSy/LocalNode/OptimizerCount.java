package De.Hpi.DecoAll.DecoSy.LocalNode;

import De.Hpi.DecoAll.DecoSy.Configure.Configuration;
import De.Hpi.DecoAll.DecoSy.Dao.LocalWindow;
import De.Hpi.DecoAll.DecoSy.Dao.LocalisEventHere;
import De.Hpi.DecoAll.DecoSy.Dao.Query;
import De.Hpi.DecoAll.DecoSy.Dao.Tuple;
import De.Hpi.DecoAll.DecoSy.Generator.InputStream;
import De.Hpi.DecoAll.DecoSy.Message.MessageToLocal;
import De.Hpi.DecoAll.DecoSy.Message.MessageToRoot;
import com.beust.ah.A;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
1. windows that have gaps only increment window counter.

 */

public class OptimizerCount implements Runnable{

    private Configuration conf;
    private InputStream inputStream;
    private ConcurrentLinkedQueue<MessageToRoot> messageToRootQueue;
    private ConcurrentLinkedQueue<MessageToLocal> messageToLocalQueue;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;

    private Query query;
    private double predictWindowSize;
    private double bufferSize;

    private long currentTupleCounter;
    private long totalTupleCounter;


    private long tupleCounter;
    private boolean[] operators;
    private LocalisEventHere localisEventHere;
    private Random random;

    //flags for organize()
    //there are non-decomposable function and system has to sort data anyway
    private boolean sortFLAG;
    //there are countbased window
    private boolean preserveFLAG;
    private boolean countbasedFLAG;
    private boolean maxFLAG;
    private boolean minFLAG;

    //For Deco
    private LocalWindow localWindow;
    //1 initialization, 2 prediction(root), 3 calculation
    private int stepFlag;

//    private long predictWindowSize;
//    private long predictWindowSizeEnd;
//    private long deltaSize;

    //For test
    private int intialTime;

    public OptimizerCount(Configuration conf, InputStream inputStream, ConcurrentLinkedQueue<MessageToRoot> messageToRootQueue,
                          ConcurrentLinkedQueue<MessageToLocal> messageToLocalQueue , ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue) {
        this.conf = conf;
        this.inputStream = inputStream;
        this.messageToRootQueue =messageToRootQueue;
        this.messageToLocalQueue =messageToLocalQueue;
        this.dataQueue =dataQueue;

        this.localWindow = new LocalWindow();
        this.localWindow.setLocalWindowCounter(1);
        this.localWindow.bufferTupleList = new ArrayList<>();
        this.stepFlag = 1;


        this.tupleCounter = 0;
        this.operators = new boolean[conf.OPERATORS];
        this.sortFLAG = false;
        this.preserveFLAG = false;
        this.countbasedFLAG = false;
        this.maxFLAG = false;
        this.minFLAG = false;
        this.localisEventHere = new LocalisEventHere();
        this.random = new Random();

    }

    public void run() {
        //to read all queries
        queryPreProcess();

        initializationStep();

        while(true) {
            //calculation step
            calculationStep();
            checkForCorrection();
        }
    }

    private void calculationStep(){
        while(stepFlag == 3) {
            if (!dataQueue.isEmpty()){
                ArrayList<Tuple> dataBuffer = dataQueue.poll();
                dataBuffer.forEach(tuple -> {

                    totalTupleCounter++;
                    currentTupleCounter++;
                    int isEventHere =  isEventHereCountBased();
                    processWindow(tuple, isEventHere);

                });

            }
        }
    }

    //the end tuple of a window always belongs to next window,
    private int isEventHereCountBased(){
        /*to make a fair comparison we let all baselines
         and our approaches be the same structures. This function
         may affect the performance since it does 'if' twice.
         */
        if(currentTupleCounter <= predictWindowSize){
            //incremental aggregation
            return 1;
//        }else if(currentTupleCounter == predictWindowSize){
            //send partial result
//            return 2;
        }else if(currentTupleCounter < predictWindowSize + bufferSize){
            //send tuples
            return 3;
        }else if(currentTupleCounter == predictWindowSize + bufferSize){
            //send frequencies
            return 4;
        }else{
            //stop processing
            return 5;
        }
    }


    private void processWindow(Tuple tuple, int isEventHere) {
        //optimizer can calculate all the queries.
        switch (isEventHere){
            //1 processing
            case 1:{
                calculate(tuple, localWindow);
                localWindow.partialResultTupleList.add(tuple);
                break;
            }
            //2 end the window and send results
            case 3:{
                localWindow.bufferTupleList.add(tuple);
                break;
            }
            case 4 : {
//               calculate(tuple, localWindow);
                MessageToRoot messageToRoot = new MessageToRoot();
                messageToRoot.setMessageType(5);
                messageToRoot.count = localWindow.count;
                messageToRoot.result = localWindow.result;
                messageToRoot.windowId = localWindow.getLocalWindowCounter();
                messageToRoot.bufferTupleList = localWindow.bufferTupleList;
                messageToRoot.eventRate = inputStream.getEventRates();
                messageToRootQueue.offer(messageToRoot);

                localWindow.count = 0;
                localWindow.result = 0;
                localWindow.localWindowCounterAdd();
                currentTupleCounter = 0;
                //move to next window's initialization step
                stepFlag = 1;
                break;
            }
            default:{
                break;
            }
        }

    }



    void calculate(Tuple tuple, LocalWindow localWindow){
            switch (query.getFunction()) {
                case Configuration.COUNT: {
                    localWindow.count++;
                    break;
                }
                case Configuration.SUM: {
                    localWindow.result += tuple.DATA;
                    break;
                }
                case Configuration.AVERAGE: {
                    localWindow.count++;
                    localWindow.result += tuple.DATA;
                    break;
                }
                case Configuration.MAX: {
                    localWindow.result = Math.max(localWindow.result, tuple.DATA);
                    break;
                }
                case Configuration.MIN: {
                    localWindow.result = Math.min(localWindow.result, tuple.DATA);
                    break;
                }
                default:
                    break;
           }
    }


    void checkForCorrection(){
        while(stepFlag == 1){
            if(!messageToLocalQueue.isEmpty()){
                MessageToLocal messageToLocal = messageToLocalQueue.poll();
                //local window size or Correction
                if(messageToLocal.getMessageType() == 3) {
                    this.predictWindowSize = messageToLocal.localWindowSize - messageToLocal.correctionFactor;
                    this.bufferSize = 2 * messageToLocal.correctionFactor;

                    localWindow.partialResultTupleList = new ArrayList<Tuple>((int) this.predictWindowSize);
                    localWindow.bufferTupleList = new ArrayList<Tuple>((int)this.bufferSize);
                    //move to calculation step
                    stepFlag = 3;
                }else if(messageToLocal.getMessageType() == 6){
                    this.predictWindowSize = messageToLocal.localWindowSize - messageToLocal.correctionFactor;
                    this.bufferSize = 2 * messageToLocal.correctionFactor;

                    dataQueue.offer(localWindow.partialResultTupleList);
                    dataQueue.offer(localWindow.bufferTupleList);
                    localWindow.partialResultTupleList = new ArrayList<Tuple>((int) this.predictWindowSize);
                    localWindow.bufferTupleList = new ArrayList<Tuple>((int)this.bufferSize);
                    //move to calculation step
                    stepFlag = 3;
                }
            }
        }
    }

    private void queryPreProcess(){
        while(true){
            if(!messageToLocalQueue.isEmpty()){
                this.query = messageToLocalQueue.poll().query;
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

    void initializationStep(){
        for(int i = 1; i <= conf.InitializationSteps; i++) {
            MessageToRoot messageToRoot = new MessageToRoot();
            messageToRoot.setMessageType(2);
            messageToRoot.eventRate = inputStream.getEventRates();
            sendMessage(messageToRoot);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while(stepFlag == 1){
            if(!messageToLocalQueue.isEmpty()){
                MessageToLocal messageToLocal = messageToLocalQueue.poll();
                //local window size
                if(messageToLocal.getMessageType() == 3) {
                    this.predictWindowSize = messageToLocal.localWindowSize - messageToLocal.correctionFactor;
                    this.bufferSize = 2 * messageToLocal.correctionFactor;
                    //move to calculation step
                    stepFlag = 3;
                }
            }
        }
    }

    private void sendMessage(MessageToRoot messageToRoot){
        messageToRootQueue.offer(messageToRoot);
    }

}
