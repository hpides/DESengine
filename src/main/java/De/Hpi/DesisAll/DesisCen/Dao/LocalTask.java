package De.Hpi.DesisAll.DesisCen.Dao;

import org.msgpack.annotation.Message;

import java.util.ArrayList;

@Message
public class LocalTask {

    private int taskId;
    public Query query;
    public ArrayList<Window> windowList;

    //for isEventHere function to process the query
    private long eventTime;
    private long processTime;
    //how many windows are calculated, it is from 1
    private int windowCounter;

    //to calculate how many windows are using this slices,
    // which means the windows that slice be shared with
    //or how many windows are currently processing
    private int windowSlices;

    //operator count, sum, sort, mix
    private int operator;

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public long getEventTime() {
        return eventTime;
    }
    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
    public long getProcessTime() {
        return processTime;
    }
    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }
    public int getWindowCounter() {
        return windowCounter;
    }
    public void setWindowCounter(int windowCounter) {
        this.windowCounter = windowCounter;
    }
    public void windowCounterAdd(){
        this.windowCounter++;
    }
    public int getOperator() {
        return operator;
    }
    public void setOperator(int operator) {
        this.operator = operator;
    }
    public void windowSliceAdd(){
        this.windowSlices++;
    }
    public void windowSliceDelete(){
        this.windowSlices--;
    }
    public int getwindowSlices() {
        return windowSlices;
    }

}
