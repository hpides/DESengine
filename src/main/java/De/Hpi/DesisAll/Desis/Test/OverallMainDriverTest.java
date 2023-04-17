package De.Hpi.DesisAll.Desis.Test;

import De.Hpi.DesisAll.Desis.Configure.Configuration;
import De.Hpi.DesisAll.Desis.EntryPoint.EntryPointForIntermediaNode;
import De.Hpi.DesisAll.Desis.EntryPoint.EntryPointForLocalNode;
import De.Hpi.DesisAll.Desis.EntryPoint.EntryPointForRootNode;

public class OverallMainDriverTest {

    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        EntryPointForRootNode.main(new String[]{"1",
                String.valueOf(conf.queryNumber), String.valueOf(conf.queryModes), String.valueOf(conf.GeneratorThreadNumber)});


        EntryPointForIntermediaNode.main(new String[]{"1",
                String.valueOf(conf.queryNumber), String.valueOf(conf.queryModes), String.valueOf(conf.GeneratorThreadNumber)});
//        EntryPointForLocalNode.main(new String[]{"1"});


        EntryPointForLocalNode.main(new String[]{"1",
                String.valueOf(conf.queryNumber), String.valueOf(conf.queryModes), String.valueOf(conf.GeneratorThreadNumber)});
//        EnteryPointForIntermediaNode.main(new String[]{"2"});
//        EntryPointForLocalNode.main(new String[]{"3"});
//        EntryPointForLocalNode.main(new String[]{"4"});
    }

}
