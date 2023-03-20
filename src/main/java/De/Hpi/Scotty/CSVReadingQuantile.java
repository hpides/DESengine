package De.Hpi.Scotty;

import De.Hpi.Scotty.Configure.Configuration;
import De.Hpi.Scotty.Generator.InputStream;
import De.Hpi.Scotty.source.slicing.SlicingWindowOperator;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class CSVReadingQuantile {

    public static void main(String[] args) {

        AtomicLong tupleCounter = new AtomicLong(0l);
        Configuration conf = new Configuration();
        ConcurrentLinkedQueue<ArrayList<DesisTuple>> dataQueue = new ConcurrentLinkedQueue<ArrayList<DesisTuple>>();
        ArrayList<Thread> threadsList = new ArrayList<Thread>();
        threadsList.add(new Thread(new InputStream(conf, dataQueue, conf.GeneratorThreadNumber)));
        threadsList.forEach( thread -> thread.start());

//        if(Integer.valueOf(args[0]) != 0){
//            conf.queryNumber = Integer.valueOf(args[0]);
//        }


        ScottyQuantile scotty = new ScottyQuantile(conf);

        //The input is a tuple
        if (!scotty.slicingWindowOperatorMap.containsKey("CSVT")) {
            scotty.slicingWindowOperatorMap.put("CSVT", scotty.initWindowOperator());
        }
        SlicingWindowOperator<Integer> slicingWindowOperator = scotty.slicingWindowOperatorMap.get("CSVT");

        long begintime = System.currentTimeMillis();
        while (true) {
            if (!dataQueue.isEmpty()) {
                ArrayList<DesisTuple> bufferTemp = dataQueue.poll();
                bufferTemp.forEach(tuple -> {
                    tupleCounter.getAndIncrement();
                    slicingWindowOperator.processElement((int) tuple.DATA, System.currentTimeMillis());
//                    System.out.println(System.c//                    System.out.println(tupleCounter.get());urrentTimeMillis()-begintime);
                    scotty.processWatermark("CSVT", System.currentTimeMillis()
                            , tupleCounter.get());
                });
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




