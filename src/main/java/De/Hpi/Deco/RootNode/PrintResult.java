package De.Hpi.Deco.RootNode;

import De.Hpi.Deco.Configure.Configuration;
import De.Hpi.Deco.Dao.WindowCollection;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PrintResult implements Runnable {

    private ConcurrentLinkedQueue<WindowCollection> resultQueue;
    private Configuration conf;

    public PrintResult(ConcurrentLinkedQueue<WindowCollection> resultQueue, Configuration conf){
        this.conf = conf;
        this.resultQueue = resultQueue;
    }

    public void run() {
        long begintime = System.currentTimeMillis();
        long endtime = System.currentTimeMillis();
        int latencyCounter = 0;
        int latencyAll = 0;
        while(!Thread.currentThread().isInterrupted()){
            if(!resultQueue.isEmpty()){
                WindowCollection windowCollection = (WindowCollection) resultQueue.poll();
                latencyAll += windowCollection.nodeId;
                latencyCounter++;
                if(conf.DEBUGMODE_ROOT) {
                    if (System.currentTimeMillis() - endtime > conf.BenchMarkDebugFrequency) {
                        endtime = System.currentTimeMillis();
                        System.out.println("rootNode--FinalAggregation"
                                + "  Latency:  " + latencyAll / latencyCounter
                        );
                        windowCollection.windowList.forEach(window -> {
                            System.out.println("rootNode--FinalAggregation"
                                            + "  QueryId:  " + window.queryId
                                            + "  WindowId: " + window.windowId
//                            + "  function  " + window.getFunction()
//                            + "  windowType  " + window.getWindowType()
                                            + "  result:  " + window.result
                                            + "  count:  " + window.count
//                                    + "  listSize:  " + window.tuples.size()
//                                    + "  NetworkOverhead:  " + networkOverhead
//                                    + "  Throughput:  " + window.tupleCounter / ((endtime - begintime) / 1000.0)
                            );

                        });
                    }
                }
          }
        }
    }

//    private static long getNetworkOverhead() {
//        return (44 + 44 + 44)  * 2;
//    }

}
