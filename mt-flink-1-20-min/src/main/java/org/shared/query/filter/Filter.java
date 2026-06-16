package org.shared.query.filter;

import org.shared.datagenerator.Tuple;

import java.io.Serializable;

public abstract class Filter implements Serializable {
    protected int column;
    protected int lowerBound;
    protected int upperBound;

    public Filter(int column, int lowerBound, int upperBound) {
        this.column = column;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public int lowerBound() {
        return lowerBound;
    }

    public int upperBound() {
        return upperBound;
    }

    public boolean test(Tuple tuple) {
        return tuple.getFields()[column] >= lowerBound && tuple.getFields()[column] <= upperBound;
    };

    public int getColumn() {
        return column;
    }

    public abstract String stringify();

    @Override
    public String toString() {
        return "filter(" + stringify() + ")";
    }

}
