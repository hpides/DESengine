package org.shared.metrics;

import java.io.Serializable;

public class MetricTuple implements Serializable {
    public long processingStartTimestamp;
    public long processingEndTimestamp;
    public long tupleCount;
    public long emissionsCount;
    public long activeQueriesCount;
    public long startingQueriesCount;
    public long sumEventTimeLatencies;
    public long sumQueryLatencies;
    public long existingAggregatesCount;
    public long totalGlobalAggregateUpdates;
    public long aggregateUpdatesProcessingTuples;
    public long aggregateUpdatesEmittingQueries;
    public long timeSpentCreatingSlices;
    public long timeSpentProcessingTuples;
    public long timeSpentEmittingQueries;
    public long sumProcessingTimeLatencies;
    public long time; // increases by one for each 1s slice. Used to merge tuples of parallel operator instances.
    public double dataRate;

    public MetricTuple() {}

    public String toCSVLine() {
        return processingStartTimestamp + ", " +
                processingEndTimestamp + ", " +
                tupleCount + ", " +
                emissionsCount + ", " +
                activeQueriesCount + ", " +
                startingQueriesCount + ", " +
                sumEventTimeLatencies + ", " +
                sumQueryLatencies + ", " +
                existingAggregatesCount + ", " +
                totalGlobalAggregateUpdates + ", " +
                aggregateUpdatesProcessingTuples + ", " +
                aggregateUpdatesEmittingQueries + ", " +
                timeSpentCreatingSlices + ", " +
                timeSpentProcessingTuples + ", " +
                timeSpentEmittingQueries + ", " +
                sumProcessingTimeLatencies + ", " +
                time + ", " +
                dataRate;
    }
}
