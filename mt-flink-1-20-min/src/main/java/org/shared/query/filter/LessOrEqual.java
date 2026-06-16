package org.shared.query.filter;

public class LessOrEqual extends Filter {

    public LessOrEqual(int column, int comparisonValue) {
        super(column, Integer.MIN_VALUE, comparisonValue);
    }

    @Override
    public String stringify() {
        return "." + column + " <= " + upperBound;
    }
}
