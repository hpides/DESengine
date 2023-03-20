package De.Hpi.DesisCen.Configure;

public class Configuration implements ConfigurationTopology, ConfigurationWindow,
        ConfigurationProcessing, ConfigurationGenerator, ConfigurationMessage
        , ConfigurationBenchmark {

    //how many query we would simulate
    public int queryNumber = 1;
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

    //windows & Linuxs & rasberrypi
    public static final boolean WINDOWS = false;
    public static final boolean RASBERRYPI = true;

    //how many threads for generator
    public int GeneratorThreadNumber = 1;
    //auto querys, from 1-3 are quantiles
    public int queryModes = 10000;

    //the sending speed of local node 1000w
    public static final int SendingSpeed = 1000 * 10000;

    //node id
    private int nodeId;

    public int getNodeId() {
        return nodeId;
    }
    public void setNodeId(int nodeNumber) {
        this.nodeId = nodeNumber;
    }
}
