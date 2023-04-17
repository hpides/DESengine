package De.Hpi.DesisAll.DesisMultipleKeys.Test;

import De.Hpi.DesisAll.DesisMultipleKeys.Configure.Configuration;
import De.Hpi.DesisAll.DesisMultipleKeys.EntryPoint.EnteryPointForIntermediaNode;
import De.Hpi.DesisAll.DesisMultipleKeys.EntryPoint.EnteryPointForRootnode;
import De.Hpi.DesisAll.DesisMultipleKeys.EntryPoint.EntryPointForLocalNode;

public class OverallMainDriverTest {

    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        EnteryPointForRootnode.main(new String[]{"1",
                String.valueOf(conf.queryNumber), String.valueOf(conf.queryModes), String.valueOf(conf.GeneratorThreadNumber)});


        EnteryPointForIntermediaNode.main(new String[]{"1",
                String.valueOf(conf.queryNumber), String.valueOf(conf.queryModes), String.valueOf(conf.GeneratorThreadNumber)});
//        EntryPointForLocalNode.main(new String[]{"1"});


        EntryPointForLocalNode.main(new String[]{"1",
                String.valueOf(conf.queryNumber), String.valueOf(conf.queryModes), String.valueOf(conf.GeneratorThreadNumber)});
//        EnteryPointForIntermediaNode.main(new String[]{"2"});
//        EntryPointForLocalNode.main(new String[]{"3"});
//        EntryPointForLocalNode.main(new String[]{"4"});
    }

}
