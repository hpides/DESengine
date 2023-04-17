package De.Hpi.DesisAll.DesisSW.Dao;

public class IntermediateWindow {

    private int windowId;
    //for 2)less window arrived
    private int windowWaitCounter;
    //to check expired
    private long processTime;
    public Window window;

    public int getWindowId() {
        return windowId;
    }
    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }
    public int getWindowWaitCounter() {
        return windowWaitCounter;
    }
    public void setWindowWaitCounter(int windowWaitCounter) {
        this.windowWaitCounter = windowWaitCounter;
    }
    public void deleteWindowWaitingCounter() {
        this.windowWaitCounter++;
    }
    public long getProcessTime() {
        return processTime;
    }
    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

}
