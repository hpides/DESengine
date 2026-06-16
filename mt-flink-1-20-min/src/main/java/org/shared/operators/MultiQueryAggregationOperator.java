package org.shared.operators;

import org.apache.flink.util.Collector;
import org.shared.datagenerator.Tuple;
import org.shared.metrics.MetricTuple;
import org.shared.query.AggregationQuery;
import org.shared.query.QueryChangelog;
import org.shared.query.aggregation.Aggregate;
import org.shared.result.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public abstract class MultiQueryAggregationOperator implements Serializable {

    public abstract void processWindowStarts(
            int time,
            List<AggregationQuery> activeQueries,
            List<Integer> startingWindowIndices,
            QueryChangelog changelog
    );

    public abstract long processTuple(Tuple tuple, List<AggregationQuery> activeQueries);

    public abstract long emitQueries(
            int time,
            List<AggregationQuery> activeQueries,
            List<Integer> emittingQueryIndices,
            long windowEndTimestamp,
            long windowEndProcessingTime,
            MetricTuple metrics,
            Collector<QueryResult> out
    );

    public abstract int countExistingAggregates();
}
