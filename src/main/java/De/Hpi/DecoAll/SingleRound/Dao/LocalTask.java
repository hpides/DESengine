package De.Hpi.DecoAll.SingleRound.Dao;

import org.msgpack.annotation.Message;

@Message
public class LocalTask {

    private int taskId;
    public Query query;

    //for isEventHere function to process the query
    private long eventTime;
    private long processTime;
    //for all time based window we need a initial time, to process window id when long gap
    private long initialTime;

    //how many windows are calculated, it is from 1, the window id
    private int windowId;
    //in case there is a long gap so, we need to save the window id after long gap
    //and window id can not be calculated as plus 1
    //the windowCount is how many windows are finish
    //windowCount < windowId & windowCount + 1 = windowId
    private int windowCount;

    //to calculate how many windows are using this slices,
    // which means the windows that slice be shared with
    //or how many windows are currently processing
    private int windowSlices;

    //window end
    private boolean windowEnd;

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
    public int getWindowId() {
        return windowId;
    }
    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }
    public int getWindowCount() {
        return windowCount;
    }
    public void setWindowCount(int windowCount) {
        this.windowCount = windowCount;
    }
    public long getInitialTime() {
        return initialTime;
    }
    public void setInitialTime(long initialTime) {
        this.initialTime = initialTime;
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
    public boolean getWindowEnd() {
        return windowEnd;
    }
    public void setWindowEnd(boolean windowEnd) {
        this.windowEnd = windowEnd;
    }

}
