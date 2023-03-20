package De.Hpi.Disco.Source.merge;

import De.Hpi.Disco.Source.aggregation.DistributedAggregateWindowState;
import De.Hpi.Disco.Source.aggregation.FunctionWindowAggregateId;
import de.tub.dima.scotty.core.AggregateWindow;
import de.tub.dima.scotty.core.windowFunction.AggregateFunction;
import de.tub.dima.scotty.core.windowType.Window;
import de.tub.dima.scotty.slicing.state.AggregateState;

import java.util.*;

public class DistributiveWindowMerger<AggType> extends BaseWindowMerger<AggType> {
    public DistributiveWindowMerger(int numChildren, List<Window> windows, List<AggregateFunction> aggFunctions) {
        super(numChildren, windows, aggFunctions);
    }

    @Override
    public void processPreAggregate(AggType preAggregate, FunctionWindowAggregateId functionWindowAggId) {
        if (this.isSessionWindow(functionWindowAggId)) {
            processGlobalSession(preAggregate, functionWindowAggId);
            return;
        }

        FunctionWindowAggregateId keylessId = functionWindowAggId.keylessCopy();
        AggregateFunction aggFn = this.aggFunctions.get(functionWindowAggId.getFunctionId());
        List<AggregateFunction> stateAggFns = Collections.singletonList(aggFn);
        Map<Integer, List<DistributedAggregateWindowState<AggType>>> keyedStates =
                windowAggregates.computeIfAbsent(keylessId, id -> new HashMap<>());

        int key = functionWindowAggId.getKey();
        List<DistributedAggregateWindowState<AggType>> aggWindows =
                keyedStates.computeIfAbsent(key, k -> new ArrayList<>());

        if (aggWindows.isEmpty()) {
            aggWindows.add(new DistributedAggregateWindowState<>(keylessId,
                                new AggregateState<>(this.stateFactory, stateAggFns)));
        }

        DistributedAggregateWindowState<AggType> aggWindow = aggWindows.get(0);
        AggregateState<AggType> state = aggWindow.getWindowState();
        state.addElement(preAggregate);
    }

    @Override
    public Double lowerFinalValue(AggregateWindow finalWindow) {
        List aggValues = finalWindow.getAggValues();
        return aggValues.isEmpty() ? null : (Double) aggValues.get(0);
    }
}
