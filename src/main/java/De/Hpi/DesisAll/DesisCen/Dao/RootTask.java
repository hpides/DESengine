package De.Hpi.DesisAll.DesisCen.Dao;

import java.util.LinkedList;

public class RootTask {

    private int taskId;
    public Query query;
    public LinkedList<RootWindow> rootWindowLinkedList;
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
