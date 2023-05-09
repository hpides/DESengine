package De.Hpi.DecoAll.Debuffer.Generator;


import De.Hpi.DecoAll.Debuffer.Configure.Configuration;
import De.Hpi.DecoAll.Debuffer.Dao.Tuple;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class DataGeneratorRealSetup implements Runnable{

    private Configuration conf;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;
    private AtomicLong tupleCounter;
    private AtomicLong eventRatesCurrent;
    private Random random;
    private long eventRates;
    private int eventRateRangeMax;
    private int eventRateRangeMin;
    private int eventRateCorrection;
    private long timeTemp;
    private long eventCounter;
    private long startTimeEverySecond;

    //for debug
//    long eventCounter;
//    long initialTime;
//    long counterTemp;

    public DataGeneratorRealSetup(Configuration conf, ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue, AtomicLong tupleConter, AtomicLong eventRatesCurrent) {
        this.conf = conf;
        this.dataQueue = dataQueue;
        this.tupleCounter = tupleConter;
        this.eventRatesCurrent = eventRatesCurrent;
        this.random = new Random();
        this.eventRates = conf.eventGenerateRate;
        this.eventRateRangeMax = conf.eventRatesChangingRange*100;
        this.eventRateRangeMin = conf.eventRatesChangingRange*-100;
        this.eventRateCorrection = 10000;
        this.timeTemp = System.currentTimeMillis();
        this.startTimeEverySecond = 0;

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
            startTimeEverySecond = System.currentTimeMillis();
            try {
//                if(dataQueue.size() < conf.DATAGENERATORMAXIMIUMBUFFER) {
                //we assume our generator is fast enough
                if(eventCounter >= eventRates) {
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
                    eventCounter += dataBuffer.size();
                    dataBuffer = new ArrayList<Tuple>(conf.MAXBUFFERSIZE);
                }else {
                    //too fast, and make no sense
//                    System.out.println("WARNING!!!!:  " + dataQueue.size());
                    //how much time spent for sending 'eventRates' events
                    long tempTime = System.currentTimeMillis() - startTimeEverySecond;
                    if(tempTime > 1000)
                        System.out.println("Warning!!! The generator is too slow!!!");
                    eventCounter = 0;
                    Thread.sleep(1000 - tempTime);

                    //change event rates
                    eventRates *= 1 + (random.nextInt(eventRateRangeMax -eventRateRangeMin)
                            + eventRateRangeMin) / eventRateCorrection;
                    eventRatesCurrent.set(eventRates);
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

