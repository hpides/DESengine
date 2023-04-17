package De.Hpi.DesisAll.DesisMultipleKeys.Generator;

import De.Hpi.DesisAll.DesisMultipleKeys.Dao.Tuple;
import De.Hpi.DesisAll.DesisMultipleKeys.Configure.Configuration;
import De.Hpi.DesisAll.DesisMultipleKeys.LocalNode.LocalParseAddress;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class OpenCSVReader {

    private Configuration conf;
    private String csvFile;
    private CSVReader reader;
    private Random random;

    public OpenCSVReader(Configuration conf){
        this.conf = conf;
        this.csvFile = LocalParseAddress.getFilePath(conf, conf.getNodeId());
        System.out.println(LocalParseAddress.getFilePath(conf, conf.getNodeId()));
        this.random = new Random();
        try {
            reader = new CSVReader(new FileReader(csvFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getData(){
        String[] line;
        try {
            if ((line = reader.readNext()) != null) {
//                System.out.println(line[0] + "   " + line[1] + "   " + line[2] + "   " + line[3] + "   " + line[4] + "   " +
//                        line[5] + "   " + line[6]);
//                DataDebs2014 dataDebs2014 = new DataDebs2014();
                return Integer.parseInt(line[4]) + Integer.parseInt(line[5]) + Integer.parseInt(line[6]);
            }else{
                reader.close();
                reader = new CSVReader(new FileReader(csvFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Tuple getDataTuple(){
        String[] line;
        try {
            if ((line = reader.readNext()) != null) {
//                System.out.println(line[0] + "   " + line[1] + "   " + line[2] + "   " + line[3] + "   " + line[4] + "   " +
//                        line[5] + "   " + line[6]);
//                DataDebs2014 dataDebs2014 = new DataDebs2014();
                Tuple tuple = new Tuple();
                tuple.TIME = Integer.valueOf(line[1]);
                tuple.DATA = (double)(Integer.valueOf(line[4]) + Integer.valueOf(line[5]) + Integer.valueOf(line[6]));
//                tuple.EVENT = Integer.valueOf(line[3]);
                tuple.EVENT = eventSimulator();
                return tuple;
            }else{
                reader.close();
                reader = new CSVReader(new FileReader(csvFile));
                return getDataTuple();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int eventSimulator(){
        return random.nextInt(conf.EVENTRANDOMSEED) == 1 ? 1 : 0;
    }

}
