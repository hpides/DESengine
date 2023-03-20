package De.Hpi.DesisMultipleKeys.Generator;

import De.Hpi.DesisMultipleKeys.Configure.Configuration;
import De.Hpi.DesisMultipleKeys.Dao.Tuple;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class DataGenerator implements Runnable {

    private Configuration conf;
    private ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue;
    private AtomicLong tupleConter;
//    private OpenCSVReader openCSVReader;
    private UniVocityCSVReader uniVocityCSVReader;
    public DataGenerator(Configuration conf, ConcurrentLinkedQueue<ArrayList<Tuple>> dataQueue, AtomicLong tupleConter) {
        this.conf = conf;
        this.dataQueue = dataQueue;
//        this.openCSVReader = new OpenCSVReader(conf);
        this.uniVocityCSVReader = new UniVocityCSVReader(conf);
        this.tupleConter = tupleConter;
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
        ArrayList<Tuple> dataBuffer = new ArrayList<>(conf.MAXBUFFERSIZE);
        while (!Thread.currentThread().isInterrupted()) {
            if(dataQueue.size() < conf.MAXBUFFERSIZE) {
                try {
                    if(dataQueue.size() < conf.DATAGENERATORMAXIMIUMBUFFER) {
                        uniVocityCSVReader.getDuplicateDataTuple(dataBuffer, conf.DUPLICATETUPLE);
                        if (dataBuffer.size() >= conf.MAXBUFFERSIZE) {
                            dataQueue.offer(dataBuffer);
                            tupleConter.addAndGet(dataBuffer.size());
                            dataBuffer = new ArrayList<Tuple>(conf.MAXBUFFERSIZE);
                        }
                    }else {
                            System.out.println("WARNING!!!!:  " + dataQueue.size());
                            Thread.sleep(conf.DATAGENERATORFREQUENCY);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
