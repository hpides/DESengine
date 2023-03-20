package De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Dao;

import java.util.ArrayList;
import java.util.LinkedList;

public class IntermediateTask {

    private int taskId;
    public Query query;
    public ArrayList<QuerySub> querySubs;
    public LinkedList<IntermediateWindow> intermediateWindows;
    //it is from 1
    private int windowCounter;
    //form 0
    private int batchCounter;

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public int getWindowCounter() {
        return windowCounter;
    }
    public void setWindowCounter(int windowCounter) {
        this.windowCounter = windowCounter;
    }
    public void windowCounterAdd(){
        this.windowCounter++;
    }
    public int getBatchCounter() {
        return batchCounter;
    }
    public void setBatchCounter(int batchCounter) {
        this.batchCounter = batchCounter;
    }
    public void batchCounterAdd(){
        this.batchCounter++;
    }

}
