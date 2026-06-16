package org.shared.query.filter;

public class MoreOrEqual extends Filter {

    public MoreOrEqual(int column, int comparisonValue) {
        super(column, comparisonValue, Integer.MAX_VALUE);
    }

    @Override
    public String stringify() {
        return "." + column + " >= " + lowerBound;
    }
}
