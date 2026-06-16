package org.shared.result;

public class QueryResult {
    public int key;
    public long windowStart;
    public long windowEnd;
    public long eventTimeLatency;
    public int queryIndex;
    public Object result;  // or a more specific type

    public QueryResult(int key, long windowStart, long windowEnd, long eventTimeLatency, int queryIndex, Object result) {
        this.key = key;
        this.windowStart = windowStart;
        this.windowEnd    = windowEnd;
        this.eventTimeLatency = eventTimeLatency;
        this.queryIndex   = queryIndex;
        this.result       = result;
    }

    @Override
    public String toString() {
        return String.format(
                "QueryResult(key=%d, [%d–%d], latency=%d, q=%d, res=%s)",
                key, windowStart, windowEnd, eventTimeLatency, queryIndex, result);
    }

    public String toCSVLine() {
        return key + "," + windowStart + "," + windowEnd + "," + eventTimeLatency + "," + queryIndex + "," + result;
    }
}
