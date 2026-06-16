package org.shared.sink;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.core.fs.FSDataOutputStream;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class BulkWriterFactory implements BulkWriter.Factory<String>{

    @Override
    public BulkWriter<String> create(FSDataOutputStream out) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), 65536);

        return new BulkWriter<String>() {

            @Override
            public void addElement(String line) throws IOException {
                bw.write(line);
                bw.newLine();
            }

            @Override
            public void flush() throws IOException {
                bw.flush();
            }

            @Override
            public void finish() throws IOException {
                bw.flush();
            }
        };
    }
}
