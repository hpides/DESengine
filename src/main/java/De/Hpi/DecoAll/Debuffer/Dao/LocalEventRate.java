package De.Hpi.DecoAll.Debuffer.Dao;

public class LocalEventRate {

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public double getEventRates() {
        return eventRates;
    }

    public void setEventRates(double eventRates) {
        this.eventRates = eventRates;
    }

    private int nodeId;
    private double eventRates;

}
