package De.Hpi.DecoAll.Deco.LocalNode;

import De.Hpi.DecoAll.Deco.Configure.Configuration;
import De.Hpi.DecoAll.Deco.Dao.LocalWindow;
import De.Hpi.DecoAll.Deco.Dao.LocalisEventHere;
import De.Hpi.DecoAll.Deco.Dao.Query;
import De.Hpi.DecoAll.Deco.Dao.Tuple;
import De.Hpi.DecoAll.Deco.Generator.InputStream;
import De.Hpi.DecoAll.Deco.Message.MessageToLocal;
import De.Hpi.DecoAll.Deco.Message.MessageToRoot;

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

        while(true) {
            //initialization step
            initializationStep();
            //calculation step
            calculationStep();
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
        if(currentTupleCounter < predictWindowSize){
            //incremental aggregation
            return 1;
        }else{
            //send partial results
            return 2;
        }
//        if(currentTupleCounter < predictWindowSize){
//            //incremental aggregation
//            return 1;
//        }else if(currentTupleCounter == predictWindowSize){
//            //send partial result
//            return 2;
//        }else if(currentTupleCounter < predictWindowSizeEnd){
//            //send tuple
//            return 3;
//        }else if(currentTupleCounter == predictWindowSizeEnd){
//            //send frequencies
//            return 4;
//        }else{
//            //stop processing
//            return 5;
//        }
    }


    private void processWindow(Tuple tuple, int isEventHere) {
        //optimizer can calculate all the queries.
        calculate(tuple, localWindow);
        switch (isEventHere){
            //1 processing
            case 1:{

                break;
            }
            //2 end the window and send results
            default :{
               MessageToRoot messageToRoot = new MessageToRoot();
               messageToRoot.setMessageType(4);
               messageToRoot.count = localWindow.count;
               messageToRoot.result = localWindow.result;
               messageToRoot.windowId = localWindow.getLocalWindowCounter();
               messageToRootQueue.offer(messageToRoot);

               localWindow.count = 0;
               localWindow.result = 0;
               localWindow.localWindowCounterAdd();
               currentTupleCounter = 0;
               //move to next window's initialization step
               stepFlag = 1;
                break;
            }
        }

//        //incremental aggregation
//        if(isEventHere == 1){
//            localWindow.fixedTupleList.add(tuple);
//            calculate(tuple);
//        //send partial result
//        }else if(isEventHere == 2){
//            localWindow.fixedTupleList.add(tuple);
//            calculate(tuple);
//            Tuple tuple1PR = new Tuple();
//            tuple1PR.EVENT = 2;
//            tuple1PR.DATA = localWindow.result;
//            tuple1PR.TIME = localWindow.count;
////            sendTuple(tuple1PR);
//        //send tuple
//        }else if(isEventHere == 3){
//            tuple.EVENT = 3;
//            localWindow.unfixedTupleList.add(tuple);
////            sendTuple(tuple);
//        //send tuple and frequencies
//        }else if(isEventHere == 4){
//            localWindow.unfixedTupleList.add(tuple);
////            sendTuple(tuple);
//            Tuple tuple1PR = new Tuple();
//            tuple1PR.EVENT = 4;
//            tuple1PR.DATA =  currentTupleCounter * 1000 / (tuple.TIME - intialTime);
//            tuple1PR.TIME = (int) System.currentTimeMillis();
////            sendTuple(tuple1PR);
//            currentTupleCounter = 0;
//        }


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
        MessageToRoot messageToRoot = new MessageToRoot();
        messageToRoot.setMessageType(2);
        messageToRoot.eventRate = inputStream.getEventRates();
        sendMessage(messageToRoot);
        while(stepFlag == 1){
            if(!messageToLocalQueue.isEmpty()){
                MessageToLocal messageToLocal = messageToLocalQueue.poll();
                //local window size
                if(messageToLocal.getMessageType() == 3) {
                    this.predictWindowSize = messageToLocal.localWindowSize;
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
