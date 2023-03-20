package De.Hpi.Scotty.Generator;

import De.Hpi.Scotty.Configure.Configuration;
import De.Hpi.Scotty.DesisTuple;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class DataGeneratorSimu implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<ArrayList<DesisTuple>> dataQueue;
    private AtomicLong tupleCounter;
    private Random random;

    public DataGeneratorSimu(Configuration conf, ConcurrentLinkedQueue<ArrayList<DesisTuple>> dataQueue, AtomicLong tupleConter) {
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
            ArrayList<DesisTuple> dataBuffer = new ArrayList<>(conf.MAXBUFFERSIZE);
            try {
                if(dataQueue.size() < conf.DATAGENERATORMAXIMIUMBUFFER) {
                    for(int i = 0; i<conf.MAXBUFFERSIZE; i++){
                        DesisTuple tuple = new DesisTuple();
                        tuple.TIME = (int) System.currentTimeMillis();
                        tuple.DATA = (double)System.currentTimeMillis() + random.nextInt(conf.EVENTRANDOMSEED);
//                tuple.EVENT = Integer.valueOf(line[3]);
                        tuple.EVENT = eventSimulator();
                        dataBuffer.add(tuple);
                    }
                        dataQueue.offer(dataBuffer);
                        tupleCounter.addAndGet(dataBuffer.size());
                        dataBuffer = new ArrayList<DesisTuple>(conf.MAXBUFFERSIZE);
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
