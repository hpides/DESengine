package De.Hpi.Disco.Source.aggregation.functions;

import De.Hpi.Disco.Source.aggregation.AlgebraicAggregateFunction;

public class MaxAverageAggregateFunction implements AlgebraicAggregateFunction<Double, MaxPartialAverage> {
    @Override
    public MaxPartialAverage lift(Double inputTuple) {
        return new MaxPartialAverage(inputTuple, 1);
    }

    @Override
    public MaxPartialAverage combine(MaxPartialAverage partialAggregate1, MaxPartialAverage partialAggregate2) {
        return partialAggregate1.merge(partialAggregate2);
    }

    @Override
    public MaxPartialAverage lower(MaxPartialAverage aggregate) {
        return aggregate;
    }

    @Override
    public MaxPartialAverage partialFromString(String partialString) {
        return new MaxPartialAverage(0, 0).fromString(partialString);
    }
}
