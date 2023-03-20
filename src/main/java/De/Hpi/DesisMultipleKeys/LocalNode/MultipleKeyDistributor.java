package De.Hpi.DesisMultipleKeys.LocalNode;

import De.Hpi.DesisMultipleKeys.Configure.Configuration;
import De.Hpi.DesisMultipleKeys.Dao.*;
import org.zeromq.ZContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultipleKeyDistributor implements Runnable{


    private Configuration conf;
    private ConcurrentLinkedQueue<WindowCollection> intermediateResultQueue;
    private ConcurrentLinkedQueue<Query> queryQueue;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;
    private HashMap<Integer, Optimizer> optimizerHashMap;

    public MultipleKeyDistributor(Configuration conf, ConcurrentLinkedQueue<WindowCollection> intermediateResultQueue,
                     ConcurrentLinkedQueue<Query> queryQueue , ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue) {

        this.conf = conf;
        this.intermediateResultQueue = intermediateResultQueue;
        this.queryQueue = queryQueue;
        this.intermediateResultQueue = intermediateResultQueue;
        this.dataQueue = dataQueue;
        this.optimizerHashMap = new HashMap<>();

    }

    public void run() {
        //to achieve the highest performance
//        readIntoMemory();

        for(int i = 0; i < conf.keyNumber; i++){
            optimizerHashMap.put(i, new Optimizer(conf, intermediateResultQueue, new ConcurrentLinkedQueue<Query>()));
        }

        int queryNumber = conf.queryNumber;
        while(0 < queryNumber){
            if(!queryQueue.isEmpty()){
                Query query = (Query) queryQueue.poll();
                optimizerHashMap.forEach((integer, optimizer) -> optimizer.queryQueue.offer(query));
                queryNumber--;
            }else{
                try {
                    Thread.sleep(conf.queryWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //to read all queries
        optimizerHashMap.forEach((integer, optimizer) -> optimizer.queryPreProcess());
        optimizerHashMap.forEach((integer, optimizer) -> optimizer.previousTimeCounter = System.currentTimeMillis());

        System.out.println(conf.keyNumber);

//        optimizerHashMap.forEach(optimizer -> {
//            System.out.println("Optimizer:   " + optimizer.optimizerId);
//            ArrayList<De.Hpi.DesisIC.Dao.LocalTask> localTasks = optimizer.localTasks;
//            System.out.println("LocalTask: " + localTasks.size());
//            System.out.println("QuerySub: ");
//            localTasks.forEach(localTask -> System.out.print(localTask.querySubs.size() + " "));
////            System.out.print(localTasks.get(0).query.getEntireQuery());
//            System.out.println();
//        });

//        Optimizer temp = new Optimizer(conf, intermediateResultQueue, queryQueue);
//        temp.queryPreProcess();
//        temp.previousTimeCounter = System.currentTimeMillis();
        while(true) {
            if (!dataQueue.isEmpty()){
                ArrayList<Tuple> dataBuffer = dataQueue.poll();
                dataBuffer.forEach(tuple -> {
                    //process tuples
                    optimizerHashMap.get(tuple.KEY).worker(tuple);
                });
            }
        }


    }


}
