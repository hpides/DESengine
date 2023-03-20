package De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Test;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.FileNotFoundException;
import java.io.FileReader;


public class UniVocityCSV {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        CsvParserSettings settings = new CsvParserSettings();
        //the file used in the example uses '\n' as the line separator sequence.
        //the line separator sequence is defined here to ensure systems such as MacOS and Windows
        //are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
        settings.getFormat().setLineSeparator("\n");
        settings.setIgnoreLeadingWhitespaces(false);
        settings.setIgnoreTrailingWhitespaces(false);
        settings.setSkipEmptyLines(false);
        settings.setColumnReorderingEnabled(false);


        // creates a CSV parser
        CsvParser parser = new CsvParser(settings);
        // call beginParsing to read records one by one, iterator-style.
        parser.beginParsing(new FileReader("E:/sourcedata/sorted.csv"));

        //among many other things, we can set default values of one ore more columns in the record metadata.
        //Let's again set year to 2000 if it comes as null.
//        parser.getRecordMetadata().setDefaultValueOfColumns(2000, "year");

        Record record;
        while ((record = parser.parseNextRecord()) != null) {
            System.out.println(record);
            System.out.println(record.getInt(0));
            System.out.println(record.getLong(1));
            System.out.println(record.getDouble(2));
            System.out.println(record.getInt(3));
//            System.out.print("Year: " + record.getInt("year"));
//            System.out.println(", Model: " + record.getString("model"));
//            System.out.println(", Price: " + record.getBigDecimal("price"));
        }
    }

}
