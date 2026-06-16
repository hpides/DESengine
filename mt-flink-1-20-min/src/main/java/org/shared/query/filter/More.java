package org.shared.query.filter;

public class More extends Filter {

    public More(int column, int comparisonValue) {
        super(column, comparisonValue + 1, Integer.MAX_VALUE);
    }


    @Override
    public String stringify() {
        return "." + column + " > " + (lowerBound - 1);
    }
}
