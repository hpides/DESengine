package org.shared.query.aggregation;

import java.io.Serializable;

public abstract class AggregationOperation implements Serializable {
    protected int column;

    public AggregationOperation(int column) {
        this.column = column;
    }

    public abstract Aggregate createAggregate();

    public int getColumn() {
        return column;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregationOperation)) return false;
        AggregationOperation that = (AggregationOperation) o;
        return column == that.column && this.getClass() == that.getClass();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(column) * 31 + this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(." + column + ")";
    }
}
