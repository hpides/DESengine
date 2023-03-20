package De.Hpi.DesisCen.Generator;

import De.Hpi.DesisCen.Configure.Configuration;
import De.Hpi.DesisCen.Dao.Tuple;
import De.Hpi.DesisCen.Generator.DataGeneratorSimu;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class InputStream implements Runnable {

    private Configuration conf;
    private int threadNumber;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;

    public InputStream(Configuration conf, ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue, int threadNumber){
        this.conf = conf;
        this.dataQueue = dataQueue;
        this.threadNumber = threadNumber;
    }

    @Override
    public void run() {
        AtomicLong tupleCounter = new AtomicLong();
        //start generators
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for(int i = 1; i <= this.threadNumber; i++){
            conf.setNodeId(i);
            threads.add(new Thread(new DataGeneratorSimu(conf, dataQueue, tupleCounter)));
        }
        threads.forEach(thread -> thread.start());

        //wait for system being stable
        long begintime = System.currentTimeMillis();
        while(true){
            if(System.currentTimeMillis() - begintime > conf.BenchMarkDelay)
                break;
        }
        begintime = System.currentTimeMillis();
        long endtime = System.currentTimeMillis();
        tupleCounter.set(0);

        while(true){
            endtime = System.currentTimeMillis();
            if (tupleCounter.get() / ((endtime - begintime) / 1000.0) > conf.SendingSpeed) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            if (System.currentTimeMillis() - endtime > conf.BenchMarkOutputFrequency) {
//                endtime = System.currentTimeMillis();
//                System.out.println("INFO--"
//                        + "Throughput:  " + tupleCounter.get() / ((endtime - begintime) / 1000.0)
//                        + "  NetworkOverhead:  " + 0
//                        + "  Allcounter:  " + tupleCounter.get()
//                        + "  Time:  " + (endtime - begintime) / 1000.0
////                        + "  GCTime:  " + getGarbageCollectionTime()
////                        + "  GC/Time-Ratio:  " + (double) getGarbageCollectionTime() / (endtime - begintime));
//
//            }
        }
    }
    private static long getGarbageCollectionTime() {
        long collectionTime = 0;
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            collectionTime += garbageCollectorMXBean.getCollectionTime();
        }
        return collectionTime;
    }
}
