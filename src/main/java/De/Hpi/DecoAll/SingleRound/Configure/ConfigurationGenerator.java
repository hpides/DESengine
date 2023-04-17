package De.Hpi.DecoAll.SingleRound.Configure;

public interface ConfigurationGenerator {

//    Linux
    public static final String LINUX_FILEADDRESS = "/hpi/fs00/home/wang.yue/data/sorted.csv";
    public static final String LINUX_FILEADDRESS_0 = "/hpi/fs00/home/wang.yue/data/sorted_0.csv";
    public static final String LINUX_FILEADDRESS_1 = "/hpi/fs00/home/wang.yue/data/sorted_1.csv";
    public static final String LINUX_FILEADDRESS_2 = "/hpi/fs00/home/wang.yue/data/sorted_2.csv";
    public static final String LINUX_FILEADDRESS_3 = "/hpi/fs00/home/wang.yue/data/sorted_3.csv";

    //WIndows
    public static final String WINDOWS_FILEADDRESS =   "E:/sourcedata/sorted.csv";
    public static final String WINDOWS_FILEADDRESS_0 = "E:/sourcedata/sorted_0.csv";
    public static final String WINDOWS_FILEADDRESS_1 = "E:/sourcedata/sorted_1.csv";
    public static final String WINDOWS_FILEADDRESS_2 = "E:/sourcedata/sorted_2.csv";
    public static final String WINDOWS_FILEADDRESS_3 = "E:/sourcedata/sorted_3.csv";
    //to slow generator in case it too fast
    public static final int DATAGENERATORFREQUENCY = 1;
    public static final int DATAGENERATORMAXIMIUMBUFFER = 1000;

    //simulate event
    public static final int EVENTRANDOMSEED = 1000/2;

    //data limited
    public static final int MAXBUFFERSIZE = 1000;

    //duplicate tuples x, x times reading speed
    public static final int DUPLICATETUPLE = 9;
}
