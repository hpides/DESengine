package De.Hpi.DesisAll.DesisCen.Dao;

public class RootWindow {

    private long windowId;
    //for 2)less window arrived
    private int windowWaittingCounter;
    //to check expired
    private long processTime;
    public Window window;

    public long getWindowId() {
        return windowId;
    }
    public void setWindowId(long windowId) {
        this.windowId = windowId;
    }
    public int getWindowWaittingCounter() {
        return windowWaittingCounter;
    }
    public void setWindowWaittingCounter(int windowWaittingCounter) {
        this.windowWaittingCounter = windowWaittingCounter;
    }
    public void deleteWindowWaittingCounter() {
        this.windowWaittingCounter++;
    }
    public long getProcessTime() {
        return processTime;
    }
    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

}
