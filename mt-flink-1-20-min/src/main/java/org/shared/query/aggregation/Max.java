package org.shared.query.aggregation;

import java.util.List;

public class Max extends AggregationFunction {

    public Max(int column) {
        super(column);
        operations = List.of(new MaxOperation(column));
    }

    @Override
    public Object getFinalResult(List<Aggregate> aggregatesOfOperations) {
        return aggregatesOfOperations.get(0).getResult();
    }

    public static class MaxOperation extends AggregationOperation {
        public MaxOperation(int column) {
            super(column);
        }

        @Override
        public Aggregate createAggregate() {
            return new MaxAggregate();
        }
    }

    private static class MaxAggregate extends Aggregate {
        private int max = Integer.MIN_VALUE;

        @Override
        public void addValue(int value) {
            super.addValue(value);
            max = Math.max(max, value);
        }

        @Override
        public void addAggregate(Aggregate aggregate) {
            super.addAggregate(aggregate);
            max = Math.max(max, ((MaxAggregate) aggregate).max);
        }

        @Override
        public Object getResult() {
            return max;
        }

        @Override
        public Aggregate copy() {
            Aggregate agg = new MaxAggregate();
            agg.addAggregate(this);
            return agg;
        }
    }
}
