package De.Hpi.Scotty.source.state;

public interface ValueState<ValueType> extends State {

    ValueType get();

    void set(final ValueType value);

}
