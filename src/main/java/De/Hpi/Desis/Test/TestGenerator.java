package De.Hpi.Desis.Test;

import De.Hpi.Desis.Configure.Configuration;
import De.Hpi.Desis.Dao.Query;
import De.Hpi.Desis.Dao.Tuple;
import De.Hpi.Desis.Dao.RootTask;
import De.Hpi.Desis.Generator.DataGenerator;
import De.Hpi.Desis.Generator.OpenCSVReader;
import De.Hpi.Desis.Generator.QueryGenerator;
import De.Hpi.Desis.Generator.UniVocityCSVReader;
import De.Hpi.Desis.Generator.InputStream;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TestGenerator {

//    private static int counterTest = 100000000;
    private static long counterTest = 1000000;
    private static int threadNumber = 1;

    public static void main(String[] args) throws InterruptedException {

        if(args.length > 1){
            threadNumber = Integer.valueOf(args[0]);
            counterTest = Long.valueOf(args[1]);
        }
        inputStreamThread(threadNumber);
//        dataGeneratorFromThread(threadNumber);
//        dataGeneratorFormFile();
//        dataGeneratorFormFile2();
//        queryGenerator();
//        dataTest();
//        dataGeneratorFromNetwork();

    }

    public static void inputStreamThread(int threadNumber){
        long tupleCounter = counterTest;
        Configuration conf = new Configuration();
        ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue = new ConcurrentLinkedQueue<ArrayList<Tuple>>();
        Thread inpuutStreamThread =new Thread(new InputStream(conf, dataQueue, 2));
        inpuutStreamThread.start();
        long begintime = System.nanoTime();

        while(counterTest > 0){
            if(!dataQueue.isEmpty()) {
                ArrayList<Tuple> bufferTemp = dataQueue.poll();
//                bufferTemp.forEach(tuple -> {});
                counterTest-= bufferTemp.size();
            }
        }

//        threads.forEach(thread -> thread.stop());
        long endtime = System.nanoTime();
        double costtime = (double)(endtime - begintime) / 1000000;
        System.out.println((long)(1000 * tupleCounter / costtime));
        System.out.println(costtime);
        System.out.println(counterTest);
        System.out.println(dataQueue.size());

    }

    public static void dataTest(){
        Configuration conf = new Configuration();
        //get data directly
        OpenCSVReader openCSVReader = new OpenCSVReader(conf);

        LinkedList<Tuple> tupleLinkedList = new LinkedList<>();
        double result1 = 0;
        double result11 = 0;
        counterTest = 1000;
        while(counterTest > 0){
            Tuple tuple = openCSVReader.getDataTuple();
            tupleLinkedList.add(tuple);
//            System.out.println(rawData.DATA + " " + rawData.TIME + " " + rawData.EVENT);
            result1+=tuple.DATA;
            result11 = result11 > tuple.DATA ? result11 : tuple.DATA;
            counterTest--;
        }
        double result2 = tupleLinkedList.stream().map(e -> e.DATA).reduce((double) 0, Double::sum);
        double result22 = tupleLinkedList.stream().map(e -> e.DATA)
                .mapToDouble(Double::doubleValue).max().getAsDouble();

        System.out.println(result1);
        System.out.println(result11);
        System.out.println(result2);
        System.out.println(result22);

    }

    public static void dataGeneratorFormFile(){
        Configuration conf = new Configuration();
        //get data directly
        UniVocityCSVReader uniVocityCSVReader = new UniVocityCSVReader(conf);
        ConcurrentLinkedQueue<Tuple> dataQueue = new ConcurrentLinkedQueue<>();
        long begintime = System.nanoTime();
        while(counterTest > 0 ){
            Tuple tuple = uniVocityCSVReader.getDataTuple();
//            dataQueue.add(tuple);
//            dataQueue.poll();
//            System.out.println(rawData.DATA + " " + rawData.TIME + " " + rawData.EVENT);
            counterTest--;
        }
        uniVocityCSVReader.end();
        long endtime = System.nanoTime();
        long costtime = (endtime - begintime) / 1000;
        System.out.println(costtime);
        System.out.println(dataQueue.size());
    }

    public static void dataGeneratorFormFile2(){
        Configuration conf = new Configuration();
        //get data directly
        UniVocityCSVReader uniVocityCSVReader = new UniVocityCSVReader(conf);
        ArrayList<Tuple> buffer = new ArrayList<>();
        ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue = new ConcurrentLinkedQueue<>();
        long begintime = System.nanoTime();
        for(int i = 0; i < 10; i++) {
            long counter = counterTest;
            while (counter > 0) {
                Tuple tuple = uniVocityCSVReader.getDataTuple();
                buffer.add(tuple);
                counter--;
            }
            System.out.println("plus 1");
        }
        dataQueue.add(buffer);
        uniVocityCSVReader.end();
        long endtime = System.nanoTime();
        long costtime = (endtime - begintime) / 1000;
        System.out.println(costtime);
        System.out.println(dataQueue.size());

        begintime = System.nanoTime();
        while(!dataQueue.isEmpty()) {
            dataQueue.poll().forEach(tuple -> tuple.toString());
            System.out.println("minus 1");
        }
        endtime = System.nanoTime();
        costtime = (endtime - begintime) / 1000;
        System.out.println(costtime);
    }

    public static void dataGeneratorFromThread(int threadNumber) throws InterruptedException {
        long tupleCounter = counterTest;
        Configuration conf = new Configuration();
        ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue = new ConcurrentLinkedQueue<ArrayList<Tuple>>();
        ArrayList<Thread> threads = new ArrayList<Thread>();
        System.out.println("Thread Number: " + threadNumber +
                "Tuple Number: " + counterTest);
        for(int i = 1; i <= TestGenerator.threadNumber; i++){
            conf.setNodeId(i);
            threads.add(new Thread(new DataGenerator(conf, dataQueue, new AtomicLong())));
        }
        threads.forEach(thread -> thread.start());;

        long begintime = System.nanoTime();

        while(counterTest > 0){
            if(!dataQueue.isEmpty()) {
                ArrayList<Tuple> bufferTemp = dataQueue.poll();
//                bufferTemp.forEach(tuple -> {});
                counterTest-= bufferTemp.size();
            }
        }

//        threads.forEach(thread -> thread.stop());
        long endtime = System.nanoTime();
        double costtime = (double)(endtime - begintime) / 1000000;
        System.out.println((long)(1000 * tupleCounter / costtime));
        System.out.println(costtime);
        System.out.println(counterTest);
        System.out.println(dataQueue.size());

    }

    public static void queryGenerator(){
        ConcurrentLinkedQueue<Query> queryQueue = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Query> queryList = new ConcurrentLinkedQueue<>();
        Configuration conf = new Configuration();
        ConcurrentHashMap<Integer, RootTask> taskQueue = new ConcurrentHashMap<>();
        QueryGenerator queryGenerator = new QueryGenerator(queryQueue, queryList, conf);
        queryGenerator.generate();

        System.out.println("--query---" + queryQueue.size());
        queryQueue.forEach( query -> {
            System.out.println("--query---");
            System.out.println(query.getKey() + " " + query.getWindowType() + " " + query.getFunction() + " " +
                    query.getQueryId() + " " + query.getSlide() + " " + query.getRange());
        });

        System.out.println("---task--" + taskQueue.size());
        taskQueue.forEach( (id, task) -> {
            System.out.println(task.getTaskId() + " " + " " + task.query);
        });
    }
}
