package De.Hpi.Scotty.Configure;

import De.Hpi.Desis.Configure.ConfigurationMessage;
import De.Hpi.Desis.Configure.ConfigurationProcessing;
import De.Hpi.Desis.Configure.ConfigurationWindow;

public class Configuration implements ConfigurationTopology, ConfigurationWindow,
        ConfigurationProcessing, ConfigurationGenerator, ConfigurationMessage
        , ConfigurationBenchmark {

    //how many query we would simulate
    public int queryNumber = 1;
    public int keyNumber = 10;
    //to make program easy
    // in optimizer system start to process only when "queryNumber" queries in system,
    // and querywait is to block loops
    public static final int queryWait = 10;
    //the batch size of centralized aggregation
    public static final int centralizedBatchSize = 1000;
    //expired time
    public static final int EXPIREDTIME = 1000; //watermark of intermediate window and root window
    //debug mode, output much more information, mainly print message between nodes
    public static final boolean DEBUGMODE = true;
    //windows & Linuxs
    public static final boolean WINDOWS = false;
    //how many threads for generator
    public static final int GeneratorThreadNumber = 2;

    //node id
    private int nodeId;

    public int getNodeId() {
        return nodeId;
    }
    public void setNodeId(int nodeNumber) {
        this.nodeId = nodeNumber;
    }
}
