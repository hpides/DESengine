package De.Hpi.Disco.Source.aggregation.functions;

import De.Hpi.Disco.Source.aggregation.DistributiveAggregateFunction;

public class MaxMinAggregateFunction implements DistributiveAggregateFunction<Long> {
    @Override
    public Long lift(Long inputTuple) {
        return 0 - inputTuple;
    }

    @Override
    public Long combine(Long partialAggregate1, Long partialAggregate2) {
        if (partialAggregate1 == null) return partialAggregate2;
        if (partialAggregate2 == null) return partialAggregate1;
        return Math.min(partialAggregate1, partialAggregate2);
    }

    @Override
    public Long lower(Long aggregate) {
        return Math.abs(aggregate);
    }
}
