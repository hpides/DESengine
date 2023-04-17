package De.Hpi.DesisAll.DesisQuantile.Test;

import De.Hpi.DesisAll.DesisQuantile.Configure.Configuration;
import De.Hpi.DesisAll.DesisQuantile.EntryPoint.EnteryPointForIntermediaNode;
import De.Hpi.DesisAll.DesisQuantile.EntryPoint.EnteryPointForRootnode;
import De.Hpi.DesisAll.DesisQuantile.EntryPoint.EntryPointForLocalNode;

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
