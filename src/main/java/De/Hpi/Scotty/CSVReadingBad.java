package De.Hpi.Scotty;

import De.Hpi.Scotty.Configure.Configuration;
import De.Hpi.Scotty.Generator.InputStreamBad;
import De.Hpi.Scotty.source.slicing.SlicingWindowOperator;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class CSVReadingBad {

    public static void main(String[] args) {

        AtomicLong tupleCounter = new AtomicLong(0l);
        Configuration conf = new Configuration();
        ConcurrentLinkedQueue<DesisTuple> dataQueue = new ConcurrentLinkedQueue<DesisTuple>();
        ArrayList<Thread> threadsList = new ArrayList<Thread>();
        threadsList.add(new Thread(new InputStreamBad(conf, dataQueue, conf.GeneratorThreadNumber)));
        threadsList.forEach( thread -> thread.start());



        Scotty scotty = new Scotty(conf);

        //The input is a tuple
        if (!scotty.slicingWindowOperatorMap.containsKey("CSVT")) {
            scotty.slicingWindowOperatorMap.put("CSVT", scotty.initWindowOperator());
        }
        SlicingWindowOperator<Double> slicingWindowOperator = scotty.slicingWindowOperatorMap.get("CSVT");

        long begintime = System.currentTimeMillis();
        while (true) {
            if (!dataQueue.isEmpty()) {
                DesisTuple tuple = dataQueue.poll();
                tupleCounter.getAndIncrement();
                slicingWindowOperator.processElement(tuple.DATA, System.currentTimeMillis());
//                    System.out.println(System.c//                    System.out.println(tupleCounter.get());urrentTimeMillis()-begintime);
                scotty.processWatermark("CSVT", System.currentTimeMillis()
                        , tupleCounter.get());
//                counterTest-= bufferTemp.size();
                //We only process the Value of a tuple
            }
        }

//        threads.forEach(thread -> thread.stop());
//        long endtime = System.nanoTime();
//        double costtime = (double) (endtime - begintime) / 10;
//        System.out.println((long)(1000 * tupleCounter / costtime));
//        System.out.println(costtime);
//        System.out.println(counterTest);
//        System.out.println(dataQueue.size());

    }

}




