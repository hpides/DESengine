package De.Hpi.DesisAll.DesisQuantile.Dao;

import org.msgpack.annotation.Message;

@Message
public class Window{

//    private int windowId;
    public int queryId;
    public int windowId;

    //intermediate results
    public double result;
    public long count;

    //slice
    public int sliceId;
    public int sliceCounter;

}
