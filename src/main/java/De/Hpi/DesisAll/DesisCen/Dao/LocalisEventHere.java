package De.Hpi.DesisAll.DesisCen.Dao;

public class LocalisEventHere {

    //the window of this query is end and need to be calculated
    public boolean[] endList;
    //the window of this query is processing
    public int[] processList;
    //mark down the current state of window that belong to this query
    public int[] stateList;
    //in case there is a long gap and multiple windows end
    public int[] multipleWindowEndList;
    //to break down functions into operators
    public boolean[] functions;
    //there are three statues for each window start end and processing
    private int createNewWindow;
    private int finishWindow;
    private int processWindow;

    public int getProcessWindow() {
        return processWindow;
    }
    public void setProcessWindow(int processWindow) {
        this.processWindow = processWindow;
    }
    public void addProcessWindow(int num) {
        this.processWindow+=num;
    }
    public int getCreateNewWindow() {
        return createNewWindow;
    }
    public void setCreateNewWindow(int createNewWindow) {
        this.createNewWindow = createNewWindow;
    }
    public void addCreateNewWindow() {
        this.createNewWindow++;
    }
    public int getFinishWindow() {
        return finishWindow;
    }
    public void setFinishWindow(int finishWindow) {
        this.finishWindow = finishWindow;
    }
    public void addFinishWindow() {
        this.finishWindow++;
    }
}
