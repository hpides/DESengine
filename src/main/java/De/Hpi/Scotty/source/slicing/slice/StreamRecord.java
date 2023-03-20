package De.Hpi.Scotty.source.slicing.slice;

public class StreamRecord<Type> implements Comparable<StreamRecord<Type>> {
    public final long ts;
    public final Type record;

    public StreamRecord(long ts, Type type) {
        this.ts = ts;
        this.record = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StreamRecord) {
            if (((StreamRecord) obj).record.equals(this.record)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(StreamRecord<Type> o) {
        return Long.compare(ts, o.ts);
    }

    @Override
    public String toString() {
        return "(ts="+ ts + ", value=" + record +")";
    }
}
