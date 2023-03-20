package De.Hpi.DesisSW.Configure;

public interface ConfigurationTopology {
    //node
    public static final int rootNumber = 1;
    public static final int intermediaNumber = 1;
    public static final int localNumber = 1;

    //root node
    public static final String rootPubAddrW = "tcp://localhost:41010";
    public static final String rootPubAddr = "tcp://node-07:41010";

    //intermediate node
    public static final String interUpperPubAddr1W = "tcp://localhost:51010";
    public static final String interLowerPubAddr1W = "tcp://localhost:51020";
    public static final String interUpperPubAddr1 = "tcp://node-02:51010";
    public static final String interLowerPubAddr1 = "tcp://node-02:51020";

    public static final String interUpperPubAddr2W = "tcp://localhost:51011";
    public static final String interLowerPubAddr2W = "tcp://localhost:51021";
    public static final String interUpperPubAddr2 = "tcp://node-08:52010";
    public static final String interLowerPubAddr2 = "tcp://node-08:52020";


    //local node
    public static final String localPubAddr1W = "tcp://localhost:61010";
    public static final String localPubAddr1 = "tcp://node-04:61010";

    public static final String localPubAddr2W = "tcp://localhost:62010";
    public static final String localPubAddr2 = "tcp://node-05:62010";

    public static final String localPubAddr3W = "tcp://localhost:61030";
    public static final String localPubAddr3 = "tcp://node-06:63010";

    public static final String localPubAddr4W = "tcp://localhost:61040";
    public static final String localPubAddr4 = "tcp://node-09:64010";

}
