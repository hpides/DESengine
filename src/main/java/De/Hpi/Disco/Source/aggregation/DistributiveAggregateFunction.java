package De.Hpi.Disco.Source.aggregation;

import de.tub.dima.scotty.core.windowFunction.AggregateFunction;

public interface DistributiveAggregateFunction<InputType> extends AggregateFunction<InputType, InputType, InputType> {}
