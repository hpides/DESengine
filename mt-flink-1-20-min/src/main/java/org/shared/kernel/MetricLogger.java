package org.shared.kernel;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.shared.metrics.MetricTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Math.max;

public class MetricLogger extends ProcessFunction<MetricTuple, MetricTuple> {
    private final static Logger log = LoggerFactory.getLogger(MetricLogger.class);
    private long firstStartTimestamp;
    private long lastEndTimestamp;
    private long lastBatchTime;
    private double lastDataRate = 9999999999999999999.9;
    private long overallTime;
    private long overallTupleCount;
    private long overallMultipliedTupleCount;
    private long overallSumEventTimeLatencies;
    private long overallSumProcessingTimeLatencies;
    private long overallEmissionsCount;
    private long overallSumQueryLatencies;
    private long overallQueryStartCount;
    private long overallAggregateUpdatesProcessingTuples;
    private long overallAggregateUpdatesEmittingQueries;
    private long overallTimeSpentOnStartingSlices;
    private long overallTimeSpentOnProcessingTuples;
    private long overallTimeSpentOnEmittingQueries;
    private long overallMaxExistingAggregates;

    @Override
    public void processElement(MetricTuple metrics, Context ctx, Collector<MetricTuple> out) throws Exception {
        updateOverallValues(metrics);

        log.info("--------");
        logDataRate(metrics);
        logThroughput(metrics);
        logEventTimeLatency(metrics);
        logProcessingTimeLatency(metrics);
        logQueryDeploymentLatency(metrics);
        logExistingAggregates(metrics);
        logAggregateUpdates(metrics);
        logWhereTimeIsSpent(metrics);

        out.collect(metrics);
    }

    private void updateOverallValues(MetricTuple metrics) {
        if (firstStartTimestamp == 0) {
            firstStartTimestamp = metrics.processingStartTimestamp;
            lastEndTimestamp = metrics.processingStartTimestamp;
        }
        if (metrics.dataRate < lastDataRate) {
            overallTupleCount = 0;
            overallMultipliedTupleCount = 0;
            overallEmissionsCount = 0;
            overallSumEventTimeLatencies = 0;
            overallSumProcessingTimeLatencies = 0;

            overallAggregateUpdatesProcessingTuples = 0;
            overallAggregateUpdatesEmittingQueries =0;

            overallTimeSpentOnStartingSlices = 0;
            overallTimeSpentOnProcessingTuples = 0;
            overallTimeSpentOnEmittingQueries = 0;

            overallMaxExistingAggregates = 0;
            firstStartTimestamp = metrics.processingStartTimestamp;
            lastDataRate = metrics.dataRate;
        }
        lastBatchTime = metrics.processingEndTimestamp - lastEndTimestamp;
        overallTime = metrics.processingEndTimestamp - firstStartTimestamp;
        lastEndTimestamp = metrics.processingEndTimestamp;

        overallTupleCount += metrics.tupleCount;
        overallMultipliedTupleCount += metrics.tupleCount * metrics.activeQueriesCount;

        overallEmissionsCount += metrics.emissionsCount;
        overallSumEventTimeLatencies += metrics.sumEventTimeLatencies;
        overallSumProcessingTimeLatencies += metrics.sumProcessingTimeLatencies;

        overallSumQueryLatencies += metrics.sumQueryLatencies;
        overallQueryStartCount += metrics.startingQueriesCount;

        overallAggregateUpdatesProcessingTuples += metrics.aggregateUpdatesProcessingTuples;
        overallAggregateUpdatesEmittingQueries += metrics.aggregateUpdatesEmittingQueries;

        overallTimeSpentOnStartingSlices += metrics.timeSpentCreatingSlices;
        overallTimeSpentOnProcessingTuples += metrics.timeSpentProcessingTuples;
        overallTimeSpentOnEmittingQueries += metrics.timeSpentEmittingQueries;

        overallMaxExistingAggregates = max(overallMaxExistingAggregates, metrics.existingAggregatesCount);
    }

    private void logThroughput(MetricTuple metrics) {
        log.info("Data throughput (M/s) now: {} - overall: {}",
                String.format("%.2f", metrics.tupleCount * 1e3 / lastBatchTime),
                String.format("%.2f", overallTupleCount * 1e3 / overallTime));
        log.info("Multiplied throughput (data throughput * #queries) (M/s) now: {} - overall: {}",
                String.format("%.2f", metrics.tupleCount * 1e3 * metrics.activeQueriesCount / lastBatchTime),
                String.format("%.2f", overallMultipliedTupleCount * 1e3 / overallTime));
    }

    private void logDataRate(MetricTuple metrics) {
        log.info("Data Source Rate (M/s) now: {}",
                String.format("%.2f", metrics.dataRate * 1e-6));
    }

    private void logEventTimeLatency(MetricTuple metrics) {
        log.info("Event time latency (ms) now: {} - overall: {}",
                String.format("%.2f", metrics.sumEventTimeLatencies * 1e-6 / metrics.emissionsCount),
                String.format("%.2f", overallSumEventTimeLatencies * 1e-6 / overallEmissionsCount));
    }

    private void logProcessingTimeLatency(MetricTuple metrics) {
        log.info("Processing time latency (ms) now: {} - overall: {}",
                String.format("%.2f", metrics.sumProcessingTimeLatencies * 1e-6 / metrics.emissionsCount),
                String.format("%.2f", overallSumProcessingTimeLatencies * 1e-6 / overallEmissionsCount));
    }

    private void logQueryDeploymentLatency(MetricTuple metrics) {
        //log.info("Query deployment latency (s) now: {} - overall: {}",
        //        String.format("%.2f", metrics.sumQueryLatencies * 1e-9 / metrics.startingQueriesCount),
        //        String.format("%.2f", overallSumQueryLatencies * 1e-9 / overallQueryStartCount));
    }

    private void logExistingAggregates(MetricTuple metrics) {
        log.info("Existing aggregates now: {} - max: {}",
                metrics.existingAggregatesCount,
                overallMaxExistingAggregates);
    }

    private void logAggregateUpdates(MetricTuple metrics) {
        log.info("Aggregate updates per processed tuple: now: {} - overall: {}",
                String.format("%.2f", (double) metrics.aggregateUpdatesProcessingTuples / metrics.tupleCount),
                String.format("%.2f", (double) overallAggregateUpdatesProcessingTuples / overallTupleCount));
        log.info("Aggregate updates per emission: now: {} - overall: {}",
                String.format("%.2f", (double) metrics.aggregateUpdatesEmittingQueries / metrics.emissionsCount),
                String.format("%.2f", (double) overallAggregateUpdatesEmittingQueries / overallEmissionsCount));
    }

    private void logWhereTimeIsSpent(MetricTuple metrics) {
        long batchTotalTime = metrics.timeSpentCreatingSlices + metrics.timeSpentProcessingTuples + metrics.timeSpentEmittingQueries;
        long overallTotalTime = overallTimeSpentOnStartingSlices + overallTimeSpentOnProcessingTuples + overallTimeSpentOnEmittingQueries;
        log.info("Time spent starting windows / slices (s): now: {} ({}%) - overall: {} ({}%)",
                String.format("%.2f", metrics.timeSpentCreatingSlices * 1e-9),
                String.format("%.2f", (100.0 * metrics.timeSpentCreatingSlices / batchTotalTime)),
                String.format("%.2f", overallTimeSpentOnStartingSlices * 1e-9),
                String.format("%.2f", (100.0 * overallTimeSpentOnStartingSlices / overallTotalTime)));
        log.info("Time spent processing tuples (s): now: {} ({}%) - overall: {} ({}%)",
                String.format("%.2f", metrics.timeSpentProcessingTuples * 1e-9),
                String.format("%.2f", (100.0 * metrics.timeSpentProcessingTuples / batchTotalTime)),
                String.format("%.2f", overallTimeSpentOnProcessingTuples * 1e-9),
                String.format("%.2f", (100.0 * overallTimeSpentOnProcessingTuples / overallTotalTime)));
        log.info("Time spent emitting query results (s): now: {} ({}%) - overall: {} ({}%)",
                String.format("%.2f", metrics.timeSpentEmittingQueries * 1e-9),
                String.format("%.2f", (100.0 * metrics.timeSpentEmittingQueries / batchTotalTime)),
                String.format("%.2f", overallTimeSpentOnEmittingQueries * 1e-9),
                String.format("%.2f", (100.0 * overallTimeSpentOnEmittingQueries / overallTotalTime)));
    }

}
