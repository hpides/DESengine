package De.Hpi.Test;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;

public class TestforSnowest {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {

        HashMap<String, SnowestTuple> snowestTupleHashMap = new HashMap<>();
        HashSet<String> allQueries = new HashSet<>();

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
//        parser.beginParsing(new FileReader("E:\\sourcedata\\snowset\\snowset-main_0.csv"));
        parser.beginParsing(new FileReader("E:\\sourcedata\\snowset\\snowset-main.csv"));

        //among many other things, we can set default values of one ore more columns in the record metadata.
        //Let's again set year to 2000 if it comes as null.
//        parser.getRecordMetadata().setDefaultValueOfColumns(2000, "year");
        Boolean flagForStep = true;
        int firstCreate = 0;

        String search = "3486194332189483438";

        long counter = 0L;
        long counterTemp = 0L;
        long wordCounter = 0L;
        Record record;
        while ((record = parser.parseNextRecord()) != null) {
            String[] temp = record.toString().split(",");
//            System.out.println(record);
//            System.out.println(temp[3]);
            if(temp[3].length() > 15) {
                counter++;
                counterTemp++;
                if(counterTemp%1000000 == 0)
                    System.out.println(counterTemp);

//                if(temp[0].equals(search)) {
//                    wordCounter++;
//                    System.out.println("Got It !   --- " + wordCounter);
//                }
//                if(allQueries.contains(temp[0])){
//                    System.out.println("Got It !   --- " + temp[0]);
//                }else{
//                    allQueries.add(temp[0]);
//                }



//                int timeHour = Integer.parseInt(temp[3].substring(12, 14));
//                int timeMin = Integer.parseInt(temp[3].substring(15, 17));
//                int timeSecond = Integer.parseInt(temp[3].substring(18, 20));
//                int timeMilScond = 0;
//                int timeRealCreate = (timeHour*3600 + timeMin*60 + timeSecond) * 1000 + timeMilScond;
//                int timeDuration = Integer.parseInt(temp[5].substring(1, temp[5].length()));

//                System.out.println(timeHour);
//                System.out.println(timeMin);
//                System.out.println(timeSecond);
//                System.out.println(timeMilScond);
//                System.out.println(timeRealCreate);
//                System.out.println(timeDuration);

//                if(firstCreate == 0)
//                    firstCreate = timeRealCreate;
//                SnowestTuple snowestTuple = new SnowestTuple();
//                snowestTuple.timeCreate = timeRealCreate;
//                snowestTuple.timeDuration = timeDuration;
//                snowestTuple.timeStep = timeRealCreate - firstCreate;
//                snowestTupleHashMap.put(temp[0], snowestTuple);
//                firstCreate = timeRealCreate;
//                System.out.println(timeHour);
//                System.out.println(timeSecond);
//                System.out.println(timeMilScond);
//                System.out.print(timeRealCreate + "  ");
//                System.out.print(timeDuration + "  ");
//                System.out.println(snowestTuple.timeStep);
            }

//            System.out.println(record.getLong(1));
//            System.out.println(record.getDouble(2));
//            System.out.println(record.getInt(3));
//            System.out.print("Year: " + record.getInt("year"));
//            System.out.println(", Model: " + record.getString("model"));
//            System.out.println(", Price: " + record.getBigDecimal("price"));

//            if(counter == 10000000)
//                break;
        }

//        System.out.println(snowestTupleHashMap.size());
        System.out.println();
//        System.out.println(allQueries.size());
        System.out.println(counter);
    }



}

class SnowestTuple {

    public int timeCreate;
    public int timeDuration;
    public int timeStep;

}