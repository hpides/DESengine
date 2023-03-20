package De.Hpi.Desis.Configure;

public interface ConfigurationTopology {
    //root node
    public static final String rootPubAddrW = "tcp://localhost:41010";
    //*************
    public static final String rootPubAddr = "tcp://node-04:41010";

    //intermediate node
    public static final String interUpperPubAddr1W = "tcp://localhost:51010";
    public static final String interLowerPubAddr1W = "tcp://localhost:51020";
    public static final String interUpperPubAddr1 = "tcp://node-11:51010";
    public static final String interLowerPubAddr1 = "tcp://node-11:51020";

    public static final String interUpperPubAddr2W = "tcp://localhost:51011";
    public static final String interLowerPubAddr2W = "tcp://localhost:51021";
    //*************
    public static final String interUpperPubAddr2 = "tcp://node-05:52010";
    public static final String interLowerPubAddr2 = "tcp://node-05:52020";


    //local node
    public static final String localPubAddr1W = "tcp://localhost:61010";
    public static final String localPubAddr1 = "tcp://node-12:61010";

    public static final String localPubAddr2W = "tcp://localhost:62010";
    public static final String localPubAddr2 = "tcp://node-10:62010";

    public static final String localPubAddr3W = "tcp://localhost:61030";
    public static final String localPubAddr3 = "tcp://node-11:63010";

    public static final String localPubAddr4W = "tcp://localhost:61040";
    //*************
    public static final String localPubAddr4 = "tcp://node-06:64010";

    /////////////////////RaspberryPi
    //root node
    public static final String rootPubAddrR = "tcp://169.254.182.235:41010";

    //inter node
    public static final String interUpperPubAddr1R = "tcp://169.254.229.62:51010";
    public static final String interLowerPubAddr1R = "tcp://169.254.229.62:51020";

    //inter node
    public static final String interUpperPubAddr2R = "tcp://169.254.229.62:51010";
    public static final String interLowerPubAddr2R = "tcp://169.254.229.62:51020";

    //local node
    public static final String localPubAddr1R = "tcp://169.254.245.161:61010";
    public static final String localPubAddr2R = "tcp://169.254.245.11:61020";
    public static final String localPubAddr3R = "tcp://169.254.245.16:61030";
    public static final String localPubAddr4R = "tcp://169.254.245.15:61040";

}
