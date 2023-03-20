package De.Hpi.Disco;

import De.Hpi.Disco.Source.executables.DistributedChildMain;
import De.Hpi.Disco.Source.executables.DistributedMergeNodeMain;
import De.Hpi.Disco.Source.executables.DistributedRootMain;

public class TestDisco {

    public static void main(String[] args) throws Exception
    {
        DistributedRootMain.main(new String[]{"1","1"});
//        System.out.println("controllerPort windowPort resultPath "
//                + "numChildren windowsString aggFnsString");

        DistributedMergeNodeMain.main(new String[]{"localhost", "1", "1"});
//        DistributedMergeNodeMain.main(new String[]{"localhost", "50010", "50020",
//                "40110", "40120",
//                "2", "2" });
//        System.out.println("parentIp parentControllerPort parentWindowPort "
//                + "controllerPort windowPort numChildren nodeId");

        DistributedChildMain.main(new String[]{"localhost", "1"});
//        DistributedChildMain.main(new String[]{"localhost", "40010", "40020",
//                "30020", "2", "1" });
//        DistributedChildMain.main(new String[]{"localhost", "40110", "40120",
//                "30030", "3", "1" });
//        DistributedChildMain.main(new String[]{"localhost", "40110", "40120",
//                "30040", "4", "1" });
//        System.out.println("parentIp parentControllerPort parentWindowPort "
//                + "streamPort childId numStreams");

//        InputStreamMain.main(new String[]{"1", "localhost:30010", "10000000"});
//        InputStreamMain.main(new String[]{"1", "localhost:30020", "10000000"});
//        InputStreamMain.main(new String[]{"1", "localhost:30030", "10000000"});
//        InputStreamMain.main(new String[]{"1", "localhost:30040", "10000000"});
//        System.out.println("streamId nodeAddress numEvents");

    }

}
