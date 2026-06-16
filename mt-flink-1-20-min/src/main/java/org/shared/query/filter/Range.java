package org.shared.query.filter;

public class Range extends Filter {

    public Range(int column, int lowerBound, int upperBound) {
        super(column, lowerBound, upperBound);
    }

    @Override
    public String stringify() {
        return lowerBound + " <= ." + column + " <= " + upperBound;
    }


}
