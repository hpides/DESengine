package De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.LocalNode.BaselineNoOptimizer;

import De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Dao.Tuple;

import java.util.LinkedList;

public class BaselineLocalWindow {

    private long windowId;
    //it decide when to delete this BaselineLocalWindow (is it still used by task)
    private int windowUsedCounter;
    //the tasks that need this slice
    public int[] processList;

    public int startIndex;
    public int endIndex;

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
