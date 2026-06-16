package org.shared.util;

import java.io.Serializable;
import java.util.function.IntConsumer;

public interface BitmapWrapper extends Serializable {
    void set(int index);
    void set(int start, int end);
    boolean get(int index);
    boolean isEmpty();
    void forEach(IntConsumer action);
    void and(BitmapWrapper other);
    BitmapWrapper copy();
}
