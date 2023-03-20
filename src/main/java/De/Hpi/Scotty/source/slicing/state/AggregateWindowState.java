package De.Hpi.Scotty.source.slicing.state;

import De.Hpi.Scotty.source.core.AggregateWindow;
import De.Hpi.Scotty.source.core.windowFunction.AggregateFunction;
import De.Hpi.Scotty.source.core.windowType.WindowMeasure;
import De.Hpi.Scotty.source.slicing.slice.Slice;
import De.Hpi.Scotty.source.state.StateFactory;

import java.util.List;
import java.util.Objects;

public class AggregateWindowState implements AggregateWindow {

    private final long start;
    private final long endTs;
    private final WindowMeasure measure;
    private final AggregateState windowState;

    public AggregateWindowState(long startTs, long endTs, WindowMeasure measure, StateFactory stateFactory, List<AggregateFunction> windowFunctionList) {
        this.start = startTs;
        this.endTs = endTs;
        this.windowState = new AggregateState(stateFactory, windowFunctionList);
        this.measure = measure;
    }

    public boolean containsSlice(Slice currentSlice) {
        if (measure == WindowMeasure.Time) {
            return this.getStart() <= currentSlice.getTStart() && (this.getEnd() > currentSlice.getTLast());
        } else {
            return this.getStart() <= currentSlice.getCStart() && (this.getEnd() >= currentSlice.getCLast());
        }
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return endTs;
    }

    @Override
    public List getAggValues() {
        return windowState.getValues();
    }

    @Override
    public boolean hasValue() {
        return windowState.hasValues();
    }

    public void addState(AggregateState aggregationState) {
        this.windowState.merge(aggregationState);
    }

    public WindowMeasure getMeasure() {
        return measure;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateWindowState that = (AggregateWindowState) o;
        return start == that.start &&
                endTs == that.endTs &&
                Objects.equals(windowState, that.windowState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, endTs, windowState);
    }

    @Override
    public String toString() {
        return "WindowResult(" +
                measure.toString() + ","+
                start + "-" + endTs +
                "," + windowState +
                ')';
    }
}
