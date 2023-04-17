package De.Hpi.DecoAll.SingleRound.LocalNode;

import De.Hpi.DecoAll.SingleRound.Configure.Configuration;
import De.Hpi.DecoAll.SingleRound.Dao.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
1. windows that have gaps only increment window counter.

 */

public class OptimizerCount implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<Tuple> intermediateResultQueue;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;


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
    private LocalTask localTask;
    private long currentTupleCounter;
    private long predictWindowSize;
    private long predictWindowSizeEnd;
    private long deltaSize;

    //For test
    private int intialTime;

    public OptimizerCount(Configuration conf, ConcurrentLinkedQueue<Tuple> intermediateResultQueue,
                          ConcurrentLinkedQueue<Query> queryQueue , ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue) {
        this.conf = conf;
        this.intermediateResultQueue =intermediateResultQueue;
        this.queryQueue =queryQueue;
        this.dataQueue =dataQueue;
        this.localTask = new LocalTask();
        this.localWindow = new LocalWindow();



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
        //Initialization Round 0 and Round 1
        predictWindowSize = 2000;
        deltaSize = 4000;
        predictWindowSizeEnd = predictWindowSize + deltaSize * 2;
        currentTupleCounter = 0;
        localWindow.setLocalWindowId(localWindow.getLocalWindowId());
        localWindow.fixedTupleList = new ArrayList<>();
        localWindow.unfixedTupleList = new ArrayList<>();


        while(true) {
            if (!dataQueue.isEmpty()){
                ArrayList<Tuple> dataBuffer = dataQueue.poll();
                dataBuffer.forEach(tuple -> {

                    //initial time
                    if(localWindow.fixedTupleList.isEmpty())
                        intialTime = tuple.TIME;

                    currentTupleCounter++;
                    int isEventHere =  isEventHereTimeBased();
                    processWindow(tuple, isEventHere);

                    if(isEventHere == 4){
                        try {
                            Thread.sleep(100000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                });

            }
        }
    }


    //the end tuple of a window always belongs to next window,
    private int isEventHereTimeBased(){
        if(currentTupleCounter < predictWindowSize){
            //incremental aggregation
            return 1;
        }else if(currentTupleCounter == predictWindowSize){
            //send partial result
            return 2;
        }else if(currentTupleCounter < predictWindowSizeEnd){
            //send tuple
            return 3;
        }else if(currentTupleCounter == predictWindowSizeEnd){
            //send frequencies
            return 4;
        }else{
            //stop processing
            return 5;
        }
    }


    private void processWindow(Tuple tuple, int isEventHere) {
        //optimizer can calculate all the queries

        //incremental aggregation
        if(isEventHere == 1){
            localWindow.fixedTupleList.add(tuple);
            calculate(tuple);
        //send partial result
        }else if(isEventHere == 2){
            localWindow.fixedTupleList.add(tuple);
            calculate(tuple);
            Tuple tuple1PR = new Tuple();
            tuple1PR.EVENT = 2;
            tuple1PR.DATA = localWindow.result;
            tuple1PR.TIME = localWindow.count;
            sendTuple(tuple1PR);
        //send tuple
        }else if(isEventHere == 3){
            tuple.EVENT = 3;
            localWindow.unfixedTupleList.add(tuple);
            sendTuple(tuple);
        //send tuple and frequencies
        }else if(isEventHere == 4){
            localWindow.unfixedTupleList.add(tuple);
            sendTuple(tuple);
            Tuple tuple1PR = new Tuple();
            tuple1PR.EVENT = 4;
            tuple1PR.DATA =  currentTupleCounter * 1000 / (tuple.TIME - intialTime);
            tuple1PR.TIME = (int) System.currentTimeMillis();
            sendTuple(tuple1PR);
            currentTupleCounter = 0;
        }


    }

    private void sendTuple(Tuple tuple){
        intermediateResultQueue.add(tuple);
    }

    void calculate(Tuple tuple){
            switch (localTask.query.getFunction()) {
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
        //get all queries, it will be skipped when all queries retrieved
        int queryCounter = 0;
        while(queryCounter < conf.queryNumber){
            if(!queryQueue.isEmpty()){
                Query query = (Query) queryQueue.poll();
                localTask.query = query;
                localTask.setTaskId(localTask.query.getQueryId());
                //window counter should start from 1,
                // since we use windowId=0 to decide if this query output result to parent
                localTask.setWindowId(1);
                queryCounter++;
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
