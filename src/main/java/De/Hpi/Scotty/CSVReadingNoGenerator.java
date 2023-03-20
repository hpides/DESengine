package De.Hpi.Scotty;

import De.Hpi.Scotty.Configure.Configuration;
import De.Hpi.Scotty.source.slicing.SlicingWindowOperator;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CSVReadingNoGenerator {

    public static void main(String[] args) {

        long tupleCounter = 0;
        Configuration conf = new Configuration();
        ConcurrentLinkedQueue<ArrayList<DesisTuple>> dataQueue = new ConcurrentLinkedQueue<ArrayList<DesisTuple>>();

//        ArrayList<Thread> threadsList = new ArrayList<Thread>();
//        threadsList.add(new Thread(new InputStream(conf, dataQueue, conf.GeneratorThreadNumber)));
//        threadsList.forEach( thread -> thread.start());

        Random random = new Random();

        Scotty scotty = new Scotty(conf);

        //The input is a tuple
        if (!scotty.slicingWindowOperatorMap.containsKey("CSVT")) {
            scotty.slicingWindowOperatorMap.put("CSVT", scotty.initWindowOperator());
        }
        SlicingWindowOperator<Double> slicingWindowOperator = scotty.slicingWindowOperatorMap.get("CSVT");

        long begintime = System.currentTimeMillis();
        while (true) {
            DesisTuple tuple = new DesisTuple();
            tuple.TIME = (int) System.currentTimeMillis();
            tuple.DATA = (double)System.currentTimeMillis();
//          tuple.EVENT = Integer.valueOf(line[3]);
            tuple.EVENT = eventSimulator(conf, random);
            tupleCounter++;



                    slicingWindowOperator.processElement(tuple.DATA, System.currentTimeMillis());
//                    System.out.println(System.c//                    System.out.println(tupleCounter.get());urrentTimeMillis()-begintime);
                    scotty.processWatermark("CSVT", System.currentTimeMillis()
                            , tupleCounter);

//                counterTest-= bufferTemp.size();
                //We only process the Value of a tuple

        }

//        threads.forEach(thread -> thread.stop());
//        long endtime = System.nanoTime();
//        double costtime = (double) (endtime - begintime) / 10;
//        System.out.println((long)(1000 * tupleCounter / costtime));
//        System.out.println(costtime);
//        System.out.println(counterTest);
//        System.out.println(dataQueue.size());

    }
    public static int eventSimulator(Configuration conf, Random random){
        return random.nextInt(conf.EVENTRANDOMSEED) == 1 ? 1 : 0;
    }
}




