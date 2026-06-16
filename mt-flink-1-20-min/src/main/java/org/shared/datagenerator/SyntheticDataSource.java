package org.shared.datagenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SyntheticDataSource implements DataSource, Serializable {
    private final int fieldCount;
    private final int keyCount;

    private final int minFieldValue = 0;
    private final int maxFieldValue = 1000;

    private Random rand = new Random(123);

    public SyntheticDataSource(int fieldCount, int keyCount) {
        this.fieldCount = fieldCount;
        this.keyCount = keyCount;
    }

    @Override
    public Tuple nextTuple() throws Exception {
        int key = rand.nextInt(keyCount);

        int[] fields = new int[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            fields[i] = rand.nextInt(maxFieldValue - minFieldValue + 1) + minFieldValue;
        }
        long timestamp = System.currentTimeMillis();

        return new Tuple(key, timestamp, fields);
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public int getFieldCount() {
        return fieldCount;
    }

    @Override
    public List<Integer> getMinFieldValues() {
        return Collections.nCopies(fieldCount, minFieldValue);
    }

    @Override
    public List<Integer> getMaxFieldValues() {
        return Collections.nCopies(fieldCount, maxFieldValue);
    }
}
