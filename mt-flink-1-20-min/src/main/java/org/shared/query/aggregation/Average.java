package org.shared.query.aggregation;

import java.util.List;

public class Average extends AggregationFunction {

    public Average(int column) {
        super(column);
        operations = List.of(new AverageOperation(column));
    }

    @Override
    public Object getFinalResult(List<Aggregate> aggregatesOfOperations) {
        return aggregatesOfOperations.get(0).getResult();
    }

    public static class AverageOperation extends AggregationOperation {
        public AverageOperation(int column) {
            super(column);
        }

        @Override
        public Aggregate createAggregate() {
            return new AverageAggregate();
        }
    }

    private static class AverageAggregate extends Aggregate {
        private int sum = 0;
        private int count = 0;

        @Override
        public void addValue(int value) {
            super.addValue(value);
            sum += value;
            count++;
        }

        @Override
        public void addAggregate(Aggregate aggregate) {
            super.addAggregate(aggregate);
            sum += ((AverageAggregate) aggregate).sum;
            count += ((AverageAggregate) aggregate).count;
        }

        @Override
        public Object getResult() {
            return (double) sum / (double) count;
        }

        @Override
        public Aggregate copy() {
            Aggregate agg = new AverageAggregate();
            agg.addAggregate(this);
            return agg;
        }

        @Override
        public String toString() {
            return "Average [sum=" + sum + ", count=" + count + "]";
        }
    }
}

