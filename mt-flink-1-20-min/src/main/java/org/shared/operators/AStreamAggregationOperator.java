package org.shared.operators;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.flink.util.Collector;
import org.shared.datagenerator.Tuple;
import org.shared.metrics.MetricTuple;
import org.shared.query.AggregationQuery;
import org.shared.query.QueryChangelog;
import org.shared.result.QueryResult;
import org.shared.query.aggregation.Aggregate;
import org.shared.query.aggregation.AggregationOperation;
import org.shared.util.BitmapCompressed;
import org.shared.util.BitmapUncompressed;
import org.shared.util.BitmapWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class AStreamAggregationOperator extends MultiQueryAggregationOperator implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(AStreamAggregationOperator.class);
    private final boolean compressBitmaps;

    private List<Slice> slices;
    private BitmapWrapper[][] changelogSetTable;

    public AStreamAggregationOperator(boolean compressBitmaps) {
        this.compressBitmaps = compressBitmaps;
        slices = new ArrayList<>();
    }

    @Override
    public void processWindowStarts(int time, List<AggregationQuery> activeQueries, List<Integer> startingWindowIndices, QueryChangelog changelog) {
        if (!startingWindowIndices.isEmpty() || changelog != null) {
            startNewSlice(time, changelog, activeQueries);
        }
    }

    @Override
    public long processTuple(Tuple tuple, List<AggregationQuery> activeQueries) {
        tuple = sharedSelection(tuple, activeQueries);
        if (tuple != null) {
            return currentSlice().addTuple(tuple);
        }
        return 0;
    }

    @Override
    public long emitQueries(int time, List<AggregationQuery> activeQueries, List<Integer> emittingQueryIndices, long windowEndTimestamp, long windowEndProcessingTime, MetricTuple metrics, Collector<QueryResult> out) {
        long updates = 0;
        for (int queryIndex : emittingQueryIndices) {
            updates += emitQuery(activeQueries.get(queryIndex), queryIndex, time, windowEndTimestamp, windowEndProcessingTime, metrics, out);
        }
        return updates;
    }

    @Override
    public int countExistingAggregates() {
        int count = 0;
        for (Slice slice : slices) {
            count += slice.getAggregateCount();
        }
        return count;
    }

    private void startNewSlice(int time, QueryChangelog queryChangelog, List<AggregationQuery> activeQueries) {
        log.debug("startNewSlice: {}", time);
        BitmapWrapper changelogSet = extractOrCreateChangelogSet(queryChangelog, activeQueries.size());

        Slice slice = new Slice(time, changelogSet, activeQueries);
        slices.add(slice);

        discardOutdatedSlices(time, activeQueries);
        updateChangelogSetTable();
    }

    private long emitQuery(AggregationQuery query, int queryIndex, int emissionTime, long windowEndTimestamp, long windowEndProcessingTime, MetricTuple metrics, Collector<QueryResult> out) {
        log.debug("emit Q{}: {}", queryIndex, query);
        AtomicLong updates = new AtomicLong();
        Map<Integer, List<Aggregate>> totalAggregates = new HashMap<>();
        Map<Integer, MutablePair<Long, Long>> tupleTimes = new HashMap<>();

        int windowStartTime = emissionTime - (query.window.getLength() - 1);
        for (int i = slices.size() - 1; i >= 0 && slices.get(i).startTime >= windowStartTime; i--) {
            Slice slice = slices.get(i);
            if (!changelogSetTable[slices.size() - 1][i].get(queryIndex)) {
                break;
            }
            slice.aggregates.get(queryIndex).forEach((key, keyAggregates) -> {
                totalAggregates.computeIfAbsent(key, k -> {
                    List<Aggregate> aggregates = new ArrayList<>();
                    for (AggregationOperation operation : query.aggregation.getOperations()) {
                        aggregates.add(operation.createAggregate());
                    }
                    return aggregates;
                });
                for (int j = 0; j < totalAggregates.get(key).size(); j++) {
                    totalAggregates.get(key).get(j).addAggregate(keyAggregates.get(j));
                    updates.getAndIncrement();
                }
            });
            slice.latestTupleTimes.get(queryIndex).forEach((key, times) -> {
                tupleTimes.merge(key, MutablePair.of(times.getLeft(), times.getRight()), (a, b) ->
                        MutablePair.of(Math.max(a.getLeft(), b.getLeft()), Math.max(a.getRight(), b.getRight())));
            });
        }
        for (Map.Entry<Integer, List<Aggregate>> entry : totalAggregates.entrySet()) {
            int key = entry.getKey();
            List<Aggregate> aggregates = entry.getValue();
            //long eventTimeLatency = (System.currentTimeMillis() - windowEndTimestamp) * 1_000_000L;
            //long processingTimeLatency = (System.nanoTime() - windowEndProcessingTime);
            long eventTimeLatency = (System.nanoTime() - tupleTimes.get(key).getLeft());
            long processingTimeLatency = (System.nanoTime() - tupleTimes.get(key).getRight());
            QueryResult result = new QueryResult(
                    key,
                    windowStartTime,
                    emissionTime,
                    eventTimeLatency,
                    queryIndex,
                    query.aggregation.getFinalResult(aggregates));
            log.debug(result.toString());
            out.collect(result);
            metrics.emissionsCount ++;
            metrics.sumEventTimeLatencies += eventTimeLatency;
            metrics.sumProcessingTimeLatencies += processingTimeLatency;
        }
        return updates.get();
    }

    private Slice currentSlice() {
        return slices.get(slices.size() - 1);
    }

    private Tuple sharedSelection(Tuple tuple, List<AggregationQuery> activeQueries) {
        BitmapWrapper bitSet = createEmptyBitset(activeQueries.size());
        for (int queryIndex = 0; queryIndex < activeQueries.size(); queryIndex++) {
            AggregationQuery query = activeQueries.get(queryIndex);
            if (query != null && query.filter.test(tuple)) {
                bitSet.set(queryIndex);
            }
        }
        if (bitSet.isEmpty()) {
            return null;
        } else {
            tuple.setQuerySet(bitSet);
            return tuple;
        }
    }

    private BitmapWrapper extractOrCreateChangelogSet(QueryChangelog queryChangelog, int activeQueriesSize) {
        BitmapWrapper changelogSet = queryChangelog.getChangelogSet();
        if (changelogSet == null) {
            changelogSet = createEmptyBitset(activeQueriesSize);
            changelogSet.set(0, activeQueriesSize);
        }
        return changelogSet;
    }

    private BitmapWrapper createEmptyBitset(int initialCapacity) {
        if (compressBitmaps) {
            return new BitmapCompressed();
        } else {
            return new BitmapUncompressed(initialCapacity);
        }
    }

    private void discardOutdatedSlices(int time, List<AggregationQuery> activeQueries) {
        int earliestRelevantTime = time;
        for (AggregationQuery query : activeQueries) {
            if (time - query.window.getLength() < query.window.getStart()) {
                earliestRelevantTime = Math.min(earliestRelevantTime, query.window.getStart());
            } else {
                earliestRelevantTime = Math.min(earliestRelevantTime, time - query.window.getLength());
            }
        }
        discardSlicesOlderThan(earliestRelevantTime);
    }

    private void discardSlicesOlderThan(int time) {
        int firstNotDiscardedIndex = 0;
        while (firstNotDiscardedIndex < slices.size() - 1 && slices.get(firstNotDiscardedIndex).startTime < time) {
            firstNotDiscardedIndex++;
        }
        int leftToDiscard = firstNotDiscardedIndex - 1;

        while (leftToDiscard > 0) {
            slices.remove(0);
            leftToDiscard--;
        }
    }

    private void updateChangelogSetTable() {
        changelogSetTable = new BitmapWrapper[slices.size()][slices.size()];

        for (int i = 0; i < changelogSetTable.length; i++) {
            for (int j = i; j >= 0; j--) {
                if (i == j) {
                    BitmapWrapper bs;
                    if (compressBitmaps) {
                        bs = new BitmapCompressed();
                    } else {
                        bs = new BitmapUncompressed(slices.get(j).activeQueries.size());
                    }
                    bs.set(0, slices.get(j).activeQueries.size());
                    changelogSetTable[i][j] = bs;
                } else if (i == j + 1) {
                    changelogSetTable[i][j] = slices.get(i).getChangelogSet();
                } else {
                    BitmapWrapper bs1 = changelogSetTable[i][j + 1].copy();
                    BitmapWrapper bs2 = changelogSetTable[i - 1][j].copy();
                    bs1.and(bs2);
                    changelogSetTable[i][j] = bs1;
                }
            }
        }
    }

    private class Slice {
        int startTime;
        BitmapWrapper changelogSet;
        List<AggregationQuery> activeQueries;

        // Per query: Map (key -> list of aggregates for query)
        List<Map<Integer, List<Aggregate>>> aggregates;
        // Per query: Map (key -> (eventTime, processingTime)
        List<Map<Integer, MutablePair<Long, Long>>> latestTupleTimes;

        int aggregateCount;

        public Slice(int startTime, BitmapWrapper changelogSet, List<AggregationQuery> activeQueries) {
            this.startTime = startTime;
            this.changelogSet = changelogSet;
            this.aggregates = new ArrayList<>();
            this.latestTupleTimes = new ArrayList<>();
            this.activeQueries = activeQueries;
            for (AggregationQuery query : activeQueries) {
                this.aggregates.add(new HashMap<>());
                this.latestTupleTimes.add(new HashMap<>());
            }
            this.aggregateCount = 0;
        }

        public long addTuple(Tuple tuple) {
            AtomicLong updates = new AtomicLong();
            BitmapWrapper bitSet = tuple.getQuerySet();
            bitSet.forEach((int i) -> {
                AggregationQuery query = activeQueries.get(i);

                Map<Integer, List<Aggregate>> aggregateByKey = aggregates.get(i);
                List<Aggregate> queryAggregates = aggregateByKey.get(tuple.getKey());
                if (queryAggregates == null) {
                    queryAggregates = new ArrayList<>();
                    for (AggregationOperation operation : query.aggregation.getOperations()) {
                        queryAggregates.add(operation.createAggregate());
                        aggregateCount ++;
                    }
                    aggregateByKey.put(tuple.getKey(), queryAggregates);
                }
                for (Aggregate aggregate : queryAggregates) {
                    aggregate.addValue(tuple.getFields()[query.aggregation.getColumn()]);
                    updates.getAndIncrement();
                }
                Map<Integer, MutablePair<Long, Long>> tupleTimesByKey = latestTupleTimes.get(i);
                MutablePair<Long, Long> tupleTimes = tupleTimesByKey.get(tuple.getKey());
                if (tupleTimes == null) {
                    tupleTimes = MutablePair.of(tuple.getTimestamp(), tuple.getProcessingTime());
                    tupleTimesByKey.put(tuple.getKey(), tupleTimes);
                } else {
                    tupleTimes.setLeft(tuple.getTimestamp());
                    tupleTimes.setRight(tuple.getProcessingTime());
                }
            });
            return updates.get();
        }

        public BitmapWrapper getChangelogSet() {
            return changelogSet;
        }

        public int getAggregateCount() {
            return aggregateCount;
        }
    }
}
