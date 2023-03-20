package De.Hpi.Disco.Source.aggregation.functions;

import De.Hpi.Disco.Source.aggregation.AlgebraicAggregateFunction;

public class AverageAggregateFunction implements AlgebraicAggregateFunction<Double, PartialAverage> {
    @Override
    public PartialAverage lift(Double inputTuple) {
        return new PartialAverage(inputTuple, 1);
    }

    @Override
    public PartialAverage combine(PartialAverage partialAggregate1, PartialAverage partialAggregate2) {
        return partialAggregate1.merge(partialAggregate2);
    }

    @Override
    public PartialAverage lower(PartialAverage aggregate) {
        return aggregate;
    }

    @Override
    public PartialAverage partialFromString(String partialString) {
        return new PartialAverage(0, 0).fromString(partialString);
    }
}
