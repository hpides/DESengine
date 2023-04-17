package De.Hpi.DesisAll.DesisQuantile.RootNode;

import De.Hpi.DesisAll.DesisQuantile.Configure.Configuration;
import De.Hpi.DesisAll.DesisQuantile.Dao.WindowCollection;

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
        while(!Thread.currentThread().isInterrupted()){
            if(!resultQueue.isEmpty()){
                WindowCollection windowCollection = (WindowCollection) resultQueue.poll();
                if(conf.DEBUGMODE_ROOT) {
                    if (System.currentTimeMillis() - endtime > conf.BenchMarkDebugFrequency) {
                        endtime = System.currentTimeMillis();
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
