package org.shared.query;

import org.shared.query.aggregation.AggregationFunction;
import org.shared.query.aggregation.AggregationOperation;
import org.shared.query.filter.Filter;

import java.io.Serializable;

public class AggregationQuery implements Serializable {
    public Window window;
    public Filter filter;
    public AggregationFunction aggregation;

    public AggregationQuery(Window window, Filter filter, AggregationFunction aggregation) {
        this.window = window;
        this.filter = filter;
        this.aggregation = aggregation;
    }

    @Override
    public String toString() {
        return window + ", " + filter + ", " + aggregation;
    }
}
