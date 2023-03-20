package De.Hpi.Disco.Source.aggregation;

import De.Hpi.Disco.Source.utility.DistributedUtils;

public class DistributiveWindowAggregate extends BaseWindowAggregate<Double> {
    public DistributiveWindowAggregate(Double value) {
        super(DistributedUtils.DISTRIBUTIVE_STRING, value);
    }

    public DistributiveWindowAggregate(Double value, int key) {
        super(DistributedUtils.DISTRIBUTIVE_STRING, value, key);
    }

//    public DistributiveWindowAggregate(Double value) {
//        super(DistributedUtils.DISTRIBUTIVE_STRING, (Double) value);
//    }
//
//    public DistributiveWindowAggregate(Double value, int key) {
//        super(DistributedUtils.DISTRIBUTIVE_STRING, (Double) value, key);
//    }

    @Override
    public String valueAsString() {
        return String.valueOf(this.value);
    }
}
