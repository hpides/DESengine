package De.Hpi.Scotty.source.state;

import java.io.Serializable;

public interface StateFactory extends Serializable {

    <T> ValueState<T> createValueState();

    <T> ListState<T> createListState();

    <T extends Comparable<T>> SetState<T> createSetState();
}
