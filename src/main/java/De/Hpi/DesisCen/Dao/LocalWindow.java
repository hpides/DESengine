package De.Hpi.DesisCen.Dao;

import java.util.LinkedList;

public class LocalWindow{

    private long windowId;
    //it decide when to delete this localwindow (is it still used by task)
    private int windowUsedCounter;
    //the tasks that need this slice
    public int[] processList;

    //operator 0x count, sum, sort
    public boolean[] operators;
    //intermediate result
    public double sum;
    public long count;
    public LinkedList<Tuple> tupleList;

    public long getWindowId() {
        return windowId;
    }
    public void setWindowId(long windowId) {
        this.windowId = windowId;
    }
    public int getWindowUsedCounter() {
        return windowUsedCounter;
    }
    public void setWindowUsedCounter(int windowCounter) {
        this.windowUsedCounter = windowCounter;
    }
    public void windowUsedCounterDelete(){
        this.windowUsedCounter--;
    }

}
