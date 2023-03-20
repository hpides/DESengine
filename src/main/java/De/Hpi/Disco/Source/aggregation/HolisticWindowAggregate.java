package De.Hpi.Disco.Source.aggregation;

import De.Hpi.Disco.Source.utility.DistributedUtils;

import java.util.List;

import static De.Hpi.Disco.Source.utility.DistributedUtils.slicesToString;
import static De.Hpi.Disco.Source.utility.Event.NO_KEY;

public class HolisticWindowAggregate extends BaseWindowAggregate<List<DistributedSlice>> {
    private final int functionId;

    public HolisticWindowAggregate(List<DistributedSlice> value) {
        this(value, NO_KEY);
    }

    public HolisticWindowAggregate(List<DistributedSlice> value, int key) {
        this(value, key, 0);
    }

    public HolisticWindowAggregate(List<DistributedSlice> value, int key, int functionId) {
        super(DistributedUtils.HOLISTIC_STRING, value, key);
        this.functionId = functionId;
    }

    @Override
    public String valueAsString() {
        return slicesToString(this.value, functionId);
    }
}
