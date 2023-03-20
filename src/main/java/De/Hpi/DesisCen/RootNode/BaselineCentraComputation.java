package De.Hpi.DesisCen.RootNode;

import De.Hpi.DesisCen.Configure.Configuration;
import De.Hpi.DesisCen.Dao.*;
import De.Hpi.DesisCen.Dao.BaselineLocalWindow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class BaselineCentraComputation implements Runnable{

    private Configuration conf;
    private ArrayList<LocalTask> localTasks;
    private ArrayList<BaselineLocalWindow> baselineLocalWindows;
    private LinkedList<Tuple> tupleList;
    private ConcurrentLinkedQueue<Window> resultQueue;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;
    private long localWindowCounter;
    private int tupleCounter;
    private long offset;
    private Random random;
    private long previousTimeCounter;


    //for debug
//    private long latencyOverall;
//    private long latencyCounter;


    public BaselineCentraComputation(Configuration conf, ConcurrentLinkedQueue<Window> resultQueue,
                                     ConcurrentLinkedQueue<Query> queryQueue , ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue) {
        this.conf = conf;
        this.resultQueue =resultQueue;
        this.queryQueue =queryQueue;
        this.dataQueue =dataQueue;
        this.localTasks = new ArrayList<>();
        this.baselineLocalWindows = new ArrayList<>();
        this.tupleList = new LinkedList<>();
        this.localWindowCounter = 0;
        this.tupleCounter = 0;
        this.offset = 0;
        this.random = new Random();
        this.previousTimeCounter = 0;

        //for debug
//        this.latencyOverall = 0;
//        this.latencyCounter = 0;

    }
//    long eventHere1 = 0;
//    long eventHere2 = 0;
//    long eventHere3 = 0;
    public void run() {
        //for debug
//        AtomicLong eventTime = new AtomicLong();
//        AtomicLong createTime = new AtomicLong();
//        AtomicLong finishStartTime = new AtomicLong();
        queryPreProcess();
        previousTimeCounter = System.currentTimeMillis();
        while(true) {
            //to read all queries
            if (!dataQueue.isEmpty()){
                ArrayList<Tuple> dataBuffer = dataQueue.poll();
                dataBuffer.forEach(tuple -> {
                    //get data
                    long timeTemp = System.currentTimeMillis();
                    //slice window
//                    long startTime = System.currentTimeMillis();
                    LocalisEventHere localisEventHere = isEventHere(tuple, timeTemp);
//                    eventTime.addAndGet(System.currentTimeMillis() - startTime);

//                    startTime = System.currentTimeMillis();
                    if(localisEventHere.getFinishWindow() > 0){
                        endWindow(localisEventHere);
//                        System.out.println(eventTime.get());
//                        System.out.println(createTime.get());
//                        System.out.println(finishStartTime.get());
//                        System.out.println("hh");
//                        System.out.println(eventHere1);
//                        System.out.println(eventHere2);
//                        System.out.println(eventHere3);
//                        System.out.println("aa");
                    }
//                    finishStartTime.addAndGet(System.currentTimeMillis() - startTime);

//                    startTime = System.currentTimeMillis();
                    if(localisEventHere.getCreateNewWindow() > 0){
                        createWindow(localisEventHere);
                    }
//                    createTime.addAndGet(System.currentTimeMillis() - startTime);
        //                if(localisEventHere.getProcessWindow() > 0){
                    //calculate the result the calculate action
        //                    processWindow(tuple);
        //                }
                });
            }

        }
    }


    //the end tuple of a window always belongs to next window,
    private LocalisEventHere isEventHere(Tuple tuple, long timeTemp){
//        long startTime = System.currentTimeMillis();
        tupleList.add(tuple);
//        eventHere1 += System.currentTimeMillis() - startTime;
//        startTime = System.currentTimeMillis();
        tupleCounterIncrement();
        //to record the window state of each query
        LocalisEventHere localisEventHere = new LocalisEventHere();
        localisEventHere.setCreateNewWindow(0);
        localisEventHere.setFinishWindow(0);
        localisEventHere.setProcessWindow(0);
        //to break down functions into operators
        localisEventHere.functions = new boolean[conf.FUNCTIONS];
        //the window of this query is end and need to be calculated
        localisEventHere.endList = new boolean[localTasks.size()];
        //the window of this query is processing
        localisEventHere.processList = new int[localTasks.size()];
        //mark down the current state of window that belong to this query
        localisEventHere.stateList = new int[localTasks.size()];
        //in case there is a long gap and multiple windows end
        localisEventHere.multipleWindowEndList = new int[localTasks.size()];

//        eventHere2 += System.currentTimeMillis() - startTime;
//        startTime = System.currentTimeMillis();

        localTasks.forEach(task -> {
            //iterate all the query and process the bound of query
            // decentralized aggregation
            switch (task.query.getWindowType()) {
                    //1------tumbling window
                    case Configuration.TUMBING: {
                        if (task.getProcessTime() == 0) {
                            //for the state
                            task.windowSliceAdd();
                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                            localisEventHere.addCreateNewWindow();
                            //initial tumbing window
                            task.setProcessTime(timeTemp);
                        } else {
                            if ((timeTemp - task.getProcessTime()) > task.query.getRange()) {
                                //for the state
//                                task.windowSliceAdd();
//                                task.windowSliceDelete();
                                localisEventHere.endList[task.getTaskId()] = true;
                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
                                localisEventHere.addCreateNewWindow();
                                localisEventHere.addFinishWindow();
                                localisEventHere.multipleWindowEndList[task.getTaskId()] =
                                        (int) (timeTemp - task.getProcessTime()) / task.query.getRange();
                                //in case there is a super long gap
                                task.setProcessTime(timeTemp - (timeTemp - task.getProcessTime()) % task.query.getRange());
                            } else {
                                //for the state
                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
                            }
                        }
                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
                        localisEventHere.addProcessWindow(task.getwindowSlices());
                        localisEventHere.functions[task.query.getFunction()] = true;
                        break;
                    }
                    //2-----sliding window
                    case Configuration.SLIDING: {
                        //initial sliding window
                        //event time to start window, processing time to end window
                        if (task.getProcessTime() == 0) {
                            //for the state
                            task.windowSliceAdd();
                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                            localisEventHere.addCreateNewWindow();
                            task.setProcessTime(timeTemp);
                            task.setEventTime(timeTemp);
                        } else {
                            if ((timeTemp - task.getProcessTime()) > task.query.getRange()) {
                                //for the state
                                task.windowSliceDelete();
                                localisEventHere.endList[task.getTaskId()] = true;
                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTEND;
                                localisEventHere.addCreateNewWindow();
                                localisEventHere.addFinishWindow();
                                //home many windows if there is a long gap
                                localisEventHere.multipleWindowEndList[task.getTaskId()] =
                                        (int) (timeTemp - task.getProcessTime() - task.query.getRange()) / task.query.getSlide() + 1;
                                //in case there is a super long gap
                                task.setProcessTime(timeTemp - (timeTemp - task.getProcessTime() - task.query.getRange()) % task.query.getSlide()
                                        - task.query.getRange() + task.query.getSlide());
                            }
                            //if gcd is slide
                            if ((timeTemp - task.getEventTime()) > task.query.getSlide()) {
                                //for the state
                                task.windowSliceAdd();
                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                                localisEventHere.addCreateNewWindow();
                                //home many windows if there is a long gap
//                                resultOfIsEventHere.multipleWindowEndList[task.getTaskId()] = (int) (timeTemp - task.getEventTime()) / task.query.getSlide();
                                //align
                                task.setEventTime(timeTemp - (timeTemp - task.getEventTime()) % task.query.getSlide());
                            }
                            if ((timeTemp - task.getEventTime()) <= task.query.getSlide()
                                    && (timeTemp - task.getProcessTime()) <= task.query.getRange()) {
                                //for the state
                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
                            }
                        }
                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
                        localisEventHere.addProcessWindow(task.getwindowSlices());
                        localisEventHere.functions[task.query.getFunction()] = true;
                        break;
                    }
                    //3-----uneven sliding window (hoping window, slide more than range), this is hopping window
                    case Configuration.SLIDING_UNEVEN: {
                        if (task.getProcessTime() == 0) {
                            task.setProcessTime(timeTemp);
                            //for the state
                            task.windowSliceAdd();
                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                            localisEventHere.addCreateNewWindow();
                        } else {
                            if ((timeTemp - task.getProcessTime()) > task.query.getRange()) {
                                //between range > to < slide
                                if ((timeTemp - task.getProcessTime()) % task.query.getSlide() > task.query.getRange()) {
                                    //for the state
                                    if(task.getwindowSlices() == 1) {
                                        task.windowSliceDelete();
                                        localisEventHere.endList[task.getTaskId()] = true;
                                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTEND;
                                        localisEventHere.addFinishWindow();
                                        //home many windows if there is a long gap
                                        localisEventHere.multipleWindowEndList[task.getTaskId()] = (int) (timeTemp - task.getProcessTime()) / task.query.getSlide() + 1;
                                        task.setProcessTime(timeTemp - (timeTemp - task.getProcessTime()) % task.query.getSlide());
                                    }else{
                                        //for the state
                                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
                                    }
                                    break;
                                    //between 0 to range
                                } else if((timeTemp - task.getProcessTime()) % task.query.getSlide() > 0){
                                    //long gap
                                    if((timeTemp - task.getProcessTime()) / task.query.getSlide() > 1) {
                                        localisEventHere.endList[task.getTaskId()] = true;
                                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
                                        localisEventHere.addCreateNewWindow();
                                        localisEventHere.addFinishWindow();
                                        //a long gap before range
                                        if(task.getwindowSlices() == 1) {
                                            //task.windowSliceDelete();
                                            //task.windowSliceAdd();
                                            localisEventHere.multipleWindowEndList[task.getTaskId()] =
                                                    (int) (timeTemp - task.getProcessTime()) / task.query.getSlide();
                                            //a long gap after range
                                        }else{
                                            task.windowSliceAdd();
                                            localisEventHere.multipleWindowEndList[task.getTaskId()] =
                                                    (int) (timeTemp - task.getProcessTime()) / task.query.getSlide() - 1;
                                        }
                                        //in case there is a super long gap
                                        task.setProcessTime(timeTemp - (timeTemp - task.getProcessTime()) % task.query.getSlide());
                                    }else{
                                        task.windowSliceAdd();
                                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                                        localisEventHere.addCreateNewWindow();
                                        //how many windows if there is a long gap
                                        //resultOfIsEventHere.multipleWindowEndList[task.getTaskId()] = (int) (timeTemp - task.getProcessTime()) / task.query.getSlide();
                                        task.setProcessTime(task.getProcessTime() + task.query.getSlide());
                                    }
                                }
                            } else {
                                //for the state
                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
                            }
                        }
                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
                        localisEventHere.addProcessWindow(task.getwindowSlices());
                        localisEventHere.functions[task.query.getFunction()] = true;
                        break;
                    }
                    //4-----session window
                    case Configuration.SESSION: {
                        if (task.getProcessTime() == 0) {
                            task.setProcessTime(timeTemp);
                            //for the state
                            task.windowSliceAdd();
                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                            localisEventHere.addCreateNewWindow();
                        } else {
                            if (timeTemp - task.getProcessTime() > task.query.getSlide()) {
                                //there is a gap
                                localisEventHere.endList[task.getTaskId()] = true;
                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
                                localisEventHere.addCreateNewWindow();
                                localisEventHere.addFinishWindow();
                                localisEventHere.multipleWindowEndList[task.getTaskId()] = 1;
                                //in case there is a super long gap
                                task.setProcessTime(timeTemp);
                            } else {
                                //for the state
                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
                                task.setProcessTime(timeTemp);
                            }
                        }
                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
                        localisEventHere.addProcessWindow(task.getwindowSlices());
                        localisEventHere.functions[task.query.getFunction()] = true;
                        break;
                    }
                    //5-----punctuation window
                    case Configuration.PUNCTUATION: {
                        if (task.getProcessTime() == 0) {
                            task.setProcessTime(timeTemp);
                            //for the state
                            task.windowSliceAdd();
                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                            localisEventHere.addCreateNewWindow();
                        } else {
                            //to simulate punctuation window
                            // if (task.query.getEndPunctuation() == tuple.EVENT) {
                            if(timeTemp - previousTimeCounter >= 1000.0 / task.query.getEndPunctuation()) {
                                if (random.nextInt((int) (task.query.getEndPunctuation()
                                        / (timeTemp - previousTimeCounter)) + 1) == 1) {
                                    //there is a gap
                                    localisEventHere.endList[task.getTaskId()] = true;
                                    localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
                                    localisEventHere.addCreateNewWindow();
                                    localisEventHere.addFinishWindow();
                                    localisEventHere.multipleWindowEndList[task.getTaskId()] = 1;
                                    //in case there is a super long gap
                                    task.setProcessTime(timeTemp);
                                } else {
                                    //for the state
                                    localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
                                }
                                previousTimeCounter = System.currentTimeMillis();
                            }
                        }
                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
                        localisEventHere.addProcessWindow(task.getwindowSlices());
                        localisEventHere.functions[task.query.getFunction()] = true;
                        break;
                    }
                    //6 -- count based window
                    //in fact the first tuple which make decision to build
                    // the sub window is not belong to this sub window, however here is a bug
                    //so the tuple number is more than batchSize, which equals to batchsize + 1
                    //batch size is process time
                    case Configuration.COUNTBASED: {
                        if (task.getProcessTime() == 0) {
                            task.setProcessTime(tupleCounter);
                            //for the state
                            task.windowSliceAdd();
                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
                            localisEventHere.addCreateNewWindow();
                        } else {
                            if (tupleCounter - task.getProcessTime() > task.query.getRange()) {
                                //for the state
                                localisEventHere.endList[task.getTaskId()] = true;
                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
                                localisEventHere.addCreateNewWindow();
                                localisEventHere.addFinishWindow();
                                localisEventHere.multipleWindowEndList[task.getTaskId()] = 1;
                                //align
                                task.setProcessTime(tupleCounter);
                            } else {
                                //for the state
                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
                            }
                        }
                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
                        localisEventHere.addProcessWindow(task.getwindowSlices());
                        localisEventHere.functions[task.query.getFunction()] = true;
                        break;
                    }
                    default: {
                        break;
                    }

                }
                // centralized aggregation
        });
//        eventHere3 += System.currentTimeMillis() - startTime;
        return localisEventHere;
    }

    private void createWindow(LocalisEventHere localisEventHere){
        //organize previous local window
        if(!baselineLocalWindows.isEmpty())
            baselineLocalWindows.get(baselineLocalWindows.size()-1).endIndex = tupleCounter;

        //instance of new local window
        localWindowCounterIncrement();
        BaselineLocalWindow baselineLocalWindow = new BaselineLocalWindow();
        baselineLocalWindow.startIndex = tupleCounter;
        baselineLocalWindow.setWindowId(localWindowCounter);
        baselineLocalWindow.processList = localisEventHere.processList;
        baselineLocalWindow.setWindowUsedCounter(localisEventHere.getProcessWindow());
        baselineLocalWindow.tupleList = new ArrayList<Tuple>();
        functionBreakToOperators(baselineLocalWindow, localisEventHere);
        baselineLocalWindows.add(baselineLocalWindow);
    }

//    private void processWindow(Tuple tuple) {
    //optimizer can calculate all the queries
    //decomposable function
//        BaselineLocalWindow baselineLocalWindow = baselineLocalWindows.getLast();
//        if(baselineLocalWindow.operators[conf.COUNTOPERATOR]){
//            baselineLocalWindow.count++;
//        }
//        if(baselineLocalWindow.operators[conf.SUMOPERATOR]){
//            baselineLocalWindow.sum += tuple.DATA;
//        }
//        if(baselineLocalWindow.operators[conf.SORTOPERATOR]){
//            baselineLocalWindow.tupleList.add(tuple);
//        }
//    }
//    long partT1 = 0;
//    long partT11 = 0;
//    long partT12 = 0;
//    long partT2 = 0;

    long latencyAll = 0;
    float latencyCount = 0;


    void endWindow(LocalisEventHere localisEventHere) {

//        for debug
        long latencyStart = System.nanoTime();

//        localTasks.forEach(task -> {
//        System.out.println(task.getTaskId()
//                + " endList " + localisEventHere.endList[task.getTaskId()]
//                + "  tuples:  " + tupleCounter
//                + "  localwindows: "  + baselineLocalWindows.size()
//                + "  slices: "  + task.getwindowSlices()
//                + "  WindowsNeedTobeProcessed: " + localisEventHere.multipleWindowEndList[task.getTaskId()] );
//        });
//
//        baselineLocalWindows.forEach(localWindow -> {
//            System.out.print(localWindow.getWindowId()
//                    + " windowUsedCounter " + localWindow.getWindowUsedCounter()
//                    + " 1 " + localWindow.startIndex
//                    + " 2 " + localWindow.endIndex);
//            System.out.print(" processList ");
//            localTasks.forEach(task -> {
//                System.out.print(" " +localWindow.processList[task.getTaskId()]);
//            });
//            System.out.println();
//        });
//        long startTime = System.currentTimeMillis();
        //to find the local windows that process this task which is end
        baselineLocalWindows.forEach(localWindow -> {
            //iterate all local tasks that processed by this local window
            localTasks.forEach(task -> {
                if (localisEventHere.endList[task.getTaskId()] &&
                        localWindow.processList[task.getTaskId()] > 0) {
//                    long startTimeT = System.currentTimeMillis();
                    //create window list
                    if (task.windowList.isEmpty()) {
                        for(int i = 0; i < localisEventHere.multipleWindowEndList[task.getTaskId()] ; i++){
                            task.windowCounterAdd();
                            Window window = new Window();
                            window.setWindowId(task.getWindowCounter());
                            window.setQueryId(task.query.getQueryId());
//                            if(task.query.getScenario() == conf.CentralizedAggregation)
                            window.tuples = new ArrayList<Tuple>();
//                            window.tupleCounter = tupleCounter;
                            task.windowList.add(window);
                        }
                    }
//                    partT11 += System.currentTimeMillis() - startTimeT;
//                    startTimeT = System.currentTimeMillis();
                    //cant make sure either number of empty window or processed window is larger
                    for(int i = 0; i < Math.min(localisEventHere.multipleWindowEndList[task.getTaskId()]
                            , localWindow.processList[task.getTaskId()]); i++){
                        //merge results of local windows into window
                        mergeWindow(task, localWindow, task.windowList.get(i));
                        //remove the empty local windows and organize rest local windows
                        localWindow.processList[task.getTaskId()] -= 1;
                        localWindow.windowUsedCounterDelete();
                    }
//                    partT12 += System.currentTimeMillis() - startTimeT;
                }
            });
        });
//        partT1 += System.currentTimeMillis() - startTime;
//        startTime = System.currentTimeMillis();
        //send window
        localTasks.forEach(task -> {
            //to hopping window, if there is long gap after range, windows can not be processed
            if(baselineLocalWindows.isEmpty()){
                if (localisEventHere.endList[task.getTaskId()]) {
                    if (task.windowList.isEmpty()) {
                        for(int i = 0; i < localisEventHere.multipleWindowEndList[task.getTaskId()] ; i++){
                            task.windowCounterAdd();
                            Window window = new Window();
                            window.setWindowId(task.getWindowCounter());
                            window.setQueryId(task.query.getQueryId());
//                            if(task.query.getScenario() == conf.CentralizedAggregation)
                            window.tuples = new ArrayList<Tuple>();
//                            window.tupleCounter = tupleCounter;
                            task.windowList.add(window);
                        }
                    }
                }
            }

            task.windowList.forEach(windowlistTask -> {
                if(!windowlistTask.tuples.isEmpty())
                    calculate(task, windowlistTask);
            });

            if(!task.windowList.isEmpty()) {
                task.windowList.get(0).setNodeId((int) localWindowCounter);


                resultQueue.addAll(task.windowList);
//                System.out.println("Wtuplist   " + task.windowList.get(0).tuples.size());

//                long latencyEnd = System.nanoTime();
//                latencyOverall += (long)(latencyEnd-latencyStart);
//                latencyCounter++;
//                System.out.println("local - latency  " + (double)latencyOverall/latencyCounter);


                resetTupleList();
//                System.out.println("tuplist   " + tupleList.size());
                task.windowList.clear();
            }
        });
//        partT2 += System.currentTimeMillis() - startTime;
//        System.out.println(baselineLocalWindows.size());
        baselineLocalWindows.removeIf(localWindow -> localWindow.getWindowUsedCounter() == 0);
//        System.out.println("part - S");
//        System.out.println(partT1);
//        System.out.println(partT2);
//        System.out.println(partT11);
//        System.out.println(partT12);
//        System.out.println("part - E");
//        System.out.println(baselineLocalWindows.size());
        //correction
//        if(baselineLocalWindows.isEmpty()){
//            tupleCounter = 0;
//            tupleList.clear();
//        }else{
//            int correctionParameter = baselineLocalWindows.getFirst().startIndex;
//            tupleCounter -= correctionParameter;
//            tupleList = new LinkedList<Tuple>(tupleList.subList(correctionParameter, tupleList.size()));
//            baselineLocalWindows.forEach(baselineLocalWindow -> {
//                baselineLocalWindow.startIndex -= correctionParameter;
//                baselineLocalWindow.endIndex -= correctionParameter;
//            });
//        }
        latencyAll += (System.nanoTime() - latencyStart);
        latencyCount++;
        System.out.println("latency:  " + latencyAll/latencyCount);

    }

    void mergeWindow(LocalTask task, BaselineLocalWindow baselineLocalWindow, Window window){
        if(baselineLocalWindow.endIndex == 0)
            baselineLocalWindow.endIndex = tupleCounter;

//        System.out.println("tuplist   " + localTasks.size());
//        System.out.println("tuplist   " + baselineLocalWindows.size());
//        System.out.println("tuplist   " + tupleList.size());
//        System.out.println("sidx   " + baselineLocalWindow.startIndex);
//        System.out.println("eidx   " + baselineLocalWindow.endIndex);
//        System.out.println("sidxO   " + (int) (baselineLocalWindow.startIndex - offset));
//        System.out.println("eidxO   " + (int) (baselineLocalWindow.endIndex - offset));
//        System.out.println(offset);
//        System.out.println(window.getWindowId());
//        System.out.println();

        List<Tuple> tupleTempList = tupleList.subList((int) Math.max((baselineLocalWindow.startIndex - offset)-1, 0)
                , (int) Math.max((baselineLocalWindow.endIndex - offset)-1, 0));
        window.tuples.addAll(tupleTempList);
    }

    void resetTupleList(){
        long newOffset = baselineLocalWindows.get(0).startIndex;
        for(int i = 0; i < baselineLocalWindows.size(); i++){
            newOffset = Math.min(newOffset, baselineLocalWindows.get(i).startIndex);
        }

//        System.out.println("newOffset   " + newOffset);
//        System.out.println("offset   " + offset);
//        System.out.println("tuplist   " + tupleList.size());

        tupleList = new LinkedList<>( tupleList.subList((int) (newOffset - offset), tupleList.size()));

//        System.out.println("tuplist   " + tupleList.size());
//        System.out.println();

        offset = newOffset;
    }

    void calculate(LocalTask task, Window window){
        switch (task.query.getFunction()) {
            case Configuration.COUNT: {
                window.count= window.tuples.size();
                window.tuples.clear();
                break;
            }
            case Configuration.SUM: {
                window.result= window.tuples.stream().map(e -> e.DATA).reduce((double) 0, Double::sum);
                window.tuples.clear();
                break;
            }
            case Configuration.AVERAGE: {
                window.count= window.tuples.size();
                window.result= window.tuples.stream().map(e -> e.DATA).reduce((double) 0, Double::sum);
                window.tuples.clear();
                break;
            }
            case Configuration.MAX: {
                window.result = window.tuples.stream().map(e -> e.DATA)
                        .mapToDouble(Double::doubleValue).max().getAsDouble();
                window.tuples.clear();
                break;
            }
            case Configuration.MIN: {
                window.result = window.tuples.stream().map(e -> e.DATA)
                        .mapToDouble(Double::doubleValue).min().getAsDouble();
                window.tuples.clear();
                break;
            }
            case Configuration.QUANTILE: {
                window.tuples.sort((a, b) -> Double.compare(a.DATA, b.DATA));
                int index = (int) Math.max((window.tuples.size() - 1)  *  task.query.getFunctionAddition(), 0);
                window.result = window.tuples.get(index).DATA;
                window.tuples.clear();
                break;
            }
            case Configuration.MEDIAN: {
                window.tuples.sort((a, b) -> Double.compare(a.DATA, b.DATA));
                int index = window.tuples.size() / 2;
                window.result = window.tuples.get(index).DATA;
                window.tuples.clear();
                break;
            }
            default:
                break;
        }
    }

    private void tupleCounterIncrement(){
        tupleCounter++;
    }

    private void localWindowCounterIncrement(){
        localWindowCounter++;
    }

    private void functionBreakToOperators(BaselineLocalWindow baselineLocalWindow, LocalisEventHere localisEventHere){
        baselineLocalWindow.operators = new boolean[conf.OPERATORS];
        if(localisEventHere.functions[conf.AVERAGE] | localisEventHere.functions[conf.COUNT]) {
            baselineLocalWindow.operators[conf.COUNTOPERATOR] = true;
        }
        if(localisEventHere.functions[conf.AVERAGE] | localisEventHere.functions[conf.SUM]){
            baselineLocalWindow.operators[conf.SUMOPERATOR] = true;
        }
        if(localisEventHere.functions[conf.MAX] | localisEventHere.functions[conf.MIN]
                | localisEventHere.functions[conf.QUANTILE] | localisEventHere.functions[conf.MEDIAN] ){
            baselineLocalWindow.operators[conf.SORTOPERATOR] = true;
        }
    }

    private void queryPreProcess(){
        //get all queries, it will be skipped when all queries retrieved
        while(localTasks.size() < conf.queryNumber){
            if(!queryQueue.isEmpty()){
                LocalTask task = new LocalTask();
                task.windowList = new ArrayList<Window>();
                task.query = (Query) queryQueue.poll();
                task.setTaskId(task.query.getQueryId());
                localTasks.add(task);
            }else{
                try {
                    Thread.sleep(conf.queryWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
//        if(!queryQueue.isEmpty()){
//            LocalTask task = new LocalTask();
//            task.windowList = new ArrayList<Window>();
//            task.query = (Query) queryQueue.poll();
//            task.setTaskId(task.query.getQueryId());
//            localTasks.add(task);
//        }

    }

}
