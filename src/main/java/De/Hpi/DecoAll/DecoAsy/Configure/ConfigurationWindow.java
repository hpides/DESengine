package De.Hpi.DecoAll.DecoAsy.Configure;

public interface ConfigurationWindow {

    //object
    public static final int SPEED = 1;

    //function
    public static final int COUNT = 0;
    public static final int SUM = 1;
    public static final int AVERAGE = 2;
    public static final int MAX = 3;
    public static final int MIN = 4;
    public static final int QUANTILE = 5;
    public static final int MEDIAN = 6;
    public static final int NON = 7;
    public static final int FUNCTIONS = 8;

    //window
    public static final int TUMBING = 0;
    public static final int SLIDING = 1;
    //hoping window
    public static final int SLIDING_UNEVEN = 2;
    public static final int SESSION = 3;
    public static final int PUNCTUATION = 4;
    public static final int COUNTBASED = 5;
    public static final int WINDOWTYPES = 6;
}
