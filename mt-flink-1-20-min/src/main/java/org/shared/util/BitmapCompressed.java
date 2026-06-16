package org.shared.util;

import org.roaringbitmap.RoaringBitmap;

import java.util.function.IntConsumer;

public class BitmapCompressed implements BitmapWrapper {
    RoaringBitmap bitmap;

    public BitmapCompressed() {
        bitmap = new RoaringBitmap();
    }

    public BitmapCompressed(RoaringBitmap bitmap) {
        this.bitmap = bitmap;
    }


    @Override
    public void set(int index) {
        bitmap.add(index);
    }

    @Override
    public void set(int start, int end) {
        bitmap.add(start, end);
    }

    @Override
    public boolean get(int index) {
        return bitmap.contains(index);
    }

    @Override
    public boolean isEmpty() {
        return bitmap.isEmpty();
    }

    @Override
    public void forEach(IntConsumer action) {
        bitmap.forEach((int x) -> action.accept(x));
    }

    @Override
    public void and(BitmapWrapper other) {
        assert other instanceof BitmapCompressed;
        bitmap.and(((BitmapCompressed) other).bitmap);
    }

    @Override
    public BitmapWrapper copy() {
        return new BitmapCompressed(bitmap.clone());
    }
}
