package De.Hpi.Disco.Source.aggregation.functions;

import De.Hpi.Disco.Source.aggregation.HolisticAggregateFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MedianAggregateFunction implements HolisticAggregateFunction<Double, List<Double>, Double> {
    @Override
    public List<Double> lift(Double inputTuple) {
        return new ArrayList<>(Collections.singletonList(inputTuple));
    }

    @Override
    public List<Double> combine(List<Double> partialAggregate1, List<Double> partialAggregate2) {
        partialAggregate1.addAll(partialAggregate2);
        return partialAggregate1;
    }

    @Override
    public Double lower(List<Double> aggregate) {
        if (aggregate.isEmpty()) {
            return null;
        }

        aggregate.sort(Comparator.naturalOrder());
        return aggregate.get(aggregate.size() / 2);
    }
}
