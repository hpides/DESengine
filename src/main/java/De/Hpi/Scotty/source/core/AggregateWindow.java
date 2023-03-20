package De.Hpi.Scotty.source.core;

import De.Hpi.Scotty.source.core.windowType.WindowMeasure;

import java.io.Serializable;
import java.util.List;

public interface AggregateWindow<T> extends Serializable {


    public WindowMeasure getMeasure();

    public long getStart();

    public long getEnd();

    public List<T> getAggValues();

    public boolean hasValue();

}
