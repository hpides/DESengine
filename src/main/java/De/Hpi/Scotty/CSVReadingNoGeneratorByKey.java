package De.Hpi.Scotty;

import De.Hpi.Desis.Dao.Tuple;
import De.Hpi.Scotty.Configure.Configuration;
import De.Hpi.Scotty.source.slicing.SlicingWindowOperator;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CSVReadingNoGeneratorByKey {

    public static void main(String[] args) {

        long tupleCounter = 0;
        Configuration conf = new Configuration();
        ConcurrentLinkedQueue<ArrayList<DesisTuple>> dataQueue = new ConcurrentLinkedQueue<ArrayList<DesisTuple>>();

//        ArrayList<Thread> threadsList = new ArrayList<Thread>();
//        threadsList.add(new Thread(new InputStream(conf, dataQueue, conf.GeneratorThreadNumber)));
//        threadsList.forEach( thread -> thread.start());

        if(Integer.valueOf(args[0]) != 0){
            conf.queryNumber = Integer.valueOf(args[0]);
        }
        if(Integer.valueOf(args[1]) != 0){
            conf.keyNumber = Integer.valueOf(args[1]);
        }

        Random random = new Random();
        Random randomKey = new Random();

        Scotty scotty = new Scotty(conf);

        //The input is a tuple
        for(int i = 0; i< conf.keyNumber; i++) {
//            if (!scotty.slicingWindowOperatorMap.containsKey(i+"")) {
            scotty.slicingWindowOperatorMap.put(i+"", scotty.initWindowOperator());
//            }
        }
        System.out.println("keynumber" + conf.keyNumber);
//        SlicingWindowOperator<Double> slicingWindowOperator = scotty.slicingWindowOperatorMap.get("CSVT");

        long begintime = System.currentTimeMillis();
        while (true) {
            DesisTuple tuple = new DesisTuple();
            tuple.TIME = (int) System.currentTimeMillis();
            tuple.DATA = (double)System.currentTimeMillis();
            tuple.KEY = random.nextInt(conf.keyNumber);
            tuple.EVENT = eventSimulator(conf, random);
            tupleCounter++;



            scotty.slicingWindowOperatorMap.get(tuple.KEY+"").processElement(tuple.DATA, System.currentTimeMillis());
//                    System.out.println(System.c//                    System.out.println(tupleCounter.get());urrentTimeMillis()-begintime);
            scotty.processWatermark(tuple.KEY+"", System.currentTimeMillis()
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
