package De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Test;

import De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Configure.Configuration;
import De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.EntryPoint.EnteryPointForIntermediaNode;
import De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.EntryPoint.EnteryPointForRootnode;
import De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.EntryPoint.EntryPointForLocalNode;

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
