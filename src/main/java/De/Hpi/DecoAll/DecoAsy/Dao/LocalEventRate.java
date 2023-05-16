package De.Hpi.DecoAll.DecoAsy.Dao;

import java.util.LinkedList;

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
    public int counterForStep;

    public double predictedWindowSize;
    public double previousPredictedWindowSize;
    public double correctionFactor;
    public double previousCorrectionFactor;
    public LinkedList<Double> previousCorrectionFactorList;

}
