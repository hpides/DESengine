package De.Hpi.Disco.Source.executables;

import De.Hpi.Disco.Source.utility.DistributedMergeNode;
import De.Hpi.Disco.generator.Configuration;

public class DistributedMergeNodeMain {

    public static void main(String[] args) {
//        if (args.length < 7) {
//            System.out.println("Not enough arguments!\nUsage: java ... parentIp parentControllerPort parentWindowPort "
//                                                                    + "controllerPort windowPort numChildren nodeId");
//            System.exit(1);
//        }
//
//        final String parentIp = args[0];
//        final int parentControllerPort = Integer.parseInt(args[1]);
//        final int parentWindowPort = Integer.parseInt(args[2]);
//        final int controllerPort = Integer.parseInt(args[3]);
//        final int windowPort = Integer.parseInt(args[4]);
//        final int numChildren = Integer.parseInt(args[5]);
//        final int nodeId = Integer.parseInt(args[6]);

        Configuration conf = new Configuration();
        final String parentIp;
        final int parentControllerPort;
        final int parentWindowPort;
        final int controllerPort;
        final int windowPort;
        final int numChildren;
        final int nodeId;

        if(conf.WINDOWS) {
            //windows
            parentIp = args[0];
            parentControllerPort = 50010;
            parentWindowPort = 50020;
            controllerPort = 40010;
            windowPort = 40020;
            numChildren = Integer.parseInt(args[1]);
            nodeId = Integer.parseInt(args[2]);
        }else {
            //linux
            parentIp = args[0];
            parentControllerPort = Integer.parseInt(args[1]);
            parentWindowPort = Integer.parseInt(args[2]);
            controllerPort = Integer.parseInt(args[3]);
            windowPort = Integer.parseInt(args[4]);
            numChildren = Integer.parseInt(args[5]);
            nodeId = Integer.parseInt(args[6]);
        }

        runMergerNode(parentIp, parentControllerPort, parentWindowPort, controllerPort, windowPort, numChildren, nodeId);
    }

    public static Thread runMergerNode(String parentIp, int parentControllerPort, int parentWindowPort,
                                       int controllerPort, int windowPort, int numChildren, int nodeId) {
        DistributedMergeNode worker = new DistributedMergeNode(parentIp, parentControllerPort, parentWindowPort,
                                                               controllerPort, windowPort, numChildren, nodeId);
        Thread thread = new Thread(worker);
        thread.start();
        return thread;
    }
}
