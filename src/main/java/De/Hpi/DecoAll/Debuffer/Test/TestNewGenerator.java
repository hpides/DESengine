package De.Hpi.DecoAll.Debuffer.Test;

import De.Hpi.DecoAll.Debuffer.Configure.Configuration;
import De.Hpi.DecoAll.Debuffer.Dao.Tuple;
import De.Hpi.DecoAll.Debuffer.EntryPoint.EnteryPointForRootnode;
import De.Hpi.DecoAll.Debuffer.EntryPoint.EntryPointForLocalNode;
import De.Hpi.DecoAll.Debuffer.Generator.InputStream;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestNewGenerator {

    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        ConcurrentLinkedQueue dataQueue = new ConcurrentLinkedQueue<ArrayList<Tuple>>();
        InputStream inputStream = new InputStream(conf, dataQueue, conf.GeneratorThreadNumber);

        System.out.println(conf.eventRatesChangingRange);
        System.out.println(inputStream.getEventRates());
        System.out.println(inputStream.getEventRates());
        System.out.println(inputStream.getEventRates());
        System.out.println(inputStream.getEventRates());
        System.out.println(inputStream.getEventRates());
    }
}
