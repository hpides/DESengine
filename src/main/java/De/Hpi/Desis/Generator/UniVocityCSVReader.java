package De.Hpi.Desis.Generator;

import De.Hpi.Desis.Configure.Configuration;
import De.Hpi.Desis.Dao.Tuple;
import De.Hpi.Desis.LocalNode.LocalParseAddress;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class UniVocityCSVReader {

    private Configuration conf;
    private CsvParser parser;
    private Random random;
    private CsvParserSettings settings;
    private String csvFile;

    public UniVocityCSVReader(Configuration conf){
        this.conf = conf;
        this.settings = new CsvParserSettings();
        this.settings.getFormat().setLineSeparator("\n");
        this.settings.setIgnoreLeadingWhitespaces(false);
        this.settings.setIgnoreTrailingWhitespaces(false);
        this.settings.setSkipEmptyLines(false);
        this.settings.setColumnReorderingEnabled(false);
        this.csvFile = LocalParseAddress.getFilePath(conf, conf.getNodeId());

        System.out.println(LocalParseAddress.getFilePath(conf, conf.getNodeId()));

        this.random = new Random();
        try {
            this.parser = new CsvParser(this.settings);
            this.parser.beginParsing(new FileReader(this.csvFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getData(){
        String[] line;
        try {
            Record record;
            if ((record = parser.parseNextRecord()) != null) {
//                System.out.println(line[0] + "   " + line[1] + "   " + line[2] + "   " + line[3] + "   " + line[4] + "   " +
//                        line[5] + "   " + line[6]);
//                DataDebs2014 dataDebs2014 = new DataDebs2014();
                return record.getInt(4) + record.getInt(5) + record.getInt(6);
            }else{
                this.parser.stopParsing();
                this.parser = new CsvParser(this.settings);
                this.parser.beginParsing(new FileReader(this.csvFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Tuple getDataTuple(){
        String[] line;
        try {
            Record record;
            if ((record = parser.parseNextRecord()) != null) {
                Tuple tuple = new Tuple();
                tuple.TIME = record.getInt(1);
                tuple.DATA = record.getDouble(2) + record.getInt(3);
                tuple.EVENT = eventSimulator();
                return tuple;
            }else{
                this.parser.stopParsing();
                this.parser = new CsvParser(this.settings);
                this.parser.beginParsing(new FileReader(this.csvFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getDuplicateDataTuple(ArrayList<Tuple> dataBuffer, int duplicateTupleNumber){
        String[] line;
        try {
            Record record;
            if ((record = parser.parseNextRecord()) != null) {
                Tuple tuple = new Tuple();
                tuple.TIME = record.getInt(1);
                tuple.DATA = record.getDouble(2) + record.getInt(3);
                tuple.EVENT = eventSimulator();

                dataBuffer.add(tuple);
                for(int i = 0; i < duplicateTupleNumber; i++){
                    Tuple tupleDuplicate = new Tuple();
                    tupleDuplicate.TIME = tuple.TIME;
                    tupleDuplicate.DATA = tuple.DATA;
                    tupleDuplicate.EVENT = eventSimulator();
                    dataBuffer.add(tupleDuplicate);
                }

            }else{
                this.parser.stopParsing();
                this.parser = new CsvParser(this.settings);
                this.parser.beginParsing(new FileReader(this.csvFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void end(){
        parser.stopParsing();
    }

    public int eventSimulator(){
        return random.nextInt(conf.EVENTRANDOMSEED) == 1 ? 1 : 0;
    }

}
