package org.shared.datagenerator;

import org.shared.util.BitmapWrapper;

import java.io.Serializable;
import java.util.Arrays;

public final class Tuple implements Serializable {
    private final int key;
    private long timestamp;
    private final int[] fields;
    private long processingTime;
    BitmapWrapper querySet;

    public Tuple(int key, long timestamp, int[] fields) {
        this.key = key;
        this.timestamp = timestamp;
        this.fields = fields;
    }

    public int getKey() {
        return key;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long eventTime) {
        this.timestamp = eventTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public int[] getFields() {
        return fields;
    }

    public BitmapWrapper getQuerySet() {
        return querySet;
    }

    public void setQuerySet(BitmapWrapper querySet) {
        this.querySet = querySet;
    }

    @Override
    public String toString() {
        return "Tuple [key=" + key + ", timestamp=" + timestamp + ", fields=" + Arrays.toString(fields) + "]";
    }

}
