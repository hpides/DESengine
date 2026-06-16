package org.shared.operators;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.flink.util.Collector;
import org.shared.datagenerator.Tuple;
import org.shared.metrics.MetricTuple;
import org.shared.query.AggregationQuery;
import org.shared.query.QueryChangelog;
import org.shared.query.aggregation.Aggregate;
import org.shared.query.aggregation.AggregationOperation;
import org.shared.query.filter.Filter;
import org.shared.result.QueryResult;
import org.shared.util.BitmapUncompressed;
import org.shared.util.BitmapWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class OptimizedAggregationOperator extends MultiQueryAggregationOperator implements Serializable {
    private static Logger log = LoggerFactory.getLogger(OptimizedAggregationOperator.class);

    private final int columns;
    List<Slice> slices;
    BitmapWrapper[][] changelogSetTable;
    List<AggregationQuery> activeQueries;

    public OptimizedAggregationOperator(int columns) {
        this.columns = columns;
        this.slices = new ArrayList<>();
    }

    @Override
    public void processWindowStarts(int time, List<AggregationQuery> activeQueries, List<Integer> startingWindowIndices, QueryChangelog changelog) {
        if (!startingWindowIndices.isEmpty() || changelog != null) {
            startNewSlice(time, changelog, activeQueries);
        }
    }

    @Override
    public long processTuple(Tuple tuple, List<AggregationQuery> activeQueries) {
        return currentSlice().addTuple(tuple);
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

        Slice slice = new Slice(columns, time, activeQueries, changelogSet);
        slices.add(slice);

        discardOutdatedSlices(time, activeQueries);
        updateChangelogSetTable();
    }

    private long emitQuery(AggregationQuery query, int queryIndex, int emissionTime, long windowEndTimestamp, long windowEndProcessingTime, MetricTuple metrics ,Collector<QueryResult> out) {
        log.debug("emit Q{}: {}", queryIndex, query);

        long updates = 0;
        List<Map<Integer, Aggregate>> keyAggregateMaps = new ArrayList<>();
        for (AggregationOperation operation : query.aggregation.getOperations()) {
            keyAggregateMaps.add(new HashMap<>());
        }
        Map<Integer, MutablePair<Long, Long>> tupleTimes = new HashMap<>();

        int windowStartTime = emissionTime - (query.window.getLength() - 1);
        for (int i = slices.size() - 1; i >= 0 && slices.get(i).startTime >= windowStartTime; i--) {
            Slice slice = slices.get(i);
            if (!changelogSetTable[slices.size() - 1][i].get(queryIndex)) {
                break;
            }
            updates += slice.computeAggregateForQuery(query, keyAggregateMaps, tupleTimes);
        }
        List<Aggregate> aggregatesOfOperations = new ArrayList<>();
        for (int key : keyAggregateMaps.get(0).keySet()) {
            aggregatesOfOperations.clear();
            for (Map<Integer, Aggregate> keyAggregateMap : keyAggregateMaps) {
                aggregatesOfOperations.add(keyAggregateMap.get(key));
            }
            Object aggregatedValue = query.aggregation.getFinalResult(aggregatesOfOperations);
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
                    aggregatedValue);
            //log.debug(result.toString());
            out.collect(result);
            metrics.emissionsCount++;
            metrics.sumEventTimeLatencies += eventTimeLatency;
            metrics.sumProcessingTimeLatencies += processingTimeLatency;
        }
        return updates;
    }

    private BitmapWrapper extractOrCreateChangelogSet(QueryChangelog queryChangelog, int activeQueriesSize) {
        BitmapWrapper changelogSet = queryChangelog.getChangelogSet();
        if (changelogSet == null) {
            changelogSet = new BitmapUncompressed(activeQueriesSize);
            changelogSet.set(0, activeQueriesSize);
        }
        return changelogSet;
    }

    private Slice currentSlice() {
        return slices.get(slices.size() - 1);
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
                    BitmapWrapper bs = new BitmapUncompressed(slices.get(j).queriesListCount);
                    bs.set(0, slices.get(j).queriesListCount);
                    changelogSetTable[i][j] = bs;
                } else if (i == j + 1) {
                    changelogSetTable[i][j] = slices.get(i).changelogSet;
                } else {
                    BitmapWrapper bs1 = changelogSetTable[i][j + 1].copy();
                    BitmapWrapper bs2 = changelogSetTable[i - 1][j].copy();
                    bs1.and(bs2);
                    changelogSetTable[i][j] = bs1;
                }
            }
        }
    }

    private class Slice implements Serializable {
        private final int startTime;
        private final int queriesListCount;
        BitmapWrapper changelogSet;

        private List<FilterColumnAggregate> columnAggregates;

        public Slice(int columnCount, int startTime, List<AggregationQuery> queries, BitmapWrapper changelogSet) {
            this.startTime = startTime;
            this.queriesListCount = queries.size();
            this.changelogSet = changelogSet;

            columnAggregates = constructFilterColumns(queries, columnCount);
        }

        public long addTuple(Tuple tuple) {
            long updates = 0;
            for (FilterColumnAggregate columnAggregate : columnAggregates) {
                updates += columnAggregate.addTuple(tuple);
            }
            return updates;
        }

        public long computeAggregateForQuery(AggregationQuery query, List<Map<Integer, Aggregate>> output, Map<Integer,MutablePair<Long, Long>> tupleTimesOutput) {
            long updates = 0;
            Filter filter = query.filter;
            for (int i = 0; i < query.aggregation.getOperations().size(); i++) {
                AggregationOperation operation = query.aggregation.getOperations().get(i);
                Map<Integer, Aggregate> keyAggregateMap = output.get(i);

                updates += columnAggregates.get(filter.getColumn()).aggregateRange(
                        filter.lowerBound(),
                        filter.upperBound(),
                        operation,
                        keyAggregateMap,
                        tupleTimesOutput
                );
            }

            return updates;
        }

        public int getAggregateCount() {
            int count = 0;
            for (FilterColumnAggregate columnAggregate : columnAggregates) {
                count += columnAggregate.getAggregateCount();
            }
            return count;
        }

        private List<FilterColumnAggregate> constructFilterColumns(List<AggregationQuery> queries, int columnCount) {
            class StartOrStopEvent {
                public final long value;
                public final int column;
                public final boolean isStart;
                public final AggregationOperation aggregation;
                public StartOrStopEvent(long value, int column, boolean isStart, AggregationOperation aggregation) {
                    this.value = value;
                    this.column = column;
                    this.isStart = isStart;
                    this.aggregation = aggregation;
                }
            }

            // Collect sorted starts and stops for query filter segments
            List<StartOrStopEvent> events = new ArrayList<>();
            for (AggregationQuery query : queries) {
                for (AggregationOperation operation : query.aggregation.getOperations()) {
                    events.add(new StartOrStopEvent(
                            query.filter.lowerBound(), query.filter.getColumn(), true, operation));
                    events.add(new StartOrStopEvent(
                            (long) query.filter.upperBound() + 1L, query.filter.getColumn(), false, operation));
                }
            }
            // Sort by value, with starts coming before stops
            events.sort(Comparator.comparingLong(event -> ((StartOrStopEvent) event).value)
                    .thenComparing(event -> ((StartOrStopEvent) event).isStart, Comparator.reverseOrder()));

            // Datastructures to keep track of current segment state for each column
            List<Map<AggregationOperation, Integer>> aggregationCountsByColumn = new ArrayList<>();
            List<Long> lowerBoundsByColumn = new ArrayList<>();
            List<List<SegmentAggregate>> segmentListsByColumn = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                aggregationCountsByColumn.add(new HashMap<>());
                lowerBoundsByColumn.add((long)Integer.MIN_VALUE);
                segmentListsByColumn.add(new ArrayList<>());
            }

            // Iterate over events and construct segment aggregates
            for (StartOrStopEvent event : events) {
                int column = event.column;
                Map<AggregationOperation, Integer> aggregationCounts = aggregationCountsByColumn.get(column);

                // If this event is after the lower bound of the current segment,
                // we emit the current segment and start a new one
                if (event.value > lowerBoundsByColumn.get(column)) {
                    SegmentAggregate segmentAggregate = new SegmentAggregate(
                            (int) lowerBoundsByColumn.get(column).longValue(),
                            (int) (event.value - 1L),
                            aggregationCounts.keySet()
                    );
                    segmentListsByColumn.get(column).add(segmentAggregate);
                    lowerBoundsByColumn.set(column, event.value);
                }
                if (event.isStart) {
                    aggregationCounts.putIfAbsent(event.aggregation, 0);
                    aggregationCounts.put(event.aggregation, aggregationCounts.get(event.aggregation) + 1);
                } else {
                    aggregationCounts.put(event.aggregation, aggregationCounts.get(event.aggregation) - 1);
                    if (aggregationCounts.get(event.aggregation) == 0) {
                        aggregationCounts.remove(event.aggregation);
                    }
                }
            }

            // Add last segment until maximum integer value
            for (int column = 0; column < columnCount; column++) {
                if (lowerBoundsByColumn.get(column) < Integer.MAX_VALUE + 1L) {
                    SegmentAggregate segmentAggregate = new SegmentAggregate(
                            (int) lowerBoundsByColumn.get(column).longValue(),
                            Integer.MAX_VALUE,
                            aggregationCountsByColumn.get(column).keySet()
                    );
                    segmentListsByColumn.get(column).add(segmentAggregate);
                }
            }

            List<FilterColumnAggregate> columnAggregates = new ArrayList<>(columnCount);
            for (int i = 0; i < columnCount; i++) {
                columnAggregates.add(new FilterColumnAggregate(i, segmentListsByColumn.get(i)));
            }
            return columnAggregates;
        }
    }

    private static class FilterColumnAggregate implements Serializable {
        List<SegmentAggregate> segments;
        int columnIndex;
        AggregateSegmentTree segTree;
        int aggregateCount;

        public FilterColumnAggregate(int columnIndex, List<SegmentAggregate> sortedSegments) {
            this.segments = sortedSegments;
            this.columnIndex = columnIndex;
            this.segTree = null;
            this.aggregateCount = 0;
        }

        public long aggregateRange(int lowerBound, int upperBound, AggregationOperation aggregation, Map<Integer, Aggregate> output, Map<Integer, MutablePair<Long, Long>> tupleTimesOutput) {
            long updates = 0;
            if (segTree == null) {
                segTree = new AggregateSegmentTree(segments);
                updates += segTree.getUpdatesDuringCreation();
            }
            updates += segTree.aggregateRange(lowerBound, upperBound, aggregation, output, tupleTimesOutput);
            return updates;
        }

        public int getAggregateCount() {
            if (segTree == null) {
                return aggregateCount;
            }
            return segTree.getAggregateCount();
        }

        public SegmentAggregate segmentContaining(int value) {
            int index = binarySearchIndexOfSegmentContaining(value);
            SegmentAggregate segment = segments.get(index);
            assert segment.lowerBound <= value && value <= segment.upperBound;
            return segment;
        }

        private int binarySearchIndexOfSegmentContaining(int value) {
            int l = 0, r = segments.size();
            while (r > l + 1) {
                int m = (l + r) / 2;
                SegmentAggregate segment = segments.get(m);
                if (segment.upperBound < value) {
                    l = m + 1;
                } else if (segment.lowerBound > value) {
                    r = m;
                } else {
                    l = m;
                    r = m + 1;
                }
            }
            return l;
        }

        public long addTuple(Tuple tuple) {
            assert segTree == null;

            SegmentAggregate segment = segmentContaining(tuple.getFields()[columnIndex]);
            aggregateCount -= segment.getAggregateCount();
            long updates = segment.addTuple(tuple);
            aggregateCount += segment.getAggregateCount();
            return updates;
        }
    }

    /***
     * Contains aggregates for a single value segment of a single filter column.
     */
    private static class SegmentAggregate implements Serializable {
        private final int lowerBound;
        private final int upperBound;
        // aggregation (definition) -> (key -> aggregate)
        private final Map<AggregationOperation, Map<Integer, Aggregate>> aggregateMap;
        // key -> (latestEventTime, latestProcessingTime)
        private final Map<Integer, MutablePair<Long, Long>> latestTupleTimes;
        private int aggregateCount;

        // for tracking metrics;
        private long updatesDuringCreation = 0;

        public SegmentAggregate(int lowerBound, int upperBound, Collection<AggregationOperation> aggregations) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            aggregateMap = new HashMap<>();
            latestTupleTimes = new HashMap<>();
            for (AggregationOperation aggregation : aggregations) {
                aggregateMap.put(aggregation, new HashMap<>());
            }
            aggregateCount = 0;
        }

        // Create a SegmentAggregate representing two neighbouring Aggregates (used in segment tree)
        public SegmentAggregate(SegmentAggregate left, SegmentAggregate right) {
            if (left == null) {
                this.lowerBound = right.lowerBound;
                this.upperBound = right.upperBound;
                aggregateMap = new HashMap<>();
                latestTupleTimes = new HashMap<>();
            } else if (right == null) {
                this.lowerBound = left.lowerBound;
                this.upperBound = left.upperBound;
                aggregateMap = new HashMap<>();
                latestTupleTimes = new HashMap<>();
            } else {
                this.lowerBound = left.lowerBound;
                this.upperBound = right.upperBound;
                aggregateMap = new HashMap<>();
                latestTupleTimes = new HashMap<>();

                left.aggregateMap.forEach((operation, leftKeyAggregateMap) -> {
                    Map<Integer, Aggregate> rightKeyAggregateMap = right.aggregateMap.get(operation);
                    if (rightKeyAggregateMap != null) {
                        Map<Integer, Aggregate> targetMap = aggregateMap.computeIfAbsent(operation, k -> new HashMap<>());
                        leftKeyAggregateMap.forEach((key, aggregate) -> {
                            targetMap.compute(key, (k, prev) -> {
                                updatesDuringCreation++;
                                if (prev == null) {
                                    return aggregate.copy();
                                } else {
                                    prev.addAggregate(aggregate);
                                    return prev;
                                }
                            });
                        });
                        rightKeyAggregateMap.forEach((key, aggregate) -> {
                            targetMap.compute(key, (k, prev) -> {
                                updatesDuringCreation++;
                                if (prev == null) {
                                    return aggregate.copy();
                                } else {
                                    prev.addAggregate(aggregate);
                                    return prev;
                                }
                            });
                        });
                    }
                });

                //left.aggregateMap.forEach((operation, keyAggregateMap) -> {
                //    Map<Integer, Aggregate> targetMap = aggregateMap.computeIfAbsent(operation, k -> new HashMap<>());
                //    keyAggregateMap.forEach((key, aggregate) -> {
                //        targetMap.compute(key, (k, prev) -> {
                //            updatesDuringCreation++;
                //            if (prev == null) {
                //                return aggregate.copy();
                //            } else {
                //                prev.addAggregate(aggregate);
                //                return prev;
                //            }
                //        });
                //    });
                //});
                left.latestTupleTimes.forEach((key, tupleTimes) -> {
                    long eventTime = tupleTimes.getLeft();
                    long processingTime = tupleTimes.getRight();
                    latestTupleTimes.merge(key, MutablePair.of(eventTime, processingTime), (prev, added) ->
                            MutablePair.of(Math.max(prev.getLeft(), added.getLeft()), Math.max(prev.getRight(), added.getRight())));
                });
                //right.aggregateMap.forEach((operation, keyAggregateMap) -> {
                //    Map<Integer, Aggregate> targetMap = aggregateMap.computeIfAbsent(operation, k -> new HashMap<>());
                //    keyAggregateMap.forEach((key, aggregate) -> {
                //        targetMap.compute(key, (k, prev) -> {
                //            updatesDuringCreation++;
                //            if (prev == null) {
                //                return aggregate.copy();
                //            } else {
                //                prev.addAggregate(aggregate);
                //                return prev;
                //            }
                //        });
                //    });
                //});
                right.latestTupleTimes.forEach((key, tupleTimes) -> {
                    long eventTime = tupleTimes.getLeft();
                    long processingTime = tupleTimes.getRight();
                    latestTupleTimes.merge(key, MutablePair.of(eventTime, processingTime), (prev, added) ->
                            MutablePair.of(Math.max(prev.getLeft(), added.getLeft()), Math.max(prev.getRight(), added.getRight())));
                });
                aggregateMap.values().forEach((keyAggreagetMap) -> {
                    aggregateCount += keyAggreagetMap.size();
                });

            }
        }


        public long addTuple(Tuple tuple) {
            aggregateMap.forEach((aggregation, keyAggregateMap) -> {
                Aggregate aggregate = keyAggregateMap.get(tuple.getKey());
                if (aggregate == null) {
                    aggregate = aggregation.createAggregate();
                    keyAggregateMap.put(tuple.getKey(), aggregate);
                    aggregateCount++;
                }
                aggregate.addValue(tuple.getFields()[aggregation.getColumn()]);
            });
            MutablePair<Long, Long> tupleTimes = latestTupleTimes.get(tuple.getKey());
            if (tupleTimes == null) {
                tupleTimes = MutablePair.of(tuple.getTimestamp(), tuple.getProcessingTime());
                latestTupleTimes.put(tuple.getKey(), tupleTimes);
            } else {
                tupleTimes.setLeft(tuple.getTimestamp());
                tupleTimes.setRight(tuple.getProcessingTime());
            }
            return aggregateMap.size();
        }

        public int getLowerBound() {
            return lowerBound;
        }

        public int getUpperBound() {
            return upperBound;
        }

        public int getAggregateCount() {
            return aggregateCount;
        }

        public long getUpdatesDuringCreation() { return updatesDuringCreation; }
    }

    private static class AggregateSegmentTree implements Serializable {
        SegmentAggregate tree[];
        int size; // number of original segments
        int powerOfTwo; // next power of two
        Map<Integer, Integer> lowerBoundToIndex = new HashMap<>();
        Map<Integer, Integer> upperBoundToIndex = new HashMap<>();
        private int aggregateCount = 0;
        private long udpatesDuringCreation = 0;

        public AggregateSegmentTree(List<SegmentAggregate> segments) {
            size = segments.size();
            powerOfTwo = segments.size() <= 1 ? 1 : Integer.highestOneBit(size - 1) << 1;

            tree = new SegmentAggregate[powerOfTwo * 2];
            build(segments);
        }

        public long aggregateRange(int lowerBound, int upperBound, AggregationOperation aggregation, Map<Integer, Aggregate> output, Map<Integer, MutablePair<Long, Long>> tupleTimesOutput) {
            long updates = 0;
            int i = lowerBoundToIndex.get(lowerBound) + powerOfTwo;
            int j = upperBoundToIndex.get(upperBound) + powerOfTwo;

            while (i <= j) {
                if ((i & 1) == 1) {  // i is a right‑child
                    updates += mergeNodeIntoResult(i, aggregation, output, tupleTimesOutput);
                    i++;
                }
                if ((j & 1) == 0) {  // j is a left‑child
                    updates += mergeNodeIntoResult(j, aggregation, output, tupleTimesOutput);
                    j--;
                }
                i >>= 1;
                j >>= 1;
            }

            return updates;
        }

        private long mergeNodeIntoResult(int nodeIndex, AggregationOperation aggregation, Map<Integer, Aggregate> output, Map<Integer, MutablePair<Long, Long>> tupleTimesOutput) {
            AtomicLong updates = new AtomicLong();
            if (tree[nodeIndex] == null) {
                return updates.get();
            }
            Map<Integer, Aggregate> keyAggregateMap = tree[nodeIndex].aggregateMap.get(aggregation);
            keyAggregateMap.forEach((key, aggregate) -> {
                Aggregate resultAggregate = output.get(key);
                if (resultAggregate == null) {
                    resultAggregate = aggregation.createAggregate();
                    output.put(key, resultAggregate);
                }
                resultAggregate.addAggregate(aggregate);
                updates.getAndIncrement();
            });
            tree[nodeIndex].latestTupleTimes.forEach((key, tupleTimes) -> {
                MutablePair<Long, Long> resultTupleTimes = tupleTimesOutput.get(key);
                if (resultTupleTimes == null) {
                    resultTupleTimes = MutablePair.of(tupleTimes.getLeft(), tupleTimes.getRight());
                    tupleTimesOutput.put(key, resultTupleTimes);
                } else {
                    resultTupleTimes.left = Math.max(resultTupleTimes.getLeft(), tupleTimes.getLeft());
                    resultTupleTimes.right = Math.max(resultTupleTimes.getRight(), tupleTimes.getRight());
                }
            });
            return updates.get();
        }


        private void build(List<SegmentAggregate> segments) {
            // Fill non empty leaves: powerOfTwo ... powerOfTwo + size - 1
            for (int i = 0; i < size; i++) {
                tree[powerOfTwo + i] = segments.get(i);
                lowerBoundToIndex.put(segments.get(i).getLowerBound(), i);
                upperBoundToIndex.put(segments.get(i).getUpperBound(), i);
                aggregateCount += segments.get(i).getAggregateCount();
            }
            // Create empty leaves: powerOfTwo + size ... 2 * powerOfTwo - 1
            for (int i = powerOfTwo + size; i < 2 * powerOfTwo; i++) {
                tree[i] = null;
            }

            // Build internal nodes bottom up
            for (int node = powerOfTwo - 1; node >= 1; node--) {
                SegmentAggregate left = tree[node << 1];
                SegmentAggregate right = tree[node << 1 | 1];
                if (left == null && right == null) {
                    tree[node] = null;
                } else {
                    tree[node] = new SegmentAggregate(left, right);
                    udpatesDuringCreation += tree[node].getUpdatesDuringCreation();
                    aggregateCount += tree[node].getAggregateCount();
                }
            }
        }

        public int getAggregateCount() {
            return aggregateCount;
        }

        public long getUpdatesDuringCreation() { return udpatesDuringCreation; }
    }
}
