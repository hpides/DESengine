package org.shared.query.aggregation;

import java.util.List;

public class Sum extends AggregationFunction {

    public Sum(int column) {
        super(column);
        operations = List.of(new SumOperation(column));
    }

    @Override
    public Object getFinalResult(List<Aggregate> aggregatesOfOperations) {
        return aggregatesOfOperations.get(0).getResult();
    }

    public static class SumOperation extends AggregationOperation {

        public SumOperation(int column) {
            super(column);
        }

        @Override
        public Aggregate createAggregate() {
            return new SumAggregate();
        }
    }

    private static class SumAggregate extends Aggregate {
        private int sum = 0;

        @Override
        public void addValue(int value) {
            super.addValue(value);
            sum += value;
        }

        @Override
        public void addAggregate(Aggregate aggregate) {
            super.addAggregate(aggregate);
            sum += ((SumAggregate) aggregate).sum;
        }

        @Override
        public Object getResult() {
            return sum;
        }

        @Override
        public Aggregate copy() {
            Aggregate agg = new SumAggregate();
            agg.addAggregate(this);
            return agg;
        }
    }
}

