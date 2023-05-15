package De.Hpi.DecoAll.Debuffer.Generator;

import De.Hpi.DecoAll.Debuffer.Configure.Configuration;
import De.Hpi.DecoAll.Debuffer.Dao.Tuple;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import static De.Hpi.DecoAll.Debuffer.Configure.Configuration.*;

/*
We let 1000 tuples be a data chunk and window operator take one chunk everytime.
If all the event rates change the local window sizes will stay the same.
So we only change one data source.
 */
public class InputStream implements Runnable {

    private Configuration conf;
    private int threadNumber;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;
    private long counter;
    private Random random;

    public InputStream(Configuration conf, ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue, int threadNumber){
        this.conf = conf;
        this.dataQueue = dataQueue;
        this.threadNumber = threadNumber;
        this.counter = counter;
        this.random = new Random();
    }

    @Override
    public void run() {
        AtomicLong tupleCounter = new AtomicLong();
        AtomicLong eventRatesCurrent = new AtomicLong();
        //start generators
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for(int i = 1; i <= this.threadNumber; i++){
//            conf.setNodeId(i);
            //generate data from a synthetic dataset
            threads.add(new Thread(new DataGeneratorSimu(conf, dataQueue, tupleCounter)));
//            threads.add(new Thread(new DataGeneratorRealSetup(conf, dataQueue, tupleCounter, eventRatesCurrent)));
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
            if(DEBUGMODE_GENERATOR) {
                if (System.currentTimeMillis() - endtime >= conf.BenchMarkOutputFrequency) {
                    endtime = System.currentTimeMillis();
                    System.out.println("LocalNode--" + conf.getNodeId() + "--INFO"
                            + "  Throughput:  " + tupleCounter.get() / ((endtime - begintime) / 1000.0)
//                        + "  NetworkOverhead:  " + 0
                            + "  Allcounter:  " + tupleCounter.get()
                            + "  EventRate:  " + eventRatesCurrent.get()
                            + "  counter:  " + (tupleCounter.get() - counter)
                            + "  Time:  " + (endtime - begintime) / 1000.0
                            + "  GCTime:  " + getGarbageCollectionTime()
                            + "  GC/Time-Ratio:  " + (double) getGarbageCollectionTime() / (endtime - begintime)
                            + "  Queue:  " + dataQueue.size()
                    );
                    counter = tupleCounter.get();
                }
            }
        }

    }
    private static long getGarbageCollectionTime() {
        long collectionTime = 0;
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            collectionTime += garbageCollectorMXBean.getCollectionTime();
        }
        return collectionTime;
    }

    public double getEventRates() {
        return 1 + (random.nextInt(conf.eventRatesChangingRange*100 - conf.eventRatesChangingRange*-100)
                + conf.eventRatesChangingRange*-100) / 10000.0;
    }
}
