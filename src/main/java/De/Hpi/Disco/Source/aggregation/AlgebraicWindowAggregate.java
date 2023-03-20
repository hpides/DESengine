package De.Hpi.Disco.Source.aggregation;

import De.Hpi.Disco.Source.utility.DistributedUtils;

public class AlgebraicWindowAggregate extends BaseWindowAggregate<AlgebraicPartial> {
    public AlgebraicWindowAggregate(AlgebraicPartial value) {
        super(DistributedUtils.ALGEBRAIC_STRING, value);
    }

    public AlgebraicWindowAggregate(AlgebraicPartial value, int key) {
        super(DistributedUtils.ALGEBRAIC_STRING, value, key);
    }

    @Override
    public String valueAsString() {
        return value != null ? this.value.asString() : null;
    }
}
