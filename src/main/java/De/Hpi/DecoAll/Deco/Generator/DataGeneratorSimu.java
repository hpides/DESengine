package De.Hpi.DecoAll.Deco.Generator;

import De.Hpi.DecoAll.Deco.Configure.Configuration;
import De.Hpi.DecoAll.Deco.Dao.Tuple;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class DataGeneratorSimu implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;
    private AtomicLong tupleCounter;
    private Random random;
    private long timeTemp;

    //for debug
//    long eventCounter;
//    long initialTime;
//    long counterTemp;

    public DataGeneratorSimu(Configuration conf, ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue, AtomicLong tupleConter) {
        this.conf = conf;
        this.dataQueue = dataQueue;
        this.tupleCounter = tupleConter;
        this.random = new Random();
        this.timeTemp = System.currentTimeMillis();

        //for debug
//        this.eventCounter = 0;
//        this.initialTime = System.currentTimeMillis();
//        this.counterTemp = 1;
    }

    public void run() {
//        System.out.println("Starting FromRNodeToINode ----rootNode");
        readDuplicateFromDiskOpenUniVocityCSVByBuffer();
    }


    void readDuplicateFromDiskOpenUniVocityCSVByBuffer(){
        while (!Thread.currentThread().isInterrupted()) {
            ArrayList<Tuple> dataBuffer = new ArrayList<>(conf.MAXBUFFERSIZE);
            try {
                if(dataQueue.size() < conf.DATAGENERATORMAXIMIUMBUFFER) {
                    for(int i = 0; i<conf.MAXBUFFERSIZE; i++){
                        Tuple tuple = new Tuple();
                        tuple.TIME = (int) System.currentTimeMillis();
                        tuple.DATA = (double)System.currentTimeMillis() + random.nextInt(conf.EVENTRANDOMSEED);
//                tuple.EVENT = Integer.valueOf(line[3]);
//                        tuple.EVENT = eventSimulator();
                        tuple.EVENT = -1;

                          //debug
//                        eventCounter += tuple.EVENT;
//                        if(System.currentTimeMillis() - initialTime >= 1000){
//                            initialTime = System.currentTimeMillis();
//                            System.out.println(eventCounter/counterTemp);
//                            counterTemp++;
//                        }

                        dataBuffer.add(tuple);
                    }
                        dataQueue.offer(dataBuffer);
                        tupleCounter.addAndGet(dataBuffer.size());
                        dataBuffer = new ArrayList<Tuple>(conf.MAXBUFFERSIZE);
                }else {
                    //too fast, and make no sence
//                    System.out.println("WARNING!!!!:  " + dataQueue.size());
                    Thread.sleep(conf.DATAGENERATORFREQUENCY);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            if(tupleCounter.get() > 10000000){
//                try {
//                    Thread.sleep(9999999);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    public int eventSimulator(){
        if(System.currentTimeMillis() - timeTemp > 0){
            timeTemp = System.currentTimeMillis();
            return random.nextInt(conf.EVENTRANDOMSEED) == 1 ? 1 : 0;
//            return 1;
        }else {
            return 0;
        }
    }

}
