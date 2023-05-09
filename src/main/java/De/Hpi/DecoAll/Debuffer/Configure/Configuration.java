package De.Hpi.DecoAll.Debuffer.Configure;

public class Configuration implements ConfigurationTopology, ConfigurationWindow,
        ConfigurationProcessing, ConfigurationGenerator, ConfigurationMessage
        , ConfigurationBenchmark{

    //how many query we would simulate
    public int queryNumber = 1;
    //to make program easy
    // in optimizer system start to process only when "queryNumber" queries in system,
    // and querywait is to block loops
    public static final int queryWait = 100;
    //the batch size of centralized aggregation default 1000 * 1000 = 100w
    //the time granularity for optimizer to process timebased window
    public int timegranularity = 1;
    //the time granularity for optimizer to process countbased window
//    public int countgranularity = 10000;
    public int countgranularity = 10000;

    //expired time
    public static final int EXPIREDTIME = 1000; //watermark of intermediate window and root window
    //debug mode, output much more information, mainly print message between nodes
    public static final boolean DEBUGMODE_ROOT = true;
    public static final boolean DEBUGMODE_LOCAL = true;
    //windows & Linuxs
    public static final boolean WINDOWS = true;
    //auto querys, from 1-3 are quantiles
    public int queryModes = 10000;

    //event rates changing range *100%
    public int eventRatesChangingRange = 5;
    //how many events are generated
    public int eventGenerateRate = 1000000;
    //how many threads for generator
    public int GeneratorThreadNumber = 1;


    //node
    public int rootNumber = 1;
    //we do not need intermediate node in deco anymore
    public int intermediaNumber = 1;
    public int localNumber = 2;

    //node id
    private int nodeId;

    public int getNodeId() {
        return nodeId;
    }
    public void setNodeId(int nodeNumber) {
        this.nodeId = nodeNumber;
    }
}
