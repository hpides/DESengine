package org.shared.query.filter;

public class Equal extends Filter {

    public Equal(int column, int comparisonValue) {
        super(column, comparisonValue, comparisonValue);
    }

    @Override
    public String stringify() {
        return "." + column + " == " + lowerBound;
    }


}
