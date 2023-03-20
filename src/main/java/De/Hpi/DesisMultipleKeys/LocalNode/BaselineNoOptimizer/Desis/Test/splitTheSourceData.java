package De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Test;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class splitTheSourceData {

    public static void main(String[] args) throws IOException {
        String csvFile;
        CSVReader reader;

        long counter = 0;
        long fileCounter = 0;
        long allCounter = 0;

//        csvFile = "E:/sourcedata/sorted.csv";
        csvFile = "/hpi/fs00/home/wang.yue/data/sorted_0.csv";
//        Writer writer = Files.newBufferedWriter(Paths.get("E:/sourcedata/test" + fileCounter + ".csv"));
        Writer writer = Files.newBufferedWriter(Paths.get("/hpi/fs00/home/wang.yue/data/sorted.csv"));

        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        try {
            reader = new CSVReader(new FileReader(csvFile));
            String[] line;
//            line = reader.readNext();
            while((line = reader.readNext()) != null) {
                counter++;
                allCounter++;

//                if(counter > 135000000 * 3){
//                    System.out.println(counter + "  " + line);
//                    break;
//                }

                csvWriter.writeNext(line);

                if(counter % 1000000L ==0 ){
                    System.out.println(allCounter);
                }

//                if(allCounter > 135000000){
//                    allCounter = 0;
//                    reader.close();
//                    reader = new CSVReader(new FileReader(csvFile));
//                }

//                if(counter > 6000000000L){
//                    fileCounter++;
//                    counter=0;
////                    writer = Files.newBufferedWriter(Paths.get("E:/sourcedata/test" + fileCounter + ".csv"));
//                    writer = Files.newBufferedWriter(Paths.get("/hpi/fs00/home/wang.yue/data/sorted_" + fileCounter + ".csv"));
//                    csvWriter = new CSVWriter(writer,
//                            CSVWriter.DEFAULT_SEPARATOR,
//                            CSVWriter.NO_QUOTE_CHARACTER,
//                            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//                            CSVWriter.DEFAULT_LINE_END);
////                    break;
//                    System.out.println(allCounter);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(counter);

    }

}
