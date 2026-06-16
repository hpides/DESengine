package org.shared.query.aggregation;

import java.util.List;

public class AverageReusingSumAndCount extends AggregationFunction {

    public AverageReusingSumAndCount(int column) {
        super(column);
        operations = List.of(new Sum.SumOperation(column), new Count.CountOperation(column));
    }

    @Override
    public Object getFinalResult(List<Aggregate> aggregatesOfOperations) {
        int sum = (int) aggregatesOfOperations.get(0).getResult();
        int count = (int) aggregatesOfOperations.get(1).getResult();
        return (double) sum / (double) count;
    }

}

