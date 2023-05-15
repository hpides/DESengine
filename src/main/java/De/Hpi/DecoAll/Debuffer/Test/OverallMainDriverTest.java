package De.Hpi.DecoAll.Debuffer.Test;

import De.Hpi.DecoAll.Debuffer.Configure.Configuration;
import De.Hpi.DecoAll.Debuffer.EntryPoint.EnteryPointForRootnode;
import De.Hpi.DecoAll.Debuffer.EntryPoint.EntryPointForLocalNode;

public class OverallMainDriverTest {

    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        EntryPointForLocalNode.main(new String[]{"1", String.valueOf(conf.localNumber),
                String.valueOf(conf.queryModes), String.valueOf(conf.GeneratorThreadNumber)});
        EntryPointForLocalNode.main(new String[]{"2", String.valueOf(conf.localNumber),
                String.valueOf(conf.queryModes), String.valueOf(conf.GeneratorThreadNumber)});
        Thread.sleep(1000);

        EnteryPointForRootnode.main(new String[]{"1", String.valueOf(conf.localNumber),
                String.valueOf(conf.queryModes), String.valueOf(conf.GeneratorThreadNumber)});

//        EnteryPointForIntermediaNode.main(new String[]{"2"});
//        EntryPointForLocalNode.main(new String[]{"3"});
//        EntryPointForLocalNode.main(new String[]{"4"});
    }

}
