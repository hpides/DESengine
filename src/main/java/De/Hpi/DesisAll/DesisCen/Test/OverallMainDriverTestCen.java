package De.Hpi.DesisAll.DesisCen.Test;

import De.Hpi.DesisAll.DesisCen.Configure.Configuration;
import De.Hpi.DesisAll.DesisCen.EntryPoint.EnteryPointForRootnode;

public class OverallMainDriverTestCen {

    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        EnteryPointForRootnode.main(new String[]{"1",
                String.valueOf(conf.queryNumber), String.valueOf(conf.queryModes), String.valueOf(conf.GeneratorThreadNumber)});

//        EntryPointForLocalNode.main(new String[]{"1",
//                String.valueOf(conf.queryNumber), String.valueOf(conf.queryModes), String.valueOf(conf.GeneratorThreadNumber)});
//        EntryPointForLocalNode.main(new String[]{"2"});
//        EntryPointForLocalNode.main(new String[]{"3"});
//        EntryPointForLocalNode.main(new String[]{"4"});

//        EntryPointForLocalNode.main(new String[]{"2"});
//
//        EnteryPointForIntermediaNode.main(new String[]{"2"});
//        EntryPointForLocalNode.main(new String[]{"3"});
//        EntryPointForLocalNode.main(new String[]{"4"});
    }

}
