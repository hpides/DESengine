package De.Hpi.DecoAll.Deco.Dao;

import org.msgpack.annotation.Message;

import java.util.ArrayList;

@Message
public class WindowCollection {

    //private int windowId;
    public int nodeId;
    //to align of windows, since all
    // timebased windows are end at the same time
    //it's from 0
    public int sliceId;
    public int sliceCounter;
    public ArrayList<Window> windowList;
    public ArrayList<Tuple> tuples;
}
