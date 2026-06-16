package org.shared.util;

import java.util.BitSet;
import java.util.function.IntConsumer;

public class BitmapUncompressed implements BitmapWrapper {
    BitSet bitset;

    public BitmapUncompressed() {
        bitset = new BitSet();
    }

    public BitmapUncompressed(int capacity) {
        bitset = new BitSet(capacity);
    }

    public BitmapUncompressed(BitSet bitset) {
        this.bitset = bitset;
    }

    @Override
    public void set(int index) {
        bitset.set(index);
    }

    @Override
    public void set(int start, int end) {
        bitset.set(start, end);
    }

    @Override
    public boolean get(int index) {
        return bitset.get(index);
    }

    @Override
    public boolean isEmpty() {
        return bitset.isEmpty();
    }

    @Override
    public void forEach(IntConsumer action) {
        bitset.stream().forEach(action);
    }

    @Override
    public void and(BitmapWrapper other) {
        assert other instanceof BitmapUncompressed;
        bitset.and(((BitmapUncompressed) other).bitset);
    }

    @Override
    public BitmapWrapper copy() {
        return new BitmapUncompressed((BitSet) bitset.clone());
    }
}
