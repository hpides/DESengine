package org.shared.query.filter;

public class Less extends Filter {

    public Less(int column, int comparisonValue) {
        super(column, Integer.MIN_VALUE, comparisonValue - 1);
    }

    @Override
    public String stringify() {
        return "." + column + " < " + (upperBound + 1);
    }
}
