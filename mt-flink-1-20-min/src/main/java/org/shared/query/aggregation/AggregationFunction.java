package org.shared.query.aggregation;

import java.io.Serializable;
import java.util.List;

/**
 * Describes an aggregation function. Uses one or more aggregate definitions.
 */

public abstract class AggregationFunction implements Serializable {
    List<AggregationOperation> operations;
    int column;

    public AggregationFunction(int column) {
        this.column = column;
    }

    public int getColumn() { return column; }

    public List<AggregationOperation> getOperations() {
        return operations;
    }

    public abstract Object getFinalResult(List<Aggregate> aggregatesOfOperations);
}
