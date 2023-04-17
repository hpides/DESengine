package De.Hpi.DesisAll.Desis.Configure;

public interface ConfigurationProcessing {

    //Scenario
    public static final int CentralizedAggregation = 1;
    public static final int DeCentralizedAggregation = -1;

    //local node process flag
    public static final int EVENTSTART = 1;
    public static final int EVENTEND = 2;
    public static final int EVENTENDANDSTART = 3;
    public static final int EVENTNOTHING = 4;
    public static final int EVENTWAITING = 5;
    public static final int EVENTSubSlidingWindowEnd = 6;
    public static final int EVENTLONGGAPEND = 7;

    //operators
    public static final int OPERATORS = 6;
    public static final int COUNTOPERATOR = 0;
    public static final int SUMOPERATOR = 1;
    public static final int SORTOPERATOR = 2;
    public static final int PRESERVEOPERATOR = 3;;
    public static final int MAXOPERATOR = 4;
    public static final int MINOPERATOR = 5;
//    public static final int SAVE = 5;
}
