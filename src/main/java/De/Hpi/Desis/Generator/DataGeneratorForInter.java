package De.Hpi.Desis.Generator;

import De.Hpi.Desis.Dao.Window;
import De.Hpi.Desis.Dao.WindowCollection;
import De.Hpi.Desis.Configure.Configuration;
import De.Hpi.Desis.Dao.Tuple;
import De.Hpi.Desis.Generator.UniVocityCSVReader;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class DataGeneratorForInter implements Runnable {

    private Configuration conf;
    private ConcurrentLinkedQueue<WindowCollection> resultQueueFromLocal;
    private Random random;
    private int nodeNumber;
    private int queryNumber;
    private int counterSlice;
    private int counter;
    //    private OpenCSVReader openCSVReader;
    private UniVocityCSVReader uniVocityCSVReader;
    public DataGeneratorForInter(Configuration conf, ConcurrentLinkedQueue<WindowCollection> resultQueueFromLocal) {
        this.conf = conf;
        this.resultQueueFromLocal = resultQueueFromLocal;
//        this.openCSVReader = new OpenCSVReader(conf);

        this.nodeNumber = conf.localNumber / conf.intermediaNumber;

        if(conf.queryNumber < 10)
            this.queryNumber = conf.queryNumber;
        else
            this.queryNumber = 10;

        this.random = new Random();
        this.counter = 0;
        this.counterSlice = 1;
    }

    public void run() {
//        System.out.println("Starting FromRNodeToINode ----rootNode");
        readDuplicateFromDiskOpenUniVocityCSVByBuffer();
        //to achieve the highest performance
//        readIntoMemory();
    }

    void readIntoMemory(){

    }

    void readDuplicateFromDiskOpenUniVocityCSVByBuffer(){
        long begintime = System.currentTimeMillis();
        long endtime = System.currentTimeMillis();
        int counterTemp = 0;
        while (!Thread.currentThread().isInterrupted()) {
            if(resultQueueFromLocal.size() < conf.MAXBUFFERSIZE) {
                try {
                    for(int i = 1; i <= nodeNumber; i++ ) {
                        if (resultQueueFromLocal.size() < conf.DATAGENERATORMAXIMIUMBUFFER) {
                            WindowCollection windowCollection = new WindowCollection();
                            windowCollection.sliceId = counterSlice;
                            windowCollection.sliceCounter = counterSlice;
                            windowCollection.windowList = new ArrayList<>();
                            Window window = new Window();
                            window.result = (double) System.currentTimeMillis() + random.nextInt(conf.EVENTRANDOMSEED);
                            window.count = (long) System.currentTimeMillis() + random.nextInt(conf.EVENTRANDOMSEED);
                            window.windowId = counterSlice;
                            window.queryId = 0;
                            windowCollection.windowList.add(window);
                            windowCollection.tuples = new ArrayList<>();

                            //this segment is for non-decomposable
//                            for(int tn = 0; tn < conf.localBatchSize; tn++) {
//                                Tuple tuple = new Tuple();
//                                tuple.DATA = (double) System.currentTimeMillis() + random.nextInt(conf.EVENTRANDOMSEED);
//                                tuple.TIME = (int) System.currentTimeMillis() + random.nextInt(conf.EVENTRANDOMSEED);
//                                tuple.EVENT = random.nextInt(conf.EVENTRANDOMSEED);
//                                windowCollection.tuples.add(tuple);
//                            }

                            counter++;
                            windowCollection.nodeId = i;
                            resultQueueFromLocal.add(windowCollection);
                        } else {
//                            System.out.println("WARNING!!!!:  " + resultQueueFromLocal.size());
                            Thread.sleep(conf.DATAGENERATORFREQUENCY);
                        }
                    }
//                    counterTemp++;
                    counterSlice++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (System.currentTimeMillis() - endtime > conf.BenchMarkOutputFrequency) {
                endtime = System.currentTimeMillis();
                System.out.println("InterThroughput--" + conf.getNodeId() + "--INFO"
                        + "  Throughput:  " + counter / ((endtime - begintime) / 1000.0)
                        + "  counter:  " + counter
                        + "  Time:  " + (endtime - begintime) / 1000.0
                        + "  Queue:  " + resultQueueFromLocal.size()
                        + "  localNodes:  " + conf.localNumber
                        + "  intermediaNodes:  " + conf.intermediaNumber
                        + "  nodeNumber:  " + this.nodeNumber
                );
            }
        }
    }
}
