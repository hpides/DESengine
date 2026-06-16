package org.shared.datagenerator;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Debs2013DataSource implements DataSource, Serializable {
    private static final Logger log = LoggerFactory.getLogger(Debs2013DataSource.class);

    private final String csvPath;
    private final int fieldCount = 11;

    private List<Tuple> datasetTuples;
    private int tupleId;

    public Debs2013DataSource(String csvPath) {
        this.csvPath = csvPath;
        datasetTuples = new ArrayList<>();
    }

    @Override
    public Tuple nextTuple() throws Exception {
        Tuple datasetTuple = datasetTuples.get(tupleId);
        Tuple newTuple = new Tuple(datasetTuple.getKey(), System.currentTimeMillis(), datasetTuple.getFields());

        tupleId = (tupleId + 1) % datasetTuples.size();
        return newTuple;
    }

    @Override
    public void initialize() throws Exception {
        FileReader fileReader = new FileReader(csvPath);
        CsvParserSettings settings = new CsvParserSettings();
        CsvParser parser = new CsvParser(settings);
        parser.beginParsing(fileReader);

        while (true) {
            Record record = parser.parseNextRecord();
            if (record == null) {
                break;
            }
            int key = record.getInt(0);
            int[] fields = new int[fieldCount];
            for (int i = 0; i < fieldCount; i++) {
                fields[i] = record.getInt(i + 2);
            }
            datasetTuples.add(new Tuple(key, 0, fields));
            if (datasetTuples.size() % 1_000_000 == 0) {
                log.info(datasetTuples.size() / 1_000_000 + " M lines parsed");
            }
        }

        parser.stopParsing();
        fileReader.close();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public int getFieldCount() {
        return fieldCount;
    }

    @Override
    public List<Integer> getMinFieldValues() {
        return List.of(-23010, -62545, -13675, 0, 0, -9999, -9999, -9999, -9999, -9999, -9999);
    }

    @Override
    public List<Integer> getMaxFieldValues() {
        return List.of(64513, 56066, 18215, 65490514, 1327791831, 9999, 9999, 9999, 9999, 9999, 9999);
    }


}
