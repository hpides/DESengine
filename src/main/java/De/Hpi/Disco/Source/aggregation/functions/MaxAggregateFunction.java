package De.Hpi.Disco.Source.aggregation.functions;

import De.Hpi.Disco.Source.aggregation.DistributiveAggregateFunction;

public class MaxAggregateFunction implements DistributiveAggregateFunction<Double> {
    @Override
    public Double lift(Double inputTuple) {
        return inputTuple;
    }

    @Override
    public Double combine(Double partialAggregate1, Double partialAggregate2) {
        if (partialAggregate1 == null) return partialAggregate2;
        if (partialAggregate2 == null) return partialAggregate1;
        return Math.max(partialAggregate1, partialAggregate2);
    }

    @Override
    public Double lower(Double aggregate) {
        return aggregate;
    }
}
