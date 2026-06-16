package org.shared.kernel;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.shared.metrics.MetricTuple;
import org.shared.operators.MultiQueryAggregationOperator;
import org.shared.datagenerator.Tuple;
import org.shared.query.AggregationQuery;
import org.shared.query.QueryChangelog;
import org.shared.query.QueryGenerator;
import org.shared.query.aggregation.Aggregate;
import org.shared.result.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class MultiQueryPipeline extends ProcessFunction<Tuple, QueryResult> {
    public static final Logger log = LoggerFactory.getLogger(MultiQueryPipeline.class);

    public static final OutputTag<MetricTuple> METRIC_TAG = new OutputTag<>("metrics", TypeInformation.of(MetricTuple.class));

    private QueryGenerator queryGenerator;
    private MultiQueryAggregationOperator operator;

    private List<AggregationQuery> activeQueries;
    // Corresponds to the number of non-null fields in activeQueries;
    private int activeQueriesCount;

    private PriorityQueue<PqEntry> windowStartPq;
    private PriorityQueue<PqEntry> emissionPq;

    private int time; // Increased after every one second window.
    private long timeWindowStartTimestamp;
    private long timeWindowEndTimestamp;
    private MetricTuple metrics;


    public MultiQueryPipeline(QueryGenerator queryGenerator, MultiQueryAggregationOperator operator) {
        this.queryGenerator = queryGenerator;
        this.operator = operator;
        this.time = -1;
        this.activeQueries = new ArrayList<>();
        this.activeQueriesCount = 0;
        windowStartPq = new PriorityQueue<>();
        emissionPq = new PriorityQueue<>();
        this.timeWindowStartTimestamp = 0;
        this.timeWindowEndTimestamp = 0;
        this.metrics = new MetricTuple();
    }

    @Override
    public void processElement(Tuple tuple, Context ctx, Collector<QueryResult> out) throws Exception {
        tuple.setProcessingTime(System.nanoTime());
        if (this.timeWindowEndTimestamp == 0) {
            this.timeWindowStartTimestamp = tuple.getTimestamp() - 1_000_000_000; // This gets moved by the call to startNewWindow()
            this.timeWindowEndTimestamp = tuple.getTimestamp();
            startNewTimeWindow();
        }
        while(tuple.getTimestamp() >= this.timeWindowEndTimestamp) {
            endPreviousTimeWindow(ctx, out);
            startNewTimeWindow();
        }
        long tAddingTuple = System.nanoTime();
        operator.processTuple(tuple, activeQueries);
        metrics.timeSpentProcessingTuples += System.nanoTime() - tAddingTuple;
        metrics.tupleCount++;
    }

    private void endPreviousTimeWindow(Context ctx, Collector<QueryResult> out) {
        long windowEndProcessingTime = System.nanoTime();
        metrics.existingAggregatesCount = operator.countExistingAggregates();
        metrics.aggregateUpdatesProcessingTuples = Aggregate.getUpdateCounter();
        Aggregate.resetUpdateCounter();

        List<Integer> emittingQueryIndices = checkQueryEmits();
        long tEmitting = System.nanoTime();
        operator.emitQueries(time, activeQueries, emittingQueryIndices, timeWindowEndTimestamp, windowEndProcessingTime, metrics, out);
        long tAfterEmitting = System.nanoTime();
        metrics.timeSpentEmittingQueries = tAfterEmitting - tEmitting;
        metrics.processingEndTimestamp = tAfterEmitting;
        metrics.aggregateUpdatesEmittingQueries = Aggregate.getUpdateCounter();
        metrics.time = time;

        ctx.output(METRIC_TAG, metrics);
    }

    private void startNewTimeWindow() {
        metrics = new MetricTuple();

        time ++;
        timeWindowStartTimestamp += 1_000_000_000;
        timeWindowEndTimestamp += 1_000_000_000;
        QueryChangelog queryChangelog = queryGenerator.changelogForTime(time, timeWindowStartTimestamp);

        updateQueryData(queryChangelog);
        metrics.activeQueriesCount = activeQueriesCount;
        metrics.startingQueriesCount = queryChangelog.getNewQueries().size();
        long now = System.nanoTime();
        metrics.sumQueryLatencies = (now - queryChangelog.getCreationTime()) * 1_000_000 * queryChangelog.getNewQueries().size();

        List<Integer> startingWindowQueryIndices = checkWindowStart();
        Aggregate.resetUpdateCounter();
        long tWindowStarts = System.nanoTime();
        operator.processWindowStarts(time, activeQueries, startingWindowQueryIndices, queryChangelog);
        metrics.timeSpentCreatingSlices = System.nanoTime() - tWindowStarts;
        metrics.aggregateUpdatesProcessingTuples = Aggregate.getUpdateCounter();

        Aggregate.resetUpdateCounter();
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

    private static class PqEntry implements Comparable<PqEntry> {
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
