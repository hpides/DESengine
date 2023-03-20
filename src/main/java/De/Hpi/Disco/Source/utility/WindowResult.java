package De.Hpi.Disco.Source.utility;

import De.Hpi.Disco.Source.aggregation.FunctionWindowAggregateId;

public class WindowResult {
    private final FunctionWindowAggregateId finalWindowId;
    private final Double value;

    public WindowResult(FunctionWindowAggregateId finalWindowId, Double value) {
        this.finalWindowId = finalWindowId;
        this.value = value;
    }

    public FunctionWindowAggregateId getFinalWindowId() {
        return finalWindowId;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WindowResult{" +
                "finalWindowId=" + finalWindowId +
                ", value=" + value +
                '}';
    }
}
