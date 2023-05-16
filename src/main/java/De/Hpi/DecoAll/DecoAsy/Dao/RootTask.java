package De.Hpi.DecoAll.DecoAsy.Dao;

import java.util.ArrayList;
import java.util.LinkedList;

public class RootTask {

    private int taskId;
    public Query query;
    //to merge queries
//    public ArrayList<QuerySub> querySubs;
    public LinkedList<RootWindow> rootWindows;
    public ArrayList<ArrayList<Tuple>> tupleLists;
    //it is from 0
    private int windowCounter;

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
    public void windowCounterDelete(){this.windowCounter--;}

}
