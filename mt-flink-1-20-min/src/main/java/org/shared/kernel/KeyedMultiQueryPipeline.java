package org.shared.kernel;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.shared.datagenerator.DataGenerator;
import org.shared.datagenerator.Tuple;
import org.shared.metrics.MetricTuple;
import org.shared.operators.MultiQueryAggregationOperator;
import org.shared.query.AggregationQuery;
import org.shared.query.QueryChangelog;
import org.shared.query.QueryGenerator;
import org.shared.query.QueryGeneratorConfig;
import org.shared.query.aggregation.Aggregate;
import org.shared.result.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Supplier;

public class KeyedMultiQueryPipeline extends KeyedProcessFunction<Integer, Tuple, QueryResult> {
    public static final Logger log = LoggerFactory.getLogger(KeyedMultiQueryPipeline.class);

    public static final OutputTag<MetricTuple> METRIC_TAG = new OutputTag<>("metrics", TypeInformation.of(MetricTuple.class));

    private final QueryGeneratorConfig queryGeneratorConfig;
    private QueryGenerator queryGenerator;
    private final Supplier<MultiQueryAggregationOperator> operatorFactory;
    private MultiQueryAggregationOperator operator;

    private List<AggregationQuery> activeQueries;
    // Corresponds to the number of non-null fields in activeQueries;
    private int activeQueriesCount;

    private PriorityQueue<PqEntry> windowStartPq;
    private PriorityQueue<PqEntry> emissionPq;

    private int time; // Increased after every one second window.
    private long timeWindowStartTimestamp;
    private long timeWindowEndTimestamp;
    private long latestSetTimer;
    private MetricTuple metrics;

    private boolean firstWindowStarted = false;


    public KeyedMultiQueryPipeline(QueryGeneratorConfig queryGeneratorConfig, Supplier<MultiQueryAggregationOperator> operatorFactory, long firstWindowStartTimestamp) {
        this.operator = operator;
        this.timeWindowStartTimestamp = firstWindowStartTimestamp;
        this.timeWindowEndTimestamp = firstWindowStartTimestamp + 1_000_000_000;
        this.queryGeneratorConfig = queryGeneratorConfig;
        this.operatorFactory = operatorFactory;
    }


    @Override
    public void open(Configuration parameters) {
        this.time = -1;
        this.activeQueries = new ArrayList<>();
        this.activeQueriesCount = 0;
        windowStartPq = new PriorityQueue<>();
        emissionPq = new PriorityQueue<>();
        this.metrics = new MetricTuple();
        this.queryGenerator = new QueryGenerator(queryGeneratorConfig);
        this.operator = operatorFactory.get();
    }

    @Override
    public void processElement(Tuple tuple, Context ctx, Collector<QueryResult> out) throws Exception {
        tuple.setProcessingTime(System.nanoTime());
        if (!firstWindowStarted) {
            firstWindowStarted = true;
            startNewTimeWindow();
        }
        endAndStartWindows(tuple.getTimestamp(), ctx, out);

        long tAddingTuple = System.nanoTime();
        metrics.aggregateUpdatesProcessingTuples += operator.processTuple(tuple, activeQueries);
        metrics.timeSpentProcessingTuples += System.nanoTime() - tAddingTuple;
        metrics.tupleCount++;
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<QueryResult> out) throws Exception {
        endAndStartWindows(ctx.timerService().currentWatermark(), ctx, out);
    }

    public void endAndStartWindows(long watermark, Context ctx, Collector<QueryResult> out) throws Exception {
        while (watermark >= this.timeWindowEndTimestamp) {
            endPreviousTimeWindow(ctx, out);
            if (watermark == Long.MAX_VALUE) { // this indicates end of stream
                break;
            }
            startNewTimeWindow();
        }
        if (latestSetTimer < timeWindowEndTimestamp && watermark < Long.MAX_VALUE) {
            ctx.timerService().registerEventTimeTimer(timeWindowEndTimestamp);
            latestSetTimer = timeWindowEndTimestamp;
        }
    }

    private void endPreviousTimeWindow(Context ctx, Collector<QueryResult> out) {
        long windowEndProcessingTime = System.nanoTime();
        metrics.existingAggregatesCount = operator.countExistingAggregates();

        List<Integer> emittingQueryIndices = checkQueryEmits();
        long tEmitting = System.nanoTime();
        metrics.aggregateUpdatesEmittingQueries = operator.emitQueries(time, activeQueries, emittingQueryIndices, timeWindowEndTimestamp, windowEndProcessingTime, metrics, out);
        long tAfterEmitting = System.nanoTime();
        metrics.timeSpentEmittingQueries = tAfterEmitting - tEmitting;
        metrics.processingEndTimestamp = tAfterEmitting;
        metrics.time = time;
        metrics.totalGlobalAggregateUpdates = Aggregate.getUpdateCounter();
        metrics.dataRate = DataGenerator.tuplesPerSecond;

        ctx.output(METRIC_TAG, metrics);
    }

    private void startNewTimeWindow() {
        metrics = new MetricTuple();

        time ++;
        timeWindowStartTimestamp += 1000_000_000;
        timeWindowEndTimestamp += 1000_000_000;
        QueryChangelog queryChangelog = queryGenerator.changelogForTime(time, timeWindowStartTimestamp);

        updateQueryData(queryChangelog);
        metrics.activeQueriesCount = activeQueriesCount;
        metrics.startingQueriesCount = queryChangelog.getNewQueries().size();
        long now = System.currentTimeMillis();
        metrics.sumQueryLatencies = (now - queryChangelog.getCreationTime()) * 1_000_000 * queryChangelog.getNewQueries().size();

        List<Integer> startingWindowQueryIndices = checkWindowStart();
        long tWindowStarts = System.nanoTime();
        operator.processWindowStarts(time, activeQueries, startingWindowQueryIndices, queryChangelog);
        metrics.timeSpentCreatingSlices = System.nanoTime() - tWindowStarts;

        metrics.processingStartTimestamp = System.nanoTime();
    }

    private void updateQueryData(QueryChangelog changelog) {
        activeQueriesCount += changelog.getNewQueryPositions().size() - changelog.getEndingQueryPositions().size();

        for (int pos : changelog.getEndingQueryPositions()) {
            activeQueries.set(pos, null);
        }

        for (int i = 0; i < changelog.getNewQueryPositions().size(); i++) {
            int pos = changelog.getNewQueryPositions().get(i);
            AggregationQuery query = changelog.getNewQueries().get(i);

            growQueriesListIfNeeded(pos + 1);
            activeQueries.set(pos, query);

            query.window.setStart(time);
            windowStartPq.add(new PqEntry(time, pos, time));
            emissionPq.add(new PqEntry(time + query.window.getLength() - 1, pos, time));
        }
    }

    private List<Integer> checkWindowStart() {
        List<Integer> startingWindowQueryIndices = new ArrayList<>();
        while (!windowStartPq.isEmpty() && windowStartPq.peek().time <= time) {
            assert windowStartPq.peek().time == time;

            PqEntry entry = windowStartPq.poll();
            AggregationQuery query = activeQueries.get(entry.queryId);
            if (entry.isValidForQuery(query)) {
                windowStartPq.add(new PqEntry(time + query.window.getSlide(), entry.queryId, time));
                startingWindowQueryIndices.add(entry.queryId);
            }
        }
        return startingWindowQueryIndices;
    }

    private List<Integer> checkQueryEmits() {
        List<Integer> emittingQueryIndices = new ArrayList<>();
        while (!emissionPq.isEmpty() && emissionPq.peek().time <= time) {
            assert emissionPq.peek().time == time;

            PqEntry entry = emissionPq.poll();
            AggregationQuery query = activeQueries.get(entry.queryId);
            if (entry.isValidForQuery(query)) {
                emissionPq.add(new PqEntry(time + query.window.getSlide(), entry.queryId, time));
                emittingQueryIndices.add(entry.queryId);
            }
        }
        return emittingQueryIndices;
    }

    private void growQueriesListIfNeeded(int newSize) {
        while(activeQueries.size() < newSize) {
            activeQueries.add(null);
        }
    }

    private static class PqEntry implements Serializable, Comparable<PqEntry> {
        final int time;
        final int queryId;
        final int creationTime;

        public PqEntry(int time, int queryId, int creationTime) {
            this.time = time;
            this.queryId = queryId;
            this.creationTime = creationTime;
        }

        @Override
        public int compareTo(PqEntry o) {
            return Integer.compare(time, o.time);
        }

        public boolean isValidForQuery(AggregationQuery query) {
            return creationTime >= query.window.getStart();
        }
    }
}
