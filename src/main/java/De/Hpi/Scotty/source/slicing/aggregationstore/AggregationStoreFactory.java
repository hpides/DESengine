package De.Hpi.Scotty.source.slicing.aggregationstore;

public interface AggregationStoreFactory {

    <InputType> AggregationStore<InputType> createAggregationStore();
}
