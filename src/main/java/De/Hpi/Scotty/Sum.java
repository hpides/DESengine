package De.Hpi.Scotty;


import De.Hpi.Scotty.source.core.windowFunction.InvertibleReduceAggregateFunction;

public class Sum implements InvertibleReduceAggregateFunction<Double> {
    @Override
    public Double invert(Double currentAggregate, Double toRemove) {
        return currentAggregate - toRemove;
    }

    @Override
    public Double combine(Double partialAggregate1, Double partialAggregate2) {
        return partialAggregate1 + partialAggregate2;
    }
}
