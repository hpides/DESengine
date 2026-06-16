package org.shared.operators;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.flink.util.Collector;
import org.shared.datagenerator.Tuple;
import org.shared.metrics.MetricTuple;
import org.shared.query.AggregationQuery;
import org.shared.query.QueryChangelog;
import org.shared.query.aggregation.Aggregate;
import org.shared.query.aggregation.AggregationOperation;
import org.shared.result.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaselineAggregationOperator extends MultiQueryAggregationOperator implements Serializable {
    Logger log = LoggerFactory.getLogger(BaselineAggregationOperator.class);

    // For each query: list of currently open windows
    List<List<QueryWindow>> queryWindows;

    public BaselineAggregationOperator() {
        queryWindows = new ArrayList<>();
    }

    @Override
    public void processWindowStarts(int time, List<AggregationQuery> activeQueries, List<Integer> startingWindowIndices, QueryChangelog changelog) {
        for (int queryIndex : startingWindowIndices) {
            boolean isNewQuery = !changelog.getChangelogSet().get(queryIndex);
            startNewQueryWindow(activeQueries.get(queryIndex), queryIndex, isNewQuery, time);
        }
    }

    @Override
    public long processTuple(Tuple tuple, List<AggregationQuery> activeQueries) {
        long updates = 0;
        for (List<QueryWindow> windowsOfSingleQuery : queryWindows) {
            if (windowsOfSingleQuery.isEmpty()) {
                continue;
            }
            if (windowsOfSingleQuery.get(0).query.filter.test(tuple)) {
                for (QueryWindow window : windowsOfSingleQuery) {
                    updates += window.addTuple(tuple);
                }
            }

        }
        return updates;
    }

    @Override
    public long emitQueries(int time, List<AggregationQuery> activeQueries, List<Integer> emittingQueryIndices, long windowEndTimestamp, long windowEndProcessingTime, MetricTuple metrics, Collector<QueryResult> out) {
        for (int queryIndex : emittingQueryIndices) {
            emitQuery(activeQueries.get(queryIndex), queryIndex, time, windowEndTimestamp, windowEndProcessingTime, metrics, out);
        }
        return 0;
    }

    @Override
    public int countExistingAggregates() {
        int count = 0;
        for (List<QueryWindow> windowsOfSingleQuery : queryWindows) {
            for (QueryWindow window : windowsOfSingleQuery) {
                count += window.getAggregateCount();
            }
        }
        return count;
    }

    private void startNewQueryWindow(AggregationQuery query, int queryIndex, boolean isNewQuery, int time) {
        if (isNewQuery) {
            growQueriesListIfNeeded(queryIndex + 1);
            queryWindows.get(queryIndex).clear();
        }
        QueryWindow newWindow = new QueryWindow(time, time + query.window.getLength() - 1, query);
        queryWindows.get(queryIndex).add(newWindow);
    }

    private void emitQuery(AggregationQuery query, int queryIndex, int emissionTime, long windowEndTimestamp, long windowEndProcessingTime, MetricTuple metrics, Collector<QueryResult> out) {
        QueryWindow window = queryWindows.get(queryIndex).get(0);
        assert window.endTime == emissionTime;
        for (Map.Entry<Integer, List<Aggregate>> entry : window.getKeyAggregateMap().entrySet()) {
            int key = entry.getKey();
            List<Aggregate> aggregates = entry.getValue();
            Object aggregateValue = query.aggregation.getFinalResult(aggregates);
            //long eventTimeLatency = (System.currentTimeMillis() - windowEndTimestamp) * 1_000_000L;
            //long processingTimeLatency = (System.nanoTime() - windowEndProcessingTime);
            long eventTimeLatency = (System.nanoTime() - window.latestTupleTimes.get(key).getLeft());
            long processingTimeLatency = (System.nanoTime() - window.latestTupleTimes.get(key).getRight());
            QueryResult result = new QueryResult(
                    key,
                    window.startTime,
                    window.endTime,
                    eventTimeLatency,
                    queryIndex,
                    aggregateValue
            );
            out.collect(result);
            metrics.emissionsCount++;
            metrics.sumEventTimeLatencies += eventTimeLatency;
            metrics.sumProcessingTimeLatencies += processingTimeLatency;
        }
        queryWindows.get(queryIndex).remove(0);

    }

    private void growQueriesListIfNeeded(int newSize) {
        while(queryWindows.size() < newSize) {
            queryWindows.add(new ArrayList<>());
        }
    }

    private static class QueryWindow {
        int startTime;
        int endTime;
        AggregationQuery query;
        int aggregateCount;
        Map<Integer, List<Aggregate>> keyAggregateMap;
        Map<Integer, MutablePair<Long, Long>> latestTupleTimes;

        public QueryWindow(int startTime, int endTime, AggregationQuery query) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.query = query;
            this.aggregateCount = 0;
            this.keyAggregateMap = new HashMap<>();
            this.latestTupleTimes = new HashMap<>();
        }

        public long addTuple(Tuple tuple) {
            long updates = 0;
            List<Aggregate> aggregates = keyAggregateMap.get(tuple.getKey());
            if (aggregates == null) {
                aggregates = new ArrayList<>();
                for (AggregationOperation operation : query.aggregation.getOperations()) {
                    aggregates.add(operation.createAggregate());
                    aggregateCount++;
                }
                keyAggregateMap.put(tuple.getKey(), aggregates);
            }

            for (Aggregate aggregate : aggregates) {
                aggregate.addValue(tuple.getFields()[query.aggregation.getColumn()]);
                updates ++;
            }

            latestTupleTimes.put(tuple.getKey(), MutablePair.of(tuple.getTimestamp(), tuple.getProcessingTime()));
            return updates;
        }

        public Map<Integer, List<Aggregate>> getKeyAggregateMap() {
            return keyAggregateMap;
        }

        public int getAggregateCount() {
            return aggregateCount;
        }
    }
}
