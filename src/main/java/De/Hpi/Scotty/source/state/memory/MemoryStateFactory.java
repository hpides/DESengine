package De.Hpi.Scotty.source.state.memory;

import De.Hpi.Scotty.source.state.ListState;
import De.Hpi.Scotty.source.state.SetState;
import De.Hpi.Scotty.source.state.StateFactory;
import De.Hpi.Scotty.source.state.ValueState;

public class MemoryStateFactory implements StateFactory {
    @Override
    public <T> ValueState<T> createValueState() {
        return new MemoryValueState<>();
    }

    @Override
    public <T> ListState<T> createListState() {
        return new MemoryListState<>();
    }

    @Override
    public <T extends Comparable<T>> SetState<T> createSetState() {
        return new MemorySetState<>();
    }
}
