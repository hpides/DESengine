package De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Dao;

public class LocalisEventHere {

    //the window of this query is end and need to be calculated
//    public boolean[] endList;
    //the window of this query is processing
    public int[] processList;
    //calculate how many times for window to be processed in total
    private int processCount;
    private int processCountNonDecom;
    //mark down the current state of window that belong to this query
//    public int[] stateList;
    //in case there is a long gap and multiple windows end
//    public int[] multipleWindowEndList;
    //to break down functions into operators
//    public boolean[] functions;

    //there are three statues for each window start end and processing
    private boolean createNewWindow;
    private boolean finishWindow;
    private boolean processWindow;

    public int getProcessCount() {
        return processCount;
    }
    public void setProcessCount(int processCount) {
        this.processCount = processCount;
    }
    public void addProcessCount(int num) {
        this.processCount+=num;
    }

    public int getProcessCountNonDecom() {
        return processCountNonDecom;
    }
    public void setProcessCountNonDecom(int processCountNonDecom) {
        this.processCountNonDecom = processCountNonDecom;
    }
    public void addProcessCountNonDecom(int num) {
        this.processCountNonDecom+=num;
    }

    public boolean isCreateNewWindow() {
        return createNewWindow;
    }
    public void setCreateNewWindow(boolean createNewWindow) {
        this.createNewWindow = createNewWindow;
    }
    public boolean isFinishWindow() {
        return finishWindow;
    }
    public void setFinishWindow(boolean finishWindow) {
        this.finishWindow = finishWindow;
    }
    public boolean isProcessWindow() {
        return processWindow;
    }
    public void setProcessWindow(boolean processWindow) {
        this.processWindow = processWindow;
    }
}
