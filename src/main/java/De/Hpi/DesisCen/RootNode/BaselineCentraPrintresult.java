package De.Hpi.DesisCen.RootNode;

import De.Hpi.DesisCen.Configure.Configuration;
import De.Hpi.DesisCen.Dao.Window;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BaselineCentraPrintresult implements Runnable {

    private ConcurrentLinkedQueue<Window> resultQueue;
    private Configuration conf;

    public BaselineCentraPrintresult(ConcurrentLinkedQueue<Window> resultQueue, Configuration conf){
        this.conf = conf;
        this.resultQueue = resultQueue;
    }

    public void run() {
        long begintime = System.currentTimeMillis();
        long endtime = System.currentTimeMillis();
        while(!Thread.currentThread().isInterrupted()){
            if(!resultQueue.isEmpty()){
                Window window = (Window) resultQueue.poll();
                if (System.currentTimeMillis() - endtime > conf.BenchMarkDebugFrequency) {
                    endtime = System.currentTimeMillis();
                    System.out.println("rootNode----finalAggregation----"
                                    + window.getWindowId()
                                    + "  QueryId:  " + window.getQueryId()
//                            + "  function  " + window.getFunction()
//                            + "  windowType  " + window.getWindowType()
                                    + "  result:  " + window.result
                                    + "  count:  " + window.count
                                    + "  Slices:  " + window.getNodeId()
//                                    + "  listSize:  " + window.tuples.size()
//                                    + "  NetworkOverhead:  " + networkOverhead
//                                    + "  Throughput:  " + window.tupleCounter / ((endtime - begintime) / 1000.0)
                    );
                }
            }
        }
    }


}
