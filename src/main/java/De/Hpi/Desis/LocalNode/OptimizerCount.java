package De.Hpi.Desis.LocalNode;

import De.Hpi.Desis.Configure.Configuration;
import De.Hpi.Desis.Dao.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/*
1. windows that have gaps only increment window counter.

 */

public class OptimizerCount implements Runnable{

    private Configuration conf;
    private ArrayList<LocalTask> localTasks;
    private LinkedList<LocalWindow> localWindows;
    private ArrayList<Tuple> tupleList;
    private ConcurrentLinkedQueue<WindowCollection> intermediateResultQueue;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;
    private int localWindowCounter;
    private int localWindowProcessCounterofNonDecomposable;
    private long tupleCounter;
    private long previousTimeCounter;
    private long previousCountCounter;
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

//    for debug
//    private long latencyOverall;
//    private long latencyCounter;

    public OptimizerCount(Configuration conf, ConcurrentLinkedQueue<WindowCollection> intermediateResultQueue,
                     ConcurrentLinkedQueue<Query> queryQueue , ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue) {
        this.conf = conf;
        this.intermediateResultQueue =intermediateResultQueue;
        this.queryQueue =queryQueue;
        this.dataQueue =dataQueue;
        this.localTasks = new ArrayList<>();
        this.localWindows = new LinkedList<>();
        this.tupleList = new ArrayList<>(conf.localBatchSize);
        this.localWindowCounter = 0;
        this.localWindowProcessCounterofNonDecomposable = 0;
        this.tupleCounter = 0;
        this.previousTimeCounter = 0;
        this.operators = new boolean[conf.OPERATORS];
        this.sortFLAG = false;
        this.preserveFLAG = false;
        this.countbasedFLAG = false;
        this.maxFLAG = false;
        this.minFLAG = false;
        this.localisEventHere = new LocalisEventHere();
        this.random = new Random();

        //for debug
//        this.latencyOverall = 0;
//        this.latencyCounter = 0;

    }

    public void run() {
        //to read all queries
        queryPreProcess();
        //to increase time granularity
        previousTimeCounter = System.currentTimeMillis();
        previousCountCounter = 0;
//        final long[] timeAll = new long[1];
//        final long[] time0 = new long[1];
//        final long[] time1 = new long[1];
//        final long[] time2 = new long[1];
//        final long[] time3 = new long[1];
//        long timeTemp1 = System.nanoTime();
//        long timeTemp2 = System.nanoTime();
        while(true) {
            if (!dataQueue.isEmpty()){
                ArrayList<Tuple> dataBuffer = dataQueue.poll();
                dataBuffer.forEach(tuple -> {
                    tupleCounterIncrement();

//                    long timeTemp1 = System.nanoTime();

                    long timeTemp = System.currentTimeMillis();
//                    if(timeTemp - previousTimeCounter >= conf. timegranularity && tuple.EVENT == 1){

//                    if(timeTemp - previousTimeCounter >= conf. timegranularity || tupleCounter - previousCountCounter >= conf.countgranularity){

                    if(tupleCounter - previousCountCounter >= conf.countgranularity){
                        //slice window
                        isEventHereTimeBased(tuple, timeTemp);
                        if(timeTemp - previousTimeCounter >= conf. timegranularity){
                            previousTimeCounter = System.currentTimeMillis();
                        }else {
                            previousCountCounter = tupleCounter;
                        }
                    }

//                    time0[0] = time0[0] + System.nanoTime() - timeTemp1;
//                    timeTemp1 = System.nanoTime();

                    if(localisEventHere.isFinishWindow()){
                        endWindow(localisEventHere.isFinishWindow());
                        localisEventHere.setFinishWindow(false);
                    }

//                    time1[0] = time1[0] + System.nanoTime() - timeTemp1;
//                    timeTemp1 = System.nanoTime();

                    if(localisEventHere.isCreateNewWindow()){
                        createWindow(localisEventHere);
                        localisEventHere.setCreateNewWindow(false);
                    }

//                    time2[0] = time2[0] + System.nanoTime() - timeTemp1;
//                    timeTemp1 = System.nanoTime();

                    //we have to process all tuples anyway
//                    if(localisEventHere.isProcessWindow()){
                    //calculate the result the calculate action
                    processWindow(tuple);
//                        localisEventHere.setProcessWindow(false);
//                    }

//                    time3[0] = time3[0] + System.nanoTime() - timeTemp1;

                });
//                if(tupleCounter > 10000000)
//                    break;;
            }
        }
//        timeAll[0] = timeAll[0] + System.nanoTime() - timeTemp2;
//        System.out.println(timeAll[0]);
//        System.out.println(time0[0]);
//        System.out.println(time1[0]);
//        System.out.println(time2[0]);
//        System.out.println(time3[0]);
//        System.out.println(tupleCounter);
    }

    //the end tuple of a window always belongs to next window,
    private void isEventHereTimeBased(Tuple tuple, long timeTemp){
        //to record the window state of each query, it is a global var now
//        LocalisEventHere localisEventHere = new LocalisEventHere();
        //to break down functions into operators
//        localisEventHere.functions = new boolean[conf.FUNCTIONS];
        //the window of this query is end and need to be calculated
//        localisEventHere.endList = new boolean[localTasks.size()];
        //the window of this query is processing
        localisEventHere.processList = new int[localTasks.size()];
        //mark down the current state of window that belong to this query
//        localisEventHere.stateList = new int[localTasks.size()];
        //in case there is a long gap and multiple windows end
//        localisEventHere.multipleWindowEndList = new int[localTasks.size()];
        localisEventHere.setProcessCount(0);
        localisEventHere.setProcessCountNonDecom(0);
        localTasks.forEach(task -> {
            //iterate all the query and process the bound of query
            // decentralized aggregation
//            if(task.query.getScenario() == conf.DeCentralizedAggregation) {
//            switch (task.query.getWindowType()) {
            switch (task.query.getWindowType()) {
                //1------tumbling window
                case Configuration.TUMBING: {
                    if (task.getInitialTime() == 0) {
                        //for the state
                        task.windowSliceAdd();
                        localisEventHere.setCreateNewWindow(true);
                        localisEventHere.setProcessWindow(true);
                        //initial tumbing window
                        task.setProcessTime(timeTemp);
                        task.setInitialTime(timeTemp);
                    } else {
                        if ((timeTemp - task.getProcessTime()) > task.query.getRange()) {
                            localisEventHere.setCreateNewWindow(true);
                            localisEventHere.setFinishWindow(true);
                            task.setWindowEnd(true);
                            task.setWindowCount((int)(timeTemp - task.getInitialTime()) / task.query.getRange());
//                            localisEventHere.multipleWindowEndList[task.getTaskId()] =
//                                    (int) (timeTemp - task.getInitialTime()) / task.query.getRange();
                            //in case there is a super long gap
                            task.setProcessTime(task.getInitialTime() + task.getWindowCount()*task.query.getRange());
                            //task.setProcessTime(timeTemp - (timeTemp - task.getProcessTime()) % task.query.getRange());
                        }
                    }
                    //the new slice includes all queries that are processing
                    localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
                    if(task.query.getFunction() == conf.MEDIAN | task.query.getFunction() == conf.QUANTILE)
                        localisEventHere.addProcessCountNonDecom(task.getwindowSlices());
                    localisEventHere.addProcessCount(task.getwindowSlices());
//                    localisEventHere.functions[task.query.getFunction()] = true;
                    break;
                }
                //2-----sliding window
                case Configuration.SLIDING: {
                    //initial sliding window
                    //event time to start window, processing time to end window
                    if (task.getInitialTime() == 0) {
                        //for the state
                        task.windowSliceAdd();
                        localisEventHere.setCreateNewWindow(true);
                        localisEventHere.setProcessWindow(true);
                        task.setProcessTime(timeTemp);
                        task.setEventTime(timeTemp);
                        task.setInitialTime(timeTemp);
                    } else {
                        //window end
                        if ((timeTemp - task.getProcessTime()) > task.query.getRange()) {
                            //for the state
                            task.windowSliceDelete();
                            localisEventHere.setCreateNewWindow(true);
                            localisEventHere.setFinishWindow(true);
                            task.setWindowEnd(true);
                            //home many windows if there is a long gap
                            task.setWindowCount(
                                    (int) (timeTemp - task.getInitialTime() - task.query.getRange()) / task.query.getSlide() + 1);
//                            localisEventHere.multipleWindowEndList[task.getTaskId()] =
//                                    (int) (timeTemp - task.getProcessTime() - task.query.getRange()) / task.query.getSlide() + 1;
                            //in case there is a super long gap
//                            task.setProcessTime(timeTemp - (timeTemp - task.getProcessTime() - task.query.getRange()) % task.query.getSlide()
//                                    - task.query.getRange() + task.query.getSlide());
                            task.setProcessTime(task.getInitialTime() + task.getWindowCount()*task.query.getSlide());
                        }
                        //window start
                        if ((timeTemp - task.getEventTime()) > task.query.getSlide()) {
                            //for the state
                            task.windowSliceAdd();
//                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                            localisEventHere.setCreateNewWindow(true);
                            //home many windows if there is a long gap
//                                resultOfIsEventHere.multipleWindowEndList[task.getTaskId()] = (int) (timeTemp - task.getEventTime()) / task.query.getSlide();
                            //align and in case there is a long gap
                            task.setEventTime(timeTemp - (timeTemp - task.getEventTime()) % task.query.getSlide());
                        }
                    }
                    localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
                    if(task.query.getFunction() == conf.MEDIAN | task.query.getFunction() == conf.QUANTILE)
                        localisEventHere.addProcessCountNonDecom(task.getwindowSlices());
                    localisEventHere.addProcessCount(task.getwindowSlices());
//                    localisEventHere.functions[task.query.getFunction()] = true;
                    break;
                }
                //4-----session window
                case Configuration.SESSION: {
                    if (task.getProcessTime() == 0) {
                        task.setProcessTime(timeTemp);
                        //for the state
                        task.windowSliceAdd();
//                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                        localisEventHere.setCreateNewWindow(true);
                        localisEventHere.setProcessWindow(true);
                    } else {
                        if (timeTemp - task.getProcessTime() > task.query.getRange()) {
                            //there is a gap
//                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
                            localisEventHere.setCreateNewWindow(true);
                            localisEventHere.setFinishWindow(true);
                            task.setWindowEnd(true);
                            task.setWindowCount(task.getWindowCount() + 1);
//                            localisEventHere.multipleWindowEndList[task.getTaskId()] = 1;
                            //in case there is a super long gap
                            task.setProcessTime(timeTemp);
                        } else {
                            //for the state
//                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
                            task.setProcessTime(timeTemp);
                        }
                    }
                    localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
                    if(task.query.getFunction() == conf.MEDIAN | task.query.getFunction() == conf.QUANTILE)
                        localisEventHere.addProcessCountNonDecom(task.getwindowSlices());
                    localisEventHere.addProcessCount(task.getwindowSlices());
//                    localisEventHere.functions[task.query.getFunction()] = true;
                    break;
                }
                //5-----punctuation window
                case Configuration.PUNCTUATION: {
                    if (task.getProcessTime() == 0) {
                        task.setProcessTime(timeTemp);
                        //for the state
                        task.windowSliceAdd();
//                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                        localisEventHere.setCreateNewWindow(true);
                        localisEventHere.setProcessWindow(true);
                    } else {
                        //to simulate punctuation window
                        // if (task.query.getEndPunctuation() == tuple.EVENT) {
                        if(timeTemp - previousTimeCounter >= 1000.0 / task.query.getEndPunctuation()) {
                            if (random.nextInt((int) (task.query.getEndPunctuation()
                                    / (timeTemp - previousTimeCounter)) + 1) == 1) {
                                //there is a gap
//                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
                                localisEventHere.setCreateNewWindow(true);
                                localisEventHere.setFinishWindow(true);
                                task.setWindowEnd(true);
//                            localisEventHere.multipleWindowEndList[task.getTaskId()] = 1;
                                task.setWindowCount(task.getWindowCount() + 1);
                                //in case there is a super long gap
                                task.setProcessTime(timeTemp);
                            }
                        }
                    }
                    localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
                    if(task.query.getFunction() == conf.MEDIAN | task.query.getFunction() == conf.QUANTILE)
                        localisEventHere.addProcessCountNonDecom(task.getwindowSlices());
                    localisEventHere.addProcessCount(task.getwindowSlices());
//                    localisEventHere.functions[task.query.getFunction()] = true;
                    break;
                }
                //6 -- count based window
                //in fact the first tuple which make decision to build
                // the sub window is not belong to this sub window, however here is a bug
                //so the tuple number is more than batchSize, which equals to batchsize + 1
                //batch size is process time
                //!! we don't need to process countbased window separately
//                case Configuration.COUNTBASED: {
                case Configuration.COUNTBASED: {
                    if (task.getProcessTime() == 0) {
                        task.setProcessTime(tupleCounter);
                        //for the state
                        task.windowSliceAdd();
//                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                        localisEventHere.setCreateNewWindow(true);
                        localisEventHere.setProcessWindow(true);
                    } else {
//                            if (tupleCounter - task.getProcessTime() > task.query.getBatchSize()) {
                        if (tupleCounter - task.getProcessTime() >= task.query.getRange()) {
                            //for the state
//                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
                            localisEventHere.setCreateNewWindow(true);
                            localisEventHere.setFinishWindow(true);
                            task.setWindowEnd(true);
//                            localisEventHere.multipleWindowEndList[task.getTaskId()] = 1;
                            task.setWindowCount(task.getWindowCount() + 1);
                            //align
                            task.setProcessTime(tupleCounter);
                        }
                    }
//                    localisEventHere.functions[task.query.getFunction()] = true;
                    localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
                    if(task.query.getFunction() == conf.MEDIAN | task.query.getFunction() == conf.QUANTILE)
                        localisEventHere.addProcessCountNonDecom(task.getwindowSlices());
                    localisEventHere.addProcessCount(task.getwindowSlices());
                    break;
                }
                default: {
                    break;
                }

            }
        });
//        return localisEventHere;
    }

    private void createWindow(LocalisEventHere localisEventHere){
        //organize previous local window
//        if(!localWindows.isEmpty())
//            localWindows.getLast().tupleList.sort((a, b) -> Double.compare(a.DATA, b.DATA));

        //instance of new local window
        localWindowCounterIncrement();
        LocalWindow localWindow = new LocalWindow();
        localWindow.setLocalWindowId(localWindowCounter);
        //record window slice
        localWindow.processList = localisEventHere.processList;
        localWindow.setLocalWindowCounter(localisEventHere.getProcessCount());
        localWindowProcessCounterofNonDecomposable = localisEventHere.getProcessCountNonDecom();
//        localWindow.tupleList = new ArrayList<Tuple>(conf.centralizedBatchSize);
        localWindow.count = 0;
        localWindow.sum = 0;
        localWindow.max = Double.MIN_VALUE;
        localWindow.min = Double.MAX_VALUE;
        //send batch when slice end
        if(tupleList.size() > 0){
            endWindow(false);
        }
        localWindows.add(localWindow);
    }

    private void processWindow(Tuple tuple) {
        //optimizer can calculate all the queries
        //decomposable function
        if(!localWindows.isEmpty()) {
            LocalWindow localWindow = localWindows.getLast();
            if (this.operators[conf.COUNTOPERATOR]) {
                localWindow.count++;
            }
            if (this.operators[conf.SUMOPERATOR]) {
                localWindow.sum += tuple.DATA;
            }
            if (this.operators[conf.MAXOPERATOR]) {
                localWindow.max = Math.max(localWindow.max, tuple.DATA);
            }
            if (this.operators[conf.MINOPERATOR]) {
                localWindow.min = Math.min(localWindow.min, tuple.DATA);
            }
        }
//        if(this.operators[conf.SORTOPERATOR] | this.operators[conf.PRESERVEOPERATOR]){
//            tupleList.add(tuple);
//        }
        //batch tuples to send, because the maximum packet size of zeromp no more than 50000 bytes.
        //only when there is no countbased window
        //tuplelist size > 0 means there are median and quantile functions
//        if(tupleList.size() >= conf.localBatchSize){
//            endWindow(false);
//        }
    }

    void endWindow(boolean isWindowEnd) {
//        long time1 = 0;
//        long time2 = 0;
//        long time3 = 0;

//        System.out.println(localWindows.size());

//        debug for latency
        long latencyStart = System.nanoTime();

        WindowCollection windowCollection = new WindowCollection();
        windowCollection.windowList = new ArrayList<>();
//        System.out.println(tupleList.size());
        //sort -> store, sort based on store
        //for decentralized aggregation
//        long timeTemp = System.nanoTime();
        organizeWinodw(windowCollection);
//        time1 = time1 + System.nanoTime() - timeTemp;
//        timeTemp = System.nanoTime();

        //we chance strategy that merges windows
        //iterate all local tasks, only when there is a timebased window end
        if(isWindowEnd) {
            localTasks.forEach(task -> {
                //we don't process countbased window
//                if(task.query.getWindowType() == conf.COUNTBASED)
//                    return;
                if (task.getWindowEnd()) {
//                    windowCounter++;
                    Window window = new Window();
                    window.queryId = task.query.getQueryId();
                    window.windowId = task.getWindowId();
                    window.sliceId = localWindowCounter;
                    task.setWindowId(task.getWindowCount() + 1);
                    task.setWindowEnd(false);
                    //all timebased queries have localwindows
                    localWindows.forEach(localWindow -> {
                        if (localWindow.processList[task.getTaskId()] > 0) {
                            //merge results of local windows into window
                            mergeWindow(task, localWindow, window);
                            //remove the empty local windows and organize rest local windows
                            localWindow.processList[task.getTaskId()] -= 1;
                            localWindow.windowUsedCounterDelete();
                            //how many slices belong to this window
                            window.sliceCounter++;
                        }
                    });
                    windowCollection.windowList.add(window);
                }
            });
            windowCollection.sliceId = localWindowCounter;
            windowCollection.sliceCounter = localWindowProcessCounterofNonDecomposable;
            //delete localwindow
            localWindows.removeIf(localWindow -> localWindow.getLocalWindowCounter() <= 0);
        }else {
            //batch and send median, quantile and countbased even if they are not end
            windowCollection.sliceId = localWindowCounter;
            windowCollection.sliceCounter = localWindowProcessCounterofNonDecomposable;
        }

//        System.out.println(time1);
//        System.out.println(time2);
//        System.out.println(localWindows.size());
//        System.out.println();

        //send window
//        System.out.println(tupleCounter);
//        System.out.println(tupleList.size());
//        System.out.println(windowCollection.windowList.size());
//        System.out.println();

//        if(tupleCounter % 1000000 == 0){
//            System.out.println(tupleCounter);
//            System.out.println(intermediateResultQueue.size());
//        }

        //debug for latency
//        long latencyEnd = System.nanoTime();
//        latencyOverall += (long)(latencyEnd-latencyStart);
//        latencyCounter++;
//        System.out.println("local - latency  " + (double)latencyOverall/latencyCounter);
//        windowCollection.nodeId = (int) (latencyEnd - latencyStart);

//        windowCollection.nodeId = localWindowCounter;

        if(localWindows.size() == 0) {
            windowCollection.nodeId = (int) (System.nanoTime() - latencyStart);
//            System.out.println("local - latency  " + (System.nanoTime() - latencyStart));
//            System.out.println("local - latency  " + windowCollection.nodeId);
        }
        intermediateResultQueue.add(windowCollection);
    }

    void organizeWinodw(WindowCollection windowCollection){
        //there is at least one centralized query
        if(preserveFLAG){
            windowCollection.tuples = tupleList;
            tupleList = new ArrayList<>(conf.localBatchSize);
            if(sortFLAG){
                windowCollection.tuples.sort((a, b) -> Double.compare(a.DATA, b.DATA));
                if(maxFLAG)
                    localWindows.getLast().max = Math.max(localWindows.getLast().max
                            , windowCollection.tuples.get(conf.localBatchSize -1).DATA);
                if(minFLAG)
                    localWindows.getLast().min = Math.min(localWindows.getLast().min
                            , windowCollection.tuples.get(0).DATA);
            }
        }
    }

    void mergeWindow(LocalTask task, LocalWindow localWindow, Window window){
//        if(task.query.getScenario() == conf.DeCentralizedAggregation) {
            switch (task.query.getFunction()) {
                case Configuration.COUNT: {
                    window.count += localWindow.count;
                    break;
                }
                case Configuration.SUM: {
                    window.result += localWindow.sum;
                    break;
                }
                case Configuration.AVERAGE: {
                    window.count += localWindow.count;
                    window.result += localWindow.sum;
                    break;
                }
                case Configuration.MAX: {
                    window.result = localWindow.max;
                    break;
                }
                case Configuration.MIN: {
                    window.result = localWindow.min;
                    break;
                }
                default:
                    break;
            }
//        }
    }

    private void tupleCounterIncrement(){
        tupleCounter++;
    }

    private void localWindowCounterIncrement(){
        localWindowCounter++;
    }

    private void functionBreakToOperators(int function){
        //to analyze operators
        if(function == conf.AVERAGE || function == conf.COUNT) {
            this.operators[conf.COUNTOPERATOR] = true;
        }
        if(function == conf.AVERAGE || function == conf.SUM) {
            this.operators[conf.SUMOPERATOR] = true;
        }
        if(function == conf.QUANTILE || function == conf.MEDIAN ){
            this.operators[conf.SORTOPERATOR] = true;
        }else {
            if (function == conf.MAX) {
                this.operators[conf.MAXOPERATOR] = true;
            }
            if (function == conf.MIN) {
                this.operators[conf.MINOPERATOR] = true;
            }
            if (function == conf.NON) {
                this.operators[conf.PRESERVEOPERATOR] = true;
            }
        }
    }

    private void queryPreProcess(){
        //get all queries, it will be skipped when all queries retrieved
        int queryNumber = conf.queryNumber;
        while(localTasks.size() < queryNumber){
            if(!queryQueue.isEmpty()){
                Query query = (Query) queryQueue.poll();
                //merge same queries
                LocalTask tempLocalTask = localTasks.stream()
                        .filter(localTask -> localTask.query.getEntireQuery().equalsIgnoreCase(query.getEntireQuery()))
                        .findFirst()
                        .orElse(null);
                if(tempLocalTask != null){
                    QuerySub querySub = new QuerySub();
                    querySub.queryId = query.getQueryId();
                    querySub.functionAddition = query.getFunctionAddition();
                    tempLocalTask.querySubs.add(querySub);
                    //end the loop
                    queryNumber--;
                    continue;
                }

                LocalTask task = new LocalTask();
                task.query = query;
//                task.setTaskId(task.query.getQueryId());
                task.setTaskId(task.query.getQueryId());
                //window counter should start from 1,
                // since we use windowId=0 to decide if this query output result to parent
                task.setWindowId(1);
                task.querySubs = new ArrayList<>();
                QuerySub querySub = new QuerySub();
                querySub.queryId = query.getQueryId();
                querySub.functionAddition = query.getFunctionAddition();
                task.querySubs.add(querySub);
                localTasks.add(task);
                //flags are for organizing windows
                //to regulate program, sort -> store,
//                if(task.query.getFunction() == conf.MEDIAN || task.query.getFunction() == conf.QUANTILE ){
//                    this.sortFLAG = true;
//                }
//                if(task.query.getScenario() == conf.CentralizedAggregation){
//                    this.preserveFLAG = true;
//                }
//                if(task.query.getWindowType() == conf.COUNTBASED){
//                    this.countbasedFLAG = true;
//                }
//                if(task.query.getScenario() == conf.MAX){
//                    this.maxFLAG = true;
//                }
//                if(task.query.getScenario() == conf.MIN){
//                    this.minFLAG = true;
//                }
                //for countbased window, we need to save data anyway
                //if there are not meidan aor quantile, we dont perform sorting
//                if(!(task.query.getFunction() == conf.MEDIAN || task.query.getFunction() == conf.QUANTILE)
//                        && task.query.getWindowType() == conf.COUNTBASED){
//                    task.query.setFunction(conf.NON);
//                }
                //analyze aggregation function operators
                functionBreakToOperators(task.query.getFunction());
            }else{
                try {
                    Thread.sleep(conf.queryWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
//        System.out.println(localTasks.size());
//        localTasks.forEach(localTask -> {
//            System.out.println(localTask.query.getQueryId());
//            System.out.println(localTask.querySubs.size());
//        });
//        System.out.println();
//        if(!queryQueue.isEmpty()){
//            LocalTask task = new LocalTask();
//            task.query = (Query) queryQueue.poll();
//            task.setTaskId(task.query.getQueryId());
//            localTasks.add(task);
//        }
    }

}
