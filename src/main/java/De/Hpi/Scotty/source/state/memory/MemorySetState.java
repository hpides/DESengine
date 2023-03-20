package De.Hpi.Scotty.source.state.memory;

import De.Hpi.Scotty.source.state.SetState;

import java.util.Iterator;
import java.util.TreeSet;

public class MemorySetState<Type extends Comparable<Type>> implements SetState<Type> {

    private TreeSet<Type> values = new TreeSet<>();

    public Type getLast() {
        return values.last();
    }

    public Type getFirst() {
        return values.first();
    }

    public Type dropLast() {
        return values.pollLast();
    }

    public Type dropFrist() {
        return values.pollFirst();
    }

    public void add(Type record) {
        values.add(record);
    }

    @Override
    public Iterator<Type> iterator() {
        return values.iterator();
    }

    @Override
    public void clean() {
        values.clear();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public String toString() {
        return values.toString();
    }
}