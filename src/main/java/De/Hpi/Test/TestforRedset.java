package De.Hpi.Test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.io.api.RecordMaterializer;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.io.ColumnIOFactory;
import org.apache.parquet.io.MessageColumnIO;
import org.apache.parquet.io.RecordReader;

import java.io.IOException;

public class TestforRedset {

    public static void main(String[] args) {
        String filePath = "E:\\sourcedata\\redset\\full.parquet";
        Path path = new Path(filePath);
        Configuration conf = new Configuration();
        int counter = 0;
        try {
            // Read Parquet file metadata
            HadoopInputFile inputFile = HadoopInputFile.fromPath(path, conf);
            ParquetMetadata metadata = ParquetFileReader.readFooter(conf, path);
            MessageType schema = metadata.getFileMetaData().getSchema();

            // Print schema
            System.out.println("Schema: " + schema);

            // Create ParquetFileReader
            ParquetFileReader reader = ParquetFileReader.open(inputFile);
            PageReadStore pages;

            while ((pages = reader.readNextRowGroup()) != null) {
                final long rows = pages.getRowCount();
                MessageColumnIO columnIO = new ColumnIOFactory().getColumnIO(schema);
                RecordReader<Group> recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema));
                for (int i = 0; i < rows; i++) {
                    Group group = recordReader.read();
//                    printGroup(group, schema);
                    String temp = group.toString();
                    String test = "query_id: 84990";
                    if(temp.contains(test))
                    System.out.println(group.toString());
                    counter++;
                }
            }
        System.out.println(counter);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printGroup(Group group, MessageType schema) {
        for (int i = 0; i < schema.getFieldCount(); i++) {
            Type field = schema.getType(i);
            if (field.isPrimitive()) {
                System.out.println(field.getName() + ": " + group.getValueToString(i, 0));
                System.out.println(schema.getFieldCount());
            } else {
                System.out.println(field.getName() + ": " + group.getGroup(i, 0));
            }
        }
    }
}