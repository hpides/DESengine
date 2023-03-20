package De.Hpi.DesisMultipleKeys.RootNode;

import De.Hpi.DesisMultipleKeys.Dao.*;
import De.Hpi.DesisMultipleKeys.Configure.Configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class RootComputationEngineDecentral implements Runnable {

    private Configuration conf;
    private ConcurrentLinkedQueue<WindowCollection> resultQueue;
    private ConcurrentLinkedQueue<WindowCollection> resultFromIntermedia;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private ArrayList<RootTask> rootTasks;
    private LinkedList<TupleBatch> tupleBatches;
    private int currentSliceId;
    private long tupleCounter;
    private boolean countBasedFlag;
    private boolean timeBasedNonDecomposableFlag;

    //for debug
//    private long latencyOverall;
//    private long latencyCounter;

    RootComputationEngineDecentral(ConcurrentLinkedQueue<WindowCollection> resultFromIntermedia, Configuration conf,
                                   ConcurrentLinkedQueue<WindowCollection> resultQueue,
                                   ConcurrentLinkedQueue<Query> queryQueue){
        this.conf = conf;
        this.resultQueue =resultQueue;
        this.resultFromIntermedia =resultFromIntermedia;
        this.rootTasks = new ArrayList<>();
        this.tupleBatches = new LinkedList<>();
        this.queryQueue = queryQueue;
        this.currentSliceId = 0;
        this.tupleCounter = 0;
        this.countBasedFlag = false;
        this.timeBasedNonDecomposableFlag = false;

        //for debug
//        this.latencyOverall = 0;
//        this.latencyCounter = 0;

    }

    public void run() {
        queryPreProcess();
        while (true) {
            if (!resultFromIntermedia.isEmpty()) {
                //to read all queries
                //get intermediate result from local nodes
                WindowCollection windowCollection = resultFromIntermedia.poll();
                long timeTemp = System.currentTimeMillis();
                windowProcess(windowCollection, timeTemp);
            }
        }
    }

    //can not process duplicate windows
    private void windowProcess(WindowCollection windowCollection, long timeTemp){
        //local window
        //the all scenarios are,
        // 1)empty, 2)less window arrived, put it into list
        // 3) all window arrived 4)expired, process and send them all
        // 5) old window arrived throw
        // 6)disorder window, keep it

        //the windows that have same window ids should be in a same intermediate window
        //and the intermediate window is to collect same windowid windows that from different nodes.
        //windowCounter 1. >wc save 2. <wc drop 3. =wc process
        //drop < windowcounter


        //debug for latency
//        long latencyStart = System.nanoTime();

        WindowCollection newWindowCollection = new WindowCollection();
        newWindowCollection.windowList = new ArrayList<>();
        newWindowCollection.tuples = new ArrayList<>();

        boolean newTupleBatch = false;
        //save tuple batch, if new id less than old id, these are disorder data just drop
        if(currentSliceId <= windowCollection.sliceId) {
            //can process timebased nondecomposable function
            if(timeBasedNonDecomposableFlag) {
                TupleBatch tupleBatch = new TupleBatch();
                tupleBatch.sliceId = windowCollection.sliceId;
                tupleBatch.sliceCounter = windowCollection.sliceCounter;
                tupleBatch.tuples = windowCollection.tuples;
                tupleBatches.addFirst(tupleBatch);
            }

            //can process countbased window
            if(countBasedFlag){
                newTupleBatch = true;
            }
            tupleCounter += windowCollection.tuples.size();
            //drop slices
            if(currentSliceId < windowCollection.sliceId){
                currentSliceId = windowCollection.sliceId;
            }
        }

        windowCollection.windowList.forEach(window -> {
            RootTask task = rootTasks.get(window.queryId);
            //time basedwindow
            if(task.query.getWindowType() != conf.COUNTBASED) {
                //5) old window arrived throw, or find aim intermediate window
                if (task.getWindowCounter() <= window.windowId) {
                    //process median or quantile
//                    if(task.query.getScenario() == conf.CentralizedAggregation){
//                        task.batchList.add(windowCollection.tuples);
//                    }
                    //if there is only child node
                    if(conf.localNumber / conf.intermediaNumber - 1 == 0){
                        //process and send window
//                        newWindowCollection.windowList.add(window);
                        //calculate final result and send it
                        calculateWindow(task, window, newWindowCollection);
                        //update task
                        task.windowCounterAdd();
                        return;
                    }
                    //if we need to build a new window
                    boolean isNewWindow = true;
                    //clean the expired intermediate windows, and find the aim one
                    Iterator<RootWindow> iter = task.rootWindows.iterator();
                    while (iter.hasNext()) {
                        RootWindow rootWindow = iter.next();
                        //4)expired, process and send them all
                        if (timeTemp - rootWindow.getProcessTime() > conf.EXPIREDTIME) {
                            //send it
//                            newWindowCollection.windowList.add(rootWindow.window);
                            //calculate final result and send it
                            calculateWindow(task, rootWindow.window, newWindowCollection);
                            //update task
                            iter.remove();
                            task.windowCounterAdd();
                        } else if (rootWindow.getWindowId() == window.windowId) {
                            //we find aim intermediate window
                            //this is not first window
                            rootWindow.deleteWindowWaitingCounter();
                            mergeWindow(task, rootWindow.window, window);
                            //2) less window arrived, still need to wait
                            //3) all window arrived
                            if (rootWindow.getWindowWaitCounter() == 0) {
                                //process and send window
//                            newWindowCollection.windowList.add(rootWindow.window);
                                //calculate final result and send it
                                calculateWindow(task, rootWindow.window, newWindowCollection);
                                //update task
                                iter.remove();
                                task.windowCounterAdd();
                                //process median and quantile
                            }
                            isNewWindow = false;
                            break;
                        }
                    }
                    //1) empty, 6)disorder window, keep it, create a new intermediate window
                    if (isNewWindow) {
                        RootWindow rootWindow = new RootWindow();
                        rootWindow.setWindowId(window.windowId);
                        rootWindow.setProcessTime(timeTemp);
                        rootWindow.setWindowWaitCounter(conf.localNumber / conf.intermediaNumber - 1);
                        rootWindow.window = window;
                        task.rootWindows.add(rootWindow);
                    }
                }
            }
        });

        //countbased window, the windowlist of windowCollcetion is empty
        if(countBasedFlag && newTupleBatch){
            rootTasks.forEach(task -> {
                //filter countbased window
                if(task.query.getWindowType() != conf.COUNTBASED){
                    return;
                }
                //for count based window
                Window window = task.rootWindows.get(0).window;
                //window end nonDecomposable
                if(task.query.getFunction() == conf.MEDIAN || task.query.getFunction() == conf.QUANTILE){
                    //window end
                    if(tupleCounter - window.count
                            >= task.query.getRange()){
                        //calculate end position
                        int endPosition = (int) (windowCollection.tuples.size() - (tupleCounter - window.count - task.query.getRange()));
                        //calculate final result and send it
                        calculateWindowCountBased(task, window, newWindowCollection,
                                0, endPosition-1, windowCollection.tuples);
                        //update new window
                        task.windowCounterAdd();
                        window.windowId = task.getWindowCounter();
                        window.result = 0;
                        window.count += task.query.getRange();
                        task.tupleLists.add(new ArrayList(windowCollection.tuples.stream()
                                .skip(endPosition).limit(windowCollection.tuples.size()).collect(Collectors.toList())));
                    }else{
                        task.tupleLists.add(windowCollection.tuples);
                    }
                }else{
                    //window end decomposable
                    if(tupleCounter - window.count
                            >= task.query.getRange()){
                        //calculate
                        int endPosition = (int) (windowCollection.tuples.size() - (tupleCounter - window.count - task.query.getRange()));
                        //calculate final result and send it
                        mergeWindowCountBased(task, window,
                                0, endPosition-1, windowCollection.tuples);
                        //send window
                        calculateWindowCountBased(task, window, newWindowCollection,
                                0, endPosition-1, windowCollection.tuples);

                        //update new window
                        task.windowCounterAdd();
                        window.windowId = task.getWindowCounter();
                        window.result = 0;
                        mergeWindowCountBased(task, window,
                                endPosition, windowCollection.tuples.size()-1, windowCollection.tuples);
                        window.count += task.query.getRange();
                    }else {
                        mergeWindowCountBased(task, window,
                                0, windowCollection.tuples.size()-1, windowCollection.tuples);
                    }
                }
            });
        }

        if(!newWindowCollection.windowList.isEmpty()) {

            //debug for latency
//            long latencyEnd = System.nanoTime();
//            latencyOverall += (long)(latencyEnd-latencyStart);
//            latencyCounter++;
//            System.out.println("root - latency  " + (double)latencyOverall/latencyCounter);
//            newWindowCollection.nodeId = (int)(endLatency - startLatency);
            resultQueue.add(newWindowCollection);
        }

    }

    void mergeWindow(RootTask task, Window window, Window newWindow){
        if(task.query.getScenario() == conf.DeCentralizedAggregation) {
            switch (task.query.getFunction()) {
                case Configuration.COUNT: {
                    window.count+= newWindow.count;
                    break;
                }
                case Configuration.SUM: {
                    window.result+= newWindow.result;
                    break;
                }
                case Configuration.AVERAGE: {
                    window.count+= newWindow.count;
                    window.result+= newWindow.result;
                    break;
                }
                case Configuration.MAX: {
                    window.result = Math.max(window.result, newWindow.result);
                    break;
                }
                case Configuration.MIN: {
                    window.result = Math.min(window.result, newWindow.result);
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void calculateWindowDecomposableFunction(RootTask task, Window window, WindowCollection windowCollection){
        task.querySubs.forEach(querySub -> {
            Window tempWindow = new Window();
            tempWindow.queryId = querySub.queryId;
            tempWindow.windowId = window.windowId;
            tempWindow.result = window.result;
            windowCollection.windowList.add(tempWindow);
        });
    }

    private void calculateWindow(RootTask task, Window window, WindowCollection windowCollection){
        switch (task.query.getFunction()) {
            case Configuration.COUNT: {
                window.result = window.count;
                calculateWindowDecomposableFunction(task, window, windowCollection);
                break;
            }
            case Configuration.SUM: {
//                window.result = window.result;
                calculateWindowDecomposableFunction(task, window, windowCollection);
                break;
            }
            case Configuration.AVERAGE: {
                window.result = window.result / window.count;
                calculateWindowDecomposableFunction(task, window, windowCollection);
                break;
            }
            case Configuration.MEDIAN: {
                ArrayList<Tuple> tuplesTemp = new ArrayList<>();
                tupleBatches.forEach(tupleBatch -> {
                    if(tupleBatch.sliceId > window.sliceId - window.sliceCounter && tupleBatch.sliceId <= window.sliceId){
                        tupleBatch.sliceCounter--;
                        tuplesTemp.addAll(tupleBatch.tuples);
                    }
                });
                tupleBatches.removeIf(tupleBatch -> tupleBatch.sliceCounter <= 0);
                tuplesTemp.sort((a, b) -> Double.compare(a.DATA, b.DATA));
                window.result = tuplesTemp.get((tuplesTemp.size() - 1) / 2).DATA;
                task.querySubs.forEach(querySub -> {
                    Window tempWindow = new Window();
                    tempWindow.queryId = querySub.queryId;
                    tempWindow.windowId = window.windowId;
                    tempWindow.result = window.result;
                    windowCollection.windowList.add(tempWindow);
                });
                break;
            }
            case Configuration.QUANTILE: {
                ArrayList<Tuple> tuplesTemp = new ArrayList<>();
                tupleBatches.forEach(tupleBatch -> {
                    if(tupleBatch.sliceId > window.sliceId - window.sliceCounter && tupleBatch.sliceId <= window.sliceId){
                        tupleBatch.sliceCounter--;
                        tuplesTemp.addAll(tupleBatch.tuples);
                    }
                });
                tupleBatches.removeIf(tupleBatch -> tupleBatch.sliceCounter <= 0);
                tuplesTemp.sort((a, b) -> Double.compare(a.DATA, b.DATA));
                task.querySubs.forEach(querySub -> {
                    Window tempWindow = new Window();
                    tempWindow.queryId = querySub.queryId;
                    tempWindow.windowId = window.windowId;
                    tempWindow.result = tuplesTemp.get((int) ((tuplesTemp.size() - 1)  * querySub.functionAddition)).DATA;
                    windowCollection.windowList.add(tempWindow);
                });
                break;
            }
            case Configuration.MAX:{
                calculateWindowDecomposableFunction(task, window, windowCollection);
            }
            case Configuration.MIN:{
                calculateWindowDecomposableFunction(task, window, windowCollection);
            }
            default:
                break;
        }
    }

    private void calculateWindowCountBased(RootTask task, Window window, WindowCollection windowCollection
            , int startPoint, int endPoint, ArrayList<Tuple> newestTuples){
        switch (task.query.getFunction()) {
            case Configuration.COUNT: {
                window.result = window.count;
                calculateWindowDecomposableFunction(task, window, windowCollection);
                break;
            }
            case Configuration.SUM: {
//                window.result = window.result;
                calculateWindowDecomposableFunction(task, window, windowCollection);
                break;
            }
            case Configuration.AVERAGE: {
                window.result = window.result / task.query.getRange();
                calculateWindowDecomposableFunction(task, window, windowCollection);
                break;
            }
            case Configuration.MEDIAN: {
                ArrayList<Tuple> tuplesTemp = new ArrayList<>();
                task.tupleLists.forEach(tuplesTemp::addAll);
                tuplesTemp.addAll(new ArrayList(newestTuples.stream().skip(startPoint).limit(endPoint).collect(Collectors.toList())));
                task.tupleLists = new ArrayList<>();
                tuplesTemp.sort((a, b) -> Double.compare(a.DATA, b.DATA));
                window.result = tuplesTemp.get((tuplesTemp.size() - 1) / 2).DATA;

                task.querySubs.forEach(querySub -> {
                    Window tempWindow = new Window();
                    tempWindow.queryId = querySub.queryId;
                    tempWindow.windowId = window.windowId;
                    tempWindow.result = window.result;
                    windowCollection.windowList.add(tempWindow);
                });
                break;
            }
            case Configuration.QUANTILE: {
                ArrayList<Tuple> tuplesTemp = new ArrayList<>();
                task.tupleLists.forEach(tuplesTemp::addAll);
                tuplesTemp.addAll(new ArrayList(newestTuples.stream().skip(startPoint).limit(endPoint).collect(Collectors.toList())));
                task.tupleLists = new ArrayList<>();
                tuplesTemp.sort((a, b) -> Double.compare(a.DATA, b.DATA));
                window.result = tuplesTemp.get((tuplesTemp.size() - 1) / 2).DATA;

                task.querySubs.forEach(querySub -> {
                    Window tempWindow = new Window();
                    tempWindow.queryId = querySub.queryId;
                    tempWindow.windowId = window.windowId;
                    tempWindow.result = tuplesTemp.get((int) ((tuplesTemp.size() - 1)  * querySub.functionAddition)).DATA;
                    windowCollection.windowList.add(tempWindow);
                });
                break;
            }
            case Configuration.MAX:{
                calculateWindowDecomposableFunction(task, window, windowCollection);
            }
            case Configuration.MIN:{
                calculateWindowDecomposableFunction(task, window, windowCollection);
            }
            default:
                break;
        }
    }

    void mergeWindowCountBased(RootTask task, Window window, int first, int end, ArrayList<Tuple> tuples){
        switch (task.query.getFunction()) {
            case Configuration.SUM:
            case Configuration.AVERAGE: {
                for(int i = first; i <= end; i++){
                    window.result += tuples.get(i).DATA;
                }
                break;
            }
            case Configuration.MAX: {
                for(int i = first; i <= end; i++){
                    window.result = Math.max(window.result, tuples.get(i).DATA);
                }
                break;
            }
            case Configuration.MIN: {
                for(int i = first; i <= end; i++){
                    window.result = Math.min(window.result, tuples.get(i).DATA);
                }
                break;
            }
            default:
                break;
        }

    }

    private void queryPreProcess(){
        //get all queries, it will be skipped when all queries retrieved
        int queryNumber = conf.queryNumber;
        while(rootTasks.size() < queryNumber){
            if(!queryQueue.isEmpty()){
                Query query = (Query) queryQueue.poll();
                //merge same queries
                RootTask tempRootTask = rootTasks.stream()
                        .filter(rootTask -> rootTask.query.getEntireQuery().equalsIgnoreCase(query.getEntireQuery()))
                        .findFirst()
                        .orElse(null);
                if(tempRootTask != null){
                    QuerySub querySub = new QuerySub();
                    querySub.queryId = query.getQueryId();
                    querySub.functionAddition = query.getFunctionAddition();
                    tempRootTask.querySubs.add(querySub);
                    //end the loop
                    queryNumber--;
                    continue;
                }
                RootTask task = new RootTask();
                task.query = query;
                task.setTaskId(task.query.getQueryId());
                task.setWindowCounter(1);
                task.querySubs = new ArrayList<>();
                QuerySub querySub = new QuerySub();
                querySub.queryId = query.getQueryId();
                querySub.functionAddition = query.getFunctionAddition();
                task.querySubs.add(querySub);
                task.rootWindows = new LinkedList<RootWindow>();
                rootTasks.add(task);

                //countbasedflag and initialized count task
                if(query.getWindowType() == conf.COUNTBASED){
                    RootWindow rootWindow = new RootWindow();
                    rootWindow.window = new Window();
                    rootWindow.setWindowId(1);
                    task.rootWindows.add(rootWindow);
                    task.tupleLists = new ArrayList<>();
                    countBasedFlag = true;
                }
                //nondecomposableFlag for median and quantile
                if(query.getWindowType() != conf.COUNTBASED && query.getScenario() == conf.CentralizedAggregation){
                    timeBasedNonDecomposableFlag = true;
                }

            }else{
                try {
                    Thread.sleep(conf.queryWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
//        if(!queryQueue.isEmpty()){
//            RootTask task = new RootTask();
//            task.query = (Query) queryQueue.poll();
//            task.setTaskId(task.query.getQueryId());
//            task.setWindowCounter(1);
//            task.rootWindows = new LinkedList<RootWindow>();
//            rootTasks.add(task);
//            //for centralized aggregation, initialize median and quantile
//            if(task.query.getScenario() == conf.CentralizedAggregation) {
//                RootWindow rootWindow = new RootWindow();
//                rootWindow.window = new Window();
//                rootWindow.setWindowId(1);
//                task.rootWindows.add(rootWindow);
//            }
//        }
    }


}
