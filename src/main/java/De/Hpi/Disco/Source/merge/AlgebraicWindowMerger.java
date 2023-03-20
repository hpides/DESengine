package De.Hpi.Disco.Source.merge;

import De.Hpi.Disco.Source.aggregation.AlgebraicPartial;
import de.tub.dima.scotty.core.AggregateWindow;
import de.tub.dima.scotty.core.windowFunction.AggregateFunction;
import de.tub.dima.scotty.core.windowType.Window;

import java.util.List;

public class AlgebraicWindowMerger<AggType extends AlgebraicPartial> extends DistributiveWindowMerger<AggType> {

    public AlgebraicWindowMerger(int numChildren, List<Window> windows, List<AggregateFunction> aggFunctions) {
        super(numChildren, windows, aggFunctions);
    }

    @Override
    public Double lowerFinalValue(AggregateWindow finalWindow) {
        List aggValues = finalWindow.getAggValues();
        if (aggValues.isEmpty()) {
            return null;
        }

        AlgebraicPartial partial = (AlgebraicPartial) aggValues.get(0);
        return (Double) partial.lower();
    }
}
