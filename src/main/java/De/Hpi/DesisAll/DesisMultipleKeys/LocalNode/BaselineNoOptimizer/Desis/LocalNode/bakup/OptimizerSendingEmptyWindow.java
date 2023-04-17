package De.Hpi.DesisAll.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.LocalNode.bakup;//package De.Hpi.DesisAll.Desis.LocalNode.bakup;
//
//import De.Hpi.DesisAll.Desis.Configure.Configuration;
//import De.Hpi.DesisAll.Desis.Dao.*;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
///*
//1. Median & Quantile
//sorting spends too much time, which affects the output
//
//2. When there is a long gap we send empty window.
// */
//
//public class OptimizerSendingEmptyWindow implements Runnable{
//
//    private Configuration conf;
//    private LinkedList<LocalTask> localTasks;
//    private LinkedList<LocalWindow> localWindows;
//    private LinkedList<Window> windowList;
//    private ArrayList<Tuple> tupleList;
//    private ConcurrentLinkedQueue<Window> intermediateResultQueue;
//    private ConcurrentLinkedQueue<Query> queryQueue;
//    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;
//    private long localWindowCounter;
//    private long tupleCounter;
//
//    private boolean nonDecomposable;
//    private boolean hasCountBased;
//
//    public OptimizerSendingEmptyWindow(Configuration conf, ConcurrentLinkedQueue<Window> intermediateResultQueue,
//                                       ConcurrentLinkedQueue<Query> queryQueue , ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue) {
//        this.conf = conf;
//        this.intermediateResultQueue =intermediateResultQueue;
//        this.queryQueue =queryQueue;
//        this.dataQueue =dataQueue;
//        this.localTasks = new LinkedList<>();
//        this.localWindows = new LinkedList<>();
//        this.windowList = new LinkedList<>();
//        this.tupleList = new ArrayList<>(conf.centralizedBatchSize);
//        this.localWindowCounter = 0;
//        this.tupleCounter = 0;
//        this.nonDecomposable = false;
//        this.hasCountBased = false;
//    }
//
//    public void run() {
//        while(true) {
//            //to read all queries
//            queryPreProcess();
//
//            if (!dataQueue.isEmpty()){
//                ArrayList<Tuple> dataBuffer = dataQueue.poll();
//
//                dataBuffer.forEach(tuple -> {
//                    //get data
//                    long timeTemp = System.currentTimeMillis();
//                    //slice window
//                    LocalisEventHere localisEventHere = isEventHere(tuple, timeTemp);
//
//                    if(localisEventHere.getFinishWindow() > 0){
//                        endWindow(localisEventHere);
//                    }
//                    if(localisEventHere.getCreateNewWindow() > 0){
//                        createWindow(localisEventHere);
//                    }
//                    if(localisEventHere.getProcessWindow() > 0){
//                        //calculate the result the calculate action
//                        processWindow(tuple);
//                    }
//                });
//
//            }
//
//        }
//    }
//
//    //the end tuple of a window always belongs to next window,
//    private LocalisEventHere isEventHere(Tuple tuple, long timeTemp){
//        tupleCounterIncrement();
//        //to record the window state of each query
//        LocalisEventHere localisEventHere = new LocalisEventHere();
//        localisEventHere.setCreateNewWindow(0);
//        localisEventHere.setFinishWindow(0);
//        localisEventHere.setProcessWindow(0);
//        //to break down functions into operators
//        localisEventHere.functions = new boolean[conf.FUNCTIONS];
//        //the window of this query is end and need to be calculated
//        localisEventHere.endList = new boolean[localTasks.size()];
//        //the window of this query is processing
//        localisEventHere.processList = new int[localTasks.size()];
//        //mark down the current state of window that belong to this query
//        localisEventHere.stateList = new int[localTasks.size()];
//        //in case there is a long gap and multiple windows end
//        localisEventHere.multipleWindowEndList = new int[localTasks.size()];
//
//        localTasks.forEach(task -> {
//            //iterate all the query and process the bound of query
//            // decentralized aggregation
////            if(task.query.getScenario() == conf.DeCentralizedAggregation) {
//                switch (task.query.getWindowType()) {
//                    //1------tumbling window
//                    case Configuration.TUMBING: {
//                        if (task.getProcessTime() == 0) {
//                            //for the state
//                            task.windowSliceAdd();
//                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
//                            localisEventHere.addCreateNewWindow();
//                            //initial tumbing window
//                            task.setProcessTime(timeTemp);
//                        } else {
//                            if ((timeTemp - task.getProcessTime()) > task.query.getRange()) {
//                                //for the state
////                                task.windowSliceAdd();
////                                task.windowSliceDelete();
//                                localisEventHere.endList[task.getTaskId()] = true;
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
//                                localisEventHere.addCreateNewWindow();
//                                localisEventHere.addFinishWindow();
//                                localisEventHere.multipleWindowEndList[task.getTaskId()] =
//                                        (int) (timeTemp - task.getProcessTime()) / task.query.getRange();
//                                //in case there is a super long gap
//                                task.setProcessTime(timeTemp - (timeTemp - task.getProcessTime()) % task.query.getRange());
//                            } else {
//                                //for the state
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
//                            }
//                        }
//                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
//                        localisEventHere.addProcessWindow(task.getwindowSlices());
//                        localisEventHere.functions[task.query.getFunction()] = true;
//                        break;
//                    }
//                    //2-----sliding window
//                    case Configuration.SLIDING: {
//                        //initial sliding window
//                        //event time to start window, processing time to end window
//                        if (task.getProcessTime() == 0) {
//                            //for the state
//                            task.windowSliceAdd();
//                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
//                            localisEventHere.addCreateNewWindow();
//                            task.setProcessTime(timeTemp);
//                            task.setEventTime(timeTemp);
//                        } else {
//                            if ((timeTemp - task.getProcessTime()) > task.query.getRange()) {
//                                //for the state
//                                task.windowSliceDelete();
//                                localisEventHere.endList[task.getTaskId()] = true;
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTEND;
//                                localisEventHere.addCreateNewWindow();
//                                localisEventHere.addFinishWindow();
//                                //home many windows if there is a long gap
//                                localisEventHere.multipleWindowEndList[task.getTaskId()] =
//                                        (int) (timeTemp - task.getProcessTime() - task.query.getRange()) / task.query.getSlide() + 1;
//                                //in case there is a super long gap
//                                task.setProcessTime(timeTemp - (timeTemp - task.getProcessTime() - task.query.getRange()) % task.query.getSlide()
//                                        - task.query.getRange() + task.query.getSlide());
//                            }
//                            //if gcd is slide
//                            if ((timeTemp - task.getEventTime()) > task.query.getSlide()) {
//                                //for the state
//                                task.windowSliceAdd();
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
//                                localisEventHere.addCreateNewWindow();
//                                //home many windows if there is a long gap
////                                resultOfIsEventHere.multipleWindowEndList[task.getTaskId()] = (int) (timeTemp - task.getEventTime()) / task.query.getSlide();
//                                //align
//                                task.setEventTime(timeTemp - (timeTemp - task.getEventTime()) % task.query.getSlide());
//                            }
//                            if ((timeTemp - task.getEventTime()) <= task.query.getSlide()
//                                    && (timeTemp - task.getProcessTime()) <= task.query.getRange()) {
//                                //for the state
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
//                            }
//                        }
//                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
//                        localisEventHere.addProcessWindow(task.getwindowSlices());
//                        localisEventHere.functions[task.query.getFunction()] = true;
//                        break;
//                    }
//                    //3-----uneven sliding window (hoping window, slide more than range), this is hopping window
//                    case Configuration.SLIDING_UNEVEN: {
//                        if (task.getProcessTime() == 0) {
//                            task.setProcessTime(timeTemp);
//                            //for the state
//                            task.windowSliceAdd();
//                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
//                            localisEventHere.addCreateNewWindow();
//                        } else {
//                            if ((timeTemp - task.getProcessTime()) > task.query.getRange()) {
//                                //between range > to < slide
//                                if ((timeTemp - task.getProcessTime()) % task.query.getSlide() > task.query.getRange()) {
//                                    //for the state
//                                    if(task.getwindowSlices() == 1) {
//                                        task.windowSliceDelete();
//                                        localisEventHere.endList[task.getTaskId()] = true;
//                                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTEND;
//                                        localisEventHere.addFinishWindow();
//                                        //home many windows if there is a long gap
//                                        localisEventHere.multipleWindowEndList[task.getTaskId()] = (int) (timeTemp - task.getProcessTime()) / task.query.getSlide() + 1;
//                                        task.setProcessTime(timeTemp - (timeTemp - task.getProcessTime()) % task.query.getSlide());
//                                    }else{
//                                        //for the state
//                                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
//                                    }
//                                    break;
//                                    //between 0 to range
//                                } else if((timeTemp - task.getProcessTime()) % task.query.getSlide() > 0){
//                                    //long gap
//                                    if((timeTemp - task.getProcessTime()) / task.query.getSlide() > 1) {
//                                        localisEventHere.endList[task.getTaskId()] = true;
//                                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
//                                        localisEventHere.addCreateNewWindow();
//                                        localisEventHere.addFinishWindow();
//                                        //a long gap before range
//                                        if(task.getwindowSlices() == 1) {
//                                            //task.windowSliceDelete();
//                                            //task.windowSliceAdd();
//                                            localisEventHere.multipleWindowEndList[task.getTaskId()] =
//                                                    (int) (timeTemp - task.getProcessTime()) / task.query.getSlide();
//                                        //a long gap after range
//                                        }else{
//                                            task.windowSliceAdd();
//                                            localisEventHere.multipleWindowEndList[task.getTaskId()] =
//                                                    (int) (timeTemp - task.getProcessTime()) / task.query.getSlide() - 1;
//                                        }
//                                        //in case there is a super long gap
//                                        task.setProcessTime(timeTemp - (timeTemp - task.getProcessTime()) % task.query.getSlide());
//                                    }else{
//                                        task.windowSliceAdd();
//                                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
//                                        localisEventHere.addCreateNewWindow();
//                                        //how many windows if there is a long gap
//                                        //resultOfIsEventHere.multipleWindowEndList[task.getTaskId()] = (int) (timeTemp - task.getProcessTime()) / task.query.getSlide();
//                                        task.setProcessTime(task.getProcessTime() + task.query.getSlide());
//                                    }
//                                }
//                            } else {
//                                //for the state
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
//                            }
//                        }
//                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
//                        localisEventHere.addProcessWindow(task.getwindowSlices());
//                        localisEventHere.functions[task.query.getFunction()] = true;
//                        break;
//                    }
//                    //4-----session window
//                    case Configuration.SESSION: {
//                        if (task.getProcessTime() == 0) {
//                            task.setProcessTime(timeTemp);
//                            //for the state
//                            task.windowSliceAdd();
//                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
//                            localisEventHere.addCreateNewWindow();
//                        } else {
//                            if (timeTemp - task.getProcessTime() > task.query.getSlide()) {
//                                //there is a gap
//                                localisEventHere.endList[task.getTaskId()] = true;
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
//                                localisEventHere.addCreateNewWindow();
//                                localisEventHere.addFinishWindow();
//                                localisEventHere.multipleWindowEndList[task.getTaskId()] = 1;
//                                //in case there is a super long gap
//                                task.setProcessTime(timeTemp);
//                            } else {
//                                //for the state
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
//                                task.setProcessTime(timeTemp);
//                            }
//                        }
//                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
//                        localisEventHere.addProcessWindow(task.getwindowSlices());
//                        localisEventHere.functions[task.query.getFunction()] = true;
//                        break;
//                    }
//                    //5-----punctuation window
//                    case Configuration.PUNCTUATION: {
//                        if (task.getProcessTime() == 0) {
//                            task.setProcessTime(timeTemp);
//                            //for the state
//                            task.windowSliceAdd();
//                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
//                            localisEventHere.addCreateNewWindow();
//                        } else {
//                            if (task.query.getEndPunctuation() == tuple.EVENT) {
//                                //there is a gap
//                                localisEventHere.endList[task.getTaskId()] = true;
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
//                                localisEventHere.addCreateNewWindow();
//                                localisEventHere.addFinishWindow();
//                                localisEventHere.multipleWindowEndList[task.getTaskId()] = 1;
//                                //in case there is a super long gap
//                                task.setProcessTime(timeTemp);
//                            } else {
//                                //for the state
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
//                            }
//                        }
//                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
//                        localisEventHere.addProcessWindow(task.getwindowSlices());
//                        localisEventHere.functions[task.query.getFunction()] = true;
//                        break;
//                    }
//                    //6 -- count based window
//                    //in fact the first tuple which make decision to build
//                    // the sub window is not belong to this sub window, however here is a bug
//                    //so the tuple number is more than batchSize, which equals to batchsize + 1
//                    //batch size is process time
//                    case Configuration.COUNTBASED: {
//                        if (task.getProcessTime() == 0) {
//                            task.setProcessTime(tupleCounter);
//                            //for the state
//                            task.windowSliceAdd();
//                            localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
//                            localisEventHere.addCreateNewWindow();
//                        } else {
////                            if (tupleCounter - task.getProcessTime() > task.query.getBatchSize()) {
//                            if (tupleCounter - task.getProcessTime() >= conf.centralizedBatchSize) {
//                                //for the state
//                                localisEventHere.endList[task.getTaskId()] = true;
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
//                                localisEventHere.addCreateNewWindow();
//                                localisEventHere.addFinishWindow();
//                                localisEventHere.multipleWindowEndList[task.getTaskId()] = 1;
//                                //align
//                                task.setProcessTime(tupleCounter);
//                            } else {
//                                //for the state
//                                localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
//                            }
//                        }
//                        localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
//                        localisEventHere.addProcessWindow(task.getwindowSlices());
////                        localisEventHere.functions[task.query.getFunction()] = true;
//                        localisEventHere.functions[conf.NON] = true;
//                        break;
//                    }
//                    default: {
//                        break;
//                    }
//
//                }
//            // transfer all tuples to root node by batch
////            }else{
////                //batch size is process time
////                if (task.getProcessTime() == 0) {
////                    task.setProcessTime(tupleCounter);
////                    //for the state
////                    task.windowSliceAdd();
////                    localisEventHere.stateList[task.getTaskId()] = conf.EVENTSTART;
////                    localisEventHere.addCreateNewWindow();
////                } else {
////                    if (tupleCounter - task.getProcessTime() > conf.centralizedBatchSize) {
////                        //for the state
////                        localisEventHere.endList[task.getTaskId()] = true;
////                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTENDANDSTART;
////                        localisEventHere.addCreateNewWindow();
////                        localisEventHere.addFinishWindow();
////                        localisEventHere.multipleWindowEndList[task.getTaskId()] = 1;
////                        //align
////                        task.setProcessTime(tupleCounter);
////                    } else {
////                        //for the state
////                        localisEventHere.stateList[task.getTaskId()] = conf.EVENTNOTHING;
////                    }
////                }
////                localisEventHere.processList[task.getTaskId()] = task.getwindowSlices();
////                localisEventHere.addProcessWindow(task.getwindowSlices());
////                localisEventHere.functions[task.query.getFunction()] = true;
////            }
//        });
//        return localisEventHere;
//    }
//
//    private void createWindow(LocalisEventHere localisEventHere){
//        //organize previous local window
////        if(!localWindows.isEmpty())
////            localWindows.getLast().tupleList.sort((a, b) -> Double.compare(a.DATA, b.DATA));
//
//        //instance of new local window
//        localWindowCounterIncrement();
//        LocalWindow localWindow = new LocalWindow();
//        localWindow.setWindowId(localWindowCounter);
//        localWindow.processList = localisEventHere.processList;
//        localWindow.setWindowUsedCounter(localisEventHere.getProcessWindow());
////        localWindow.tupleList = new ArrayList<Tuple>(conf.centralizedBatchSize);
//        localWindow.count = 0;
//        localWindow.sum = 0;
//        localWindow.max = Double.MIN_VALUE;
//        localWindow.min = Double.MAX_VALUE;
//        functionBreakToOperators(localWindow, localisEventHere);
//        localWindows.add(localWindow);
//    }
//
//    private void processWindow(Tuple tuple) {
//        //optimizer can calculate all the queries
//        //decomposable function
//        LocalWindow localWindow = localWindows.getLast();
//        if(localWindow.operators[conf.COUNTOPERATOR]){
//            localWindow.count++;
//        }
//        if(localWindow.operators[conf.SUMOPERATOR]){
//            localWindow.sum += tuple.DATA;
//        }
//        if(localWindow.operators[conf.SINGLEMAXOPERATOR]){
//            localWindow.max = Math.max(localWindow.max, tuple.DATA);
//        }
//        if(localWindow.operators[conf.SINGLEMINOPERATOR]){
//            localWindow.min = Math.min(localWindow.min, tuple.DATA);
//        }
//        if(localWindow.operators[conf.SORTANDSTOREOPERATOR]){
//            tupleList.add(tuple);
//        }
//
//
//        //batch tuples to send, because the maximum packet size of zeromp no more than 50000 bytes.
//        //only when there is no countbased window
//        //tuplelist size > 0 means there are median and quantile functions
//        if(!hasCountBased && tupleList.size() >= conf.centralizedBatchSize){
//            //the tuple list need to be sorted
//            tupleList.sort((a, b) -> Double.compare(a.DATA, b.DATA));
//            batchAndSending(localWindow);
//        }
//    }
//
//    //batch median and quantile tuples
//    void batchAndSending(LocalWindow localWindow){
//        //iterate all local tasks that processed by this local window
//        Window window = new Window();
//        window.queryIdList = new int[conf.queryNumber];
//        localTasks.forEach(task -> {
//            switch (task.query.getFunction()){
//                case Configuration.MAX: {
//                    localWindow.max = Math.max(localWindow.max, tupleList.get(conf.centralizedBatchSize-1).DATA);
//                    break;
//                }
//                case Configuration.MIN: {
//                    localWindow.min = Math.min(localWindow.min, tupleList.get(0).DATA);
//                    break;
//                }
//                case Configuration.MEDIAN: {
//                    //window id for this query
//                    window.queryIdList[task.query.getQueryId()] = task.getWindowCounter();
//                    break;
//                }
//                case Configuration.QUANTILE: {
//                    //window id for this query
//                    window.queryIdList[task.query.getQueryId()] = task.getWindowCounter();
//                    break;
//                }
//                default:
//                    break;
//            }
//        });
////        windowList.add(window);
////        intermediateResultQueue.addAll(windowList);
////        windowList.clear();
//        window.tuples = tupleList;
//        intermediateResultQueue.add(window);
//        tupleList = new ArrayList<>(conf.centralizedBatchSize);
////        tupleList.clear();
////        System.out.println(tupleList.size());
//    }
//
//    void endWindow(LocalisEventHere localisEventHere) {
////        System.out.println(tupleCounter);
////        System.out.println("-----------------------------");
////        System.out.println(localisEventHere.getCreateNewWindow());
////        System.out.println(localisEventHere.getFinishWindow());
////        System.out.println(localisEventHere.getCreateNewWindow());
////        System.out.println(tupleList.size());
////        System.out.println(localWindows.get(0).count);
//
////        localTasks.forEach(task -> {
////        System.out.println(task.getTaskId()
////                + " endList " + resultOfIsEventHere.endList[task.getTaskId()]
////                + "  tuples:  " + tupleCounter
////                + "  localwindows: "  + localWindows.size()
////                + "  slices: "  + task.getwindowSlices()
////                + "  WindowsNeedTobeProcessed: " + resultOfIsEventHere.multipleWindowEndList[task.getTaskId()] );
////        });
////
////        localWindows.forEach(localWindow -> {
////            System.out.print(localWindow.getWindowId()
////                    + " windowUsedCounter " + localWindow.getWindowUsedCounter());
////            System.out.print(" processList ");
////            localTasks.forEach(task -> {
////                System.out.print(" " +localWindow.processList[task.getTaskId()]);
////            });
////            System.out.println();
////        });
//
//        //for centralized aggregation we need to sort tuples anyway
//        if(nonDecomposable | hasCountBased){
////            if(nonDecomposable)
//                tupleList.sort((a, b) -> Double.compare(a.DATA, b.DATA));
//            //send tuples
//            Window windowDecentralized = new Window();
//            windowDecentralized.queryIdList = new int[conf.queryNumber];
//            windowDecentralized.tuples = tupleList;
//            tupleList = new ArrayList<>(conf.centralizedBatchSize);
//
//            //to find the local windows that process this task which is end
//            localWindows.forEach(localWindow -> {
//                //iterate all local tasks that processed by this local window
//                localTasks.forEach(task -> {
//                    if (localisEventHere.endList[task.getTaskId()] &&
//                            localWindow.processList[task.getTaskId()] > 0) {
//                        //create window list
//                        if (task.windowList.isEmpty()) {
//                            for (int i = 0; i < localisEventHere.multipleWindowEndList[task.getTaskId()]; i++) {
//                                Window window = new Window();
////                            window.setWindowId(task.getWindowCounter());
//                                window.queryId = task.query.getQueryId();
////                            window.setScenario(task.query.getScenario());
////                            if(task.query.getScenario() == conf.CentralizedAggregation)
//                            window.tuples = new ArrayList<Tuple>();
////                            window.tupleCounter = tupleCounter;
//                                task.windowList.add(window);
//                                task.windowCounterAdd();
//                            }
//                        }
//                        //cant make sure either number of empty window or processed window is larger
//                        //all processed windows should be merged
//                        if(task.query.getScenario() == conf.DeCentralizedAggregation) {
//                            //cant make sure either number of empty window or processed window is larger
//                            for (int i = 0; i < Math.min(localisEventHere.multipleWindowEndList[task.getTaskId()]
//                                    , localWindow.processList[task.getTaskId()]); i++) {
//                                //merge results of local windows into window
//                                mergeWindow(task, localWindow, task.windowList.get(i));
//                                //remove the empty local windows and organize rest local windows
//                                localWindow.processList[task.getTaskId()] -= 1;
//                                localWindow.windowUsedCounterDelete();
//                            }
//                        }else{
//                            //cant make sure either number of empty window or processed window is larger
//                            for (int i = 0; i < Math.min(localisEventHere.multipleWindowEndList[task.getTaskId()]
//                                    , localWindow.processList[task.getTaskId()]); i++) {
//                                //remove the empty local windows and organize rest local windows
//                                localWindow.processList[task.getTaskId()] -= 1;
//                                localWindow.windowUsedCounterDelete();
//                            }
//                            windowDecentralized.queryIdList[task.query.getQueryId()] = task.getWindowCounter();
//                            task.windowList.remove(0);
//                        }
//                    }
//                });
//            });
//            intermediateResultQueue.add(windowDecentralized);
//        }else {
//            //to find the local windows that process this task which is end
//            localWindows.forEach(localWindow -> {
//                //iterate all local tasks that processed by this local window
//                localTasks.forEach(task -> {
//                    if (localisEventHere.endList[task.getTaskId()] &&
//                            localWindow.processList[task.getTaskId()] > 0) {
//                        //create window list
//                        if (task.windowList.isEmpty()) {
//                            for (int i = 0; i < localisEventHere.multipleWindowEndList[task.getTaskId()]; i++) {
//                                Window window = new Window();
////                            window.setWindowId(task.getWindowCounter());
//                                window.queryId = task.query.getQueryId();
////                            window.setScenario(task.query.getScenario());
////                            if(task.query.getScenario() == conf.CentralizedAggregation)
//                            window.tuples = new ArrayList<Tuple>();
////                            window.tupleCounter = tupleCounter;
//                                task.windowList.add(window);
//                                task.windowCounterAdd();
//                            }
//                        }
//                        //cant make sure either number of empty window or processed window is larger
//                        for (int i = 0; i < Math.min(localisEventHere.multipleWindowEndList[task.getTaskId()]
//                                , localWindow.processList[task.getTaskId()]); i++) {
//                            //merge results of local windows into window
//                            mergeWindow(task, localWindow, task.windowList.get(i));
//                            //remove the empty local windows and organize rest local windows
//                            localWindow.processList[task.getTaskId()] -= 1;
//                            localWindow.windowUsedCounterDelete();
//                        }
//                    }
//                });
//            });
//        }
//        //send window
//        localTasks.forEach(task -> {
//            //to hopping window, if there is long gap after range, windows can not be processed
//            //but it doesn't matter, because hopping window is not important
//            if(localWindows.isEmpty()){
//                if (localisEventHere.endList[task.getTaskId()]) {
//                    if (task.windowList.isEmpty()) {
//                        for(int i = 0; i < localisEventHere.multipleWindowEndList[task.getTaskId()] ; i++){
//                            Window window = new Window();
////                            window.setWindowId(task.getWindowCounter());
//                            window.queryId = task.query.getQueryId();
////                            window.setScenario(task.query.getScenario());
////                            if(task.query.getScenario() == conf.CentralizedAggregation)
//                            window.tuples = new ArrayList<Tuple>();
////                            window.tupleCounter = tupleCounter;
//                            task.windowList.add(window);
//                            task.windowCounterAdd();
//                        }
//                    }
//                }
//            }
//            if(!task.windowList.isEmpty()) {
////                System.out.println(dataQueue.size());
//                intermediateResultQueue.addAll(task.windowList);
//                task.windowList.clear();
//            }
//        });
//        localWindows.removeIf(localWindow -> localWindow.getWindowUsedCounter() == 0);
//    }
//
//    void mergeWindow(LocalTask task, LocalWindow localWindow, Window window){
//        if(task.query.getScenario() == conf.DeCentralizedAggregation) {
//            switch (task.query.getFunction()) {
//                case Configuration.COUNT: {
//                    window.count += localWindow.count;
//                    break;
//                }
//                case Configuration.SUM: {
//                    window.result += localWindow.sum;
//                    break;
//                }
//                case Configuration.AVERAGE: {
//                    window.count += localWindow.count;
//                    window.result += localWindow.sum;
//                    break;
//                }
//                case Configuration.MAX: {
//                    if(nonDecomposable)
//                        window.result = Math.max(window.result, tupleList.get(conf.centralizedBatchSize-1).DATA);
//                    else
//                        window.result = Math.max(window.result, localWindow.max);
//                    break;
//                }
//                case Configuration.MIN: {
//                    if(nonDecomposable)
//                        window.result = Math.min(window.result, tupleList.get(0).DATA);
//                    else
//                        window.result = Math.min(window.result, localWindow.min);
//                    break;
//                }
//                default:
//                    break;
//            }
//        }
////        }else{
////            //merge two list
////            window.tuples.addAll(localWindow.tupleList);
////            window.tuples.sort((a, b) -> Double.compare(a.DATA, b.DATA));
////        }
//    }
//
//    private void tupleCounterIncrement(){
//        tupleCounter++;
//    }
//
//    private void localWindowCounterIncrement(){
//        localWindowCounter++;
//    }
//
//    private void functionBreakToOperators(LocalWindow localWindow, LocalisEventHere localisEventHere){
//        localWindow.operators = new boolean[conf.OPERATORS];
//        //to analyze operators
//        if(localisEventHere.functions[conf.AVERAGE] | localisEventHere.functions[conf.COUNT]) {
//            localWindow.operators[conf.COUNTOPERATOR] = true;
//        }
//        if(localisEventHere.functions[conf.AVERAGE] | localisEventHere.functions[conf.SUM]){
//            localWindow.operators[conf.SUMOPERATOR] = true;
//        }
//        if(localisEventHere.functions[conf.QUANTILE] | localisEventHere.functions[conf.MEDIAN] ){
//            localWindow.operators[conf.SORTANDSTOREOPERATOR] = true;
//        }else {
//            if (localisEventHere.functions[conf.MAX]) {
//                localWindow.operators[conf.SINGLEMAXOPERATOR] = true;
//            }
//            if (localisEventHere.functions[conf.MIN]) {
//                localWindow.operators[conf.SINGLEMINOPERATOR] = true;
//            }
//            if (localisEventHere.functions[conf.NON]) {
//                localWindow.operators[conf.SORTANDSTOREOPERATOR] = true;
//            }
//        }
//    }
//
//    private void queryPreProcess(){
//        //get all queries, it will be skipped when all queries retrieved
//        while(localTasks.size() < conf.queryNumber){
//            if(!queryQueue.isEmpty()){
//                LocalTask task = new LocalTask();
//                task.windowList = new ArrayList<Window>();
//                task.query = (Query) queryQueue.poll();
//                task.setTaskId(task.query.getQueryId());
//                //window counter should start from 1,
//                // since we use windowId=0 to decide if this query output result to parent
//                task.setWindowCounter(1);
//                localTasks.add(task);
//
//                //to regulate program
//                if(task.query.getFunction() == conf.MEDIAN || task.query.getFunction() == conf.QUANTILE ){
//                    this.nonDecomposable = true;
//                }
//
//                if(task.query.getWindowType() == conf.COUNTBASED){
//                    this.hasCountBased = true;
//                }
//
//            }else{
//                try {
//                    Thread.sleep(conf.queryWait);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        if(!queryQueue.isEmpty()){
//            LocalTask task = new LocalTask();
//            task.windowList = new ArrayList<Window>();
//            task.query = (Query) queryQueue.poll();
//            task.setTaskId(task.query.getQueryId());
//            localTasks.add(task);
//        }
//
//    }
//
//}
