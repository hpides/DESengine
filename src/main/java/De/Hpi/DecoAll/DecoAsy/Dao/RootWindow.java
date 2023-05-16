package De.Hpi.DecoAll.DecoAsy.Dao;

public class RootWindow {

    private int windowId;
    //for 2)less window arrived
    private int windowWaitCounter;
    //to check expired
//    private long processTime;
//    public Window window;
    //intermediate result
    public double result;
    public int count;

    public int getWindowId() {
        return windowId;
    }
    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }
    public void addWindowId() {
        this.windowId++;
    }

    public int getWindowWaitCounter() {
        return windowWaitCounter;
    }
    public void setWindowWaitCounter(int windowWaittingCounter) {
        this.windowWaitCounter = windowWaittingCounter;
    }
    public void deleteWindowWaitingCounter() {
        this.windowWaitCounter--;
    }
    public void addWindowWaitingCounter() {
        this.windowWaitCounter++;
    }
//    public long getProcessTime() {
//        return processTime;
//    }
//    public void setProcessTime(long processTime) {
//        this.processTime = processTime;
//    }

}
