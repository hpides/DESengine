package De.Hpi.DecoAll.SingleRound.Dao;

import java.util.ArrayList;

public class LocalWindow{

    private long localWindowId;
    //it decide when to delete this localwindow (is it still used by task)
    private int localWindowCounter;

    //for fixed slice
    public ArrayList<Tuple> fixedTupleList;
    //for unfixed slice
    public ArrayList<Tuple> unfixedTupleList;

    //intermediate result
    public double result;
    public int count;

//    public ArrayList<Tuple> tupleList;

    public long getLocalWindowId() {
        return localWindowId;
    }
    public void setLocalWindowId(long localWindowId) {
        this.localWindowId = localWindowId;
    }
    public int getLocalWindowCounter() {
        return localWindowCounter;
    }
    public void setLocalWindowCounter(int windowCounter) {
        this.localWindowCounter = windowCounter;
    }
    public void windowUsedCounterDelete(){
        this.localWindowCounter--;
    }

}
