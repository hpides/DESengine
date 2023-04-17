package De.Hpi.DesisAll.Desis.IntermediaNode;

import De.Hpi.DesisAll.Desis.Configure.Configuration;
import De.Hpi.DesisAll.Desis.Dao.*;
import org.apache.commons.collections.ArrayStack;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class IntermediateComputationEngine implements Runnable {

    private Configuration conf;
    private ConcurrentLinkedQueue<WindowCollection> resultQueueFromLocal;
    private ConcurrentLinkedQueue<WindowCollection> resultQueue;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private ArrayList<IntermediateTask> intermediateTasks;
    private ArrayList<Tuple> tupleListForCen;
    private long tupleBatchCounter;
    private boolean sortFlag;
    private boolean centralizedFlag;
    private int currentSliceId;
    private boolean endFlag;

    //for debug
//    private long latencyOverall;
//    private long latencyCounter;

     IntermediateComputationEngine(ConcurrentLinkedQueue<WindowCollection> resultQueue, ConcurrentLinkedQueue<WindowCollection> resultQueueFromLocal,
                                   ConcurrentLinkedQueue<Query> queryQueue, Configuration conf){
         this.conf = conf;
         this.resultQueueFromLocal =resultQueueFromLocal;
         this.resultQueue = resultQueue;
         this.queryQueue = queryQueue;
         this.intermediateTasks = new ArrayStack();
         this.tupleListForCen = new ArrayList<>();
         this.tupleBatchCounter = 0;
         this.sortFlag = false;
         this.centralizedFlag = false;
         this.currentSliceId = 0;
         this.endFlag = false;

         //for debug
//         this.latencyOverall = 0;
//         this.latencyCounter = 0;

     }

    public void run() {
        //to read all queries
        queryPreProcess();
        while (true) {
            if (!resultQueueFromLocal.isEmpty()) {
                //get intermediate result from local nodes
                WindowCollection windowCollection = resultQueueFromLocal.poll();
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
        // 6) disorder window, keep it

        //the windows that have same window ids should be in a same intermediate window
        //and the intermediate window is to collect same windowid windows that from different nodes.
        //windowCounter 1. >wc save 2. <wc drop 3. =wc process
        //drop < windowcounter

        //debug for latency
//        long latencyStart = System.nanoTime();

        //save & process  > & == windowcounter
        WindowCollection newWindowCollection = new WindowCollection();
        newWindowCollection.windowList = new ArrayList<>();
        newWindowCollection.tuples = new ArrayList<>();
        //send batch when median or quantile window end
//        endFlag = false;

        windowCollection.windowList.forEach(window -> {
            IntermediateTask task = intermediateTasks.get(window.queryId);
            //5) old window arrived throw, or find aim intermediate window
            if (task.getWindowCounter() <= window.windowId) {
                //if there is only child node
                if(conf.localNumber / conf.intermediaNumber - 1 == 0){
                    //process and send window
                    newWindowCollection.windowList.add(window);
                    //update task
                    task.windowCounterAdd();
                    //align windows from different nodes
//                    if(task.query.getScenario() == conf.CentralizedAggregation)
                    endFlag = true;
                    return;
                }
                //if we need to build a new window
                boolean isNewWindow = true;
                //clean the expired intermediate windows, and find the aim one
                Iterator<IntermediateWindow> iter = task.intermediateWindows.iterator();
                while (iter.hasNext()) {
                    IntermediateWindow intermediateWindow = iter.next();
                    //4)expired, process and send them all
                    if (timeTemp - intermediateWindow.getProcessTime() > conf.EXPIREDTIME) {
                        //send it
                        newWindowCollection.windowList.add(intermediateWindow.window);
                        //update task
                        iter.remove();
                        task.windowCounterAdd();
                        endFlag = true;
                        //align windows from different nodes
                    } else if (intermediateWindow.getWindowId() == window.windowId) {
                        //we find aim intermediate window
                        //this is not first window
                        intermediateWindow.deleteWindowWaitingCounter();
                        mergeWindow(task, intermediateWindow.window, window);
                        //2) less window arrived, still need to wait
                        //3) all window arrived, window end
                        if (intermediateWindow.getWindowWaitCounter() == 0) {
                            //process and send window
                            newWindowCollection.windowList.add(intermediateWindow.window);
                            //update task
                            iter.remove();
                            task.windowCounterAdd();
                            //align windows from different nodes
//                            if(task.query.getScenario() == conf.CentralizedAggregation)
                            endFlag = true;
                        }
                        isNewWindow = false;
                        break;
                    }
                }
                //1) empty, 6)disorder window, keep it, create a new intermediate window
                if (isNewWindow) {
                    IntermediateWindow intermediateWindow = new IntermediateWindow();
                    intermediateWindow.setWindowId(window.windowId);
                    intermediateWindow.setProcessTime(timeTemp);
                    intermediateWindow.setWindowWaitCounter(conf.localNumber / conf.intermediaNumber - 1);
                    intermediateWindow.window = window;
                    task.intermediateWindows.add(intermediateWindow);
                }
            }
        });

        if (currentSliceId <= windowCollection.sliceId){
            organizeWinodw(windowCollection, newWindowCollection);
        }
//    System.out.println(tupleListForCen.size());
        //send reuslt
        if(endFlag) {
            endFlag = false;
//        newWindowCollection.tuples = windowCollection.tuples;
            newWindowCollection.sliceId = windowCollection.sliceId ;
            newWindowCollection.sliceCounter = windowCollection.sliceCounter;
//            System.out.println(newWindowCollection.toString());

            //debug for latency
//            long latencyEnd = System.nanoTime();
//            latencyOverall += (long)(latencyEnd-latencyStart);
//            latencyCounter++;
//            System.out.println("inter - latency  " + (double)latencyOverall/latencyCounter);
//            newWindowCollection.nodeId = (int)(endLatency - startLatency);
            resultQueue.add(newWindowCollection);
        }
    }

    void organizeWinodw(WindowCollection windowCollection, WindowCollection newWindowCollection){
        //there is at least one centralized query
        if(centralizedFlag) {
            if (currentSliceId < windowCollection.sliceId || tupleBatchCounter >= conf.interBatchSize) {
                //sort
                if(sortFlag)
                    tupleListForCen.sort((a, b) -> Double.compare(a.DATA, b.DATA));
                newWindowCollection.tuples.addAll(tupleListForCen);
                tupleListForCen = new ArrayList<>();
                tupleBatchCounter = 0;
                endFlag = true;
                currentSliceId = windowCollection.sliceId;
            }
            tupleBatchCounter++;
            tupleListForCen.addAll(windowCollection.tuples);
        }
    }

    void mergeWindow(IntermediateTask task, Window window, Window newWindow){
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

    private void queryPreProcess(){
        //get all queries, it will be skipped when all queries retrieved
        int queryNumber = conf.queryNumber;
        while(intermediateTasks.size() < queryNumber){
            if(!queryQueue.isEmpty()){
                Query query = (Query) queryQueue.poll();
                //merge same queries
                IntermediateTask tempInterTask = intermediateTasks.stream()
                        .filter(interTask -> interTask.query.getEntireQuery().equalsIgnoreCase(query.getEntireQuery()))
                        .findFirst()
                        .orElse(null);
                if(tempInterTask != null){
                    QuerySub querySub = new QuerySub();
                    querySub.queryId = query.getQueryId();
                    querySub.functionAddition = query.getFunctionAddition();
                    tempInterTask.querySubs.add(querySub);
                    //end the loop
                    queryNumber--;
                    continue;
                }

                IntermediateTask task = new IntermediateTask();
                task.query = query;
                task.setTaskId(task.query.getQueryId());
                task.setWindowCounter(1);
                task.querySubs = new ArrayList<>();
                QuerySub querySub = new QuerySub();
                querySub.queryId = query.getQueryId();
                querySub.functionAddition = query.getFunctionAddition();
                task.querySubs.add(querySub);
                task.intermediateWindows = new LinkedList<IntermediateWindow>();
                intermediateTasks.add(task);

                //if tuples need to be sorted
                if(task.query.getFunction() == conf.MEDIAN || task.query.getFunction() == conf.QUANTILE){
                    sortFlag = true;
                }
                if(task.query.getScenario() == conf.CentralizedAggregation){
                    centralizedFlag = true;
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
//            IntermediateTask task = new IntermediateTask();
//            task.query = (Query) queryQueue.poll();
//            task.setTaskId(task.query.getQueryId());
//            task.setWindowCounter(1);
//            task.intermediateWindows = new LinkedList<IntermediateWindow>();
//            intermediateTasks.add(task);
//
//            //if tuples need to be sorted
//            if(task.query.getFunction() == conf.MEDIAN || task.query.getFunction() == conf.QUANTILE){
//                sortFlag = true;
//            }
//            if(task.query.getScenario() == conf.CentralizedAggregation){
//                centralizedFlag = true;
//            }
//        }
    }

}
