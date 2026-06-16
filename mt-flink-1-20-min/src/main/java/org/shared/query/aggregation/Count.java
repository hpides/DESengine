package org.shared.query.aggregation;

import java.util.List;

public class Count extends AggregationFunction{

    public Count(int column) {
        super(column);
        operations = List.of(new CountOperation(column));
    }

    @Override
    public Object getFinalResult(List<Aggregate> aggregatesOfOperations) {
        return aggregatesOfOperations.get(0).getResult();
    }

    public static class CountOperation extends AggregationOperation {
        public CountOperation(int column) {
            super(column);
        }

        @Override
        public Aggregate createAggregate() {
            return new CountAggregate();
        }
    }

    private static class CountAggregate extends Aggregate {
        private int count = 0;

        @Override
        public void addValue(int value) {
            super.addValue(value);
            count++;
        }

        @Override
        public void addAggregate(Aggregate aggregate) {
            super.addAggregate(aggregate);
            count += ((CountAggregate) aggregate).count;
        }

        @Override
        public Object getResult() {
            return count;
        }

        @Override
        public Aggregate copy() {
            Aggregate agg = new CountAggregate();
            agg.addAggregate(this);
            return agg;
        }
    }
}
