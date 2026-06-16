package org.shared.query.aggregation;

import java.util.List;

public class Min extends AggregationFunction {

    public Min(int column) {
        super(column);
        operations = List.of(new MinOperation(column));
    }

    @Override
    public Object getFinalResult(List<Aggregate> aggregatesOfOperations) {
        return aggregatesOfOperations.get(0).getResult();

    }

    public static class MinOperation extends AggregationOperation {
        public MinOperation(int column) {
            super(column);
        }

        @Override
        public Aggregate createAggregate() {
            return new MinAggregate();
        }

    }

    private static class MinAggregate extends Aggregate {
        private int min = Integer.MAX_VALUE;

        @Override
        public void addValue(int value) {
            super.addValue(value);
            min = Math.min(min, value);
        }

        @Override
        public void addAggregate(Aggregate aggregate) {
            super.addAggregate(aggregate);
            min = Math.min(min, ((MinAggregate) aggregate).min);
        }

        @Override
        public Object getResult() {
            return min;
        }

        @Override
        public Aggregate copy() {
            Aggregate agg = new MinAggregate();
            agg.addAggregate(this);
            return agg;
        }
    }
}
