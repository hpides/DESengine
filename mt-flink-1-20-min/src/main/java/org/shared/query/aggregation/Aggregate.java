package org.shared.query.aggregation;

import java.io.Serializable;

public abstract class Aggregate implements Serializable {
    private static int updateCounter;
    public void addValue(int value) {
        updateCounter++;
    }
    public void addAggregate(Aggregate aggregate) {
        updateCounter++;
    }
    public abstract Object getResult();
    public abstract Aggregate copy();

    public static void resetUpdateCounter() {
        updateCounter = 0;
    }

    public static int getUpdateCounter() {
        return updateCounter;
    }
}
