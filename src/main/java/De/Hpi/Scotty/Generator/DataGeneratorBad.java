package De.Hpi.Scotty.Generator;

import De.Hpi.Scotty.Configure.Configuration;
import De.Hpi.Scotty.DesisTuple;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class DataGeneratorBad implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<DesisTuple> dataQueue;
    private AtomicLong tupleCounter;
    private Random random;

    public DataGeneratorBad(Configuration conf, ConcurrentLinkedQueue<DesisTuple> dataQueue, AtomicLong tupleConter) {
        this.conf = conf;
        this.dataQueue = dataQueue;
        this.tupleCounter = tupleConter;
        this.random = new Random();
    }

    public void run() {
//        System.out.println("Starting FromRNodeToINode ----rootNode");
        readDuplicateFromDiskOpenUniVocityCSVByBuffer();
    }


    void readDuplicateFromDiskOpenUniVocityCSVByBuffer(){
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if(dataQueue.size() < conf.BADDATAGENERATORMAXIMIUMBUFFER) {
                    DesisTuple tuple = new DesisTuple();
                    tuple.TIME = (int) System.currentTimeMillis();
                    tuple.DATA = (double)System.currentTimeMillis();
//                tuple.EVENT = Integer.valueOf(line[3]);
                    tuple.EVENT = eventSimulator();
                    dataQueue.offer(tuple);
                    tupleCounter.addAndGet(1);
                }else {
                    //too fast, and make no sence
//                    System.out.println("WARNING!!!!:  " + dataQueue.size());
                    Thread.sleep(conf.DATAGENERATORFREQUENCY);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public int eventSimulator(){
        return random.nextInt(conf.EVENTRANDOMSEED) == 1 ? 1 : 0;
    }

}
