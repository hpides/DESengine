package De.Hpi.Disco.Source.executables;

import De.Hpi.Disco.Source.utility.DistributedChild;
import De.Hpi.Disco.generator.Configuration;

public class DistributedChildMain {

    public static void main(String[] args) {
//        if (args.length < 6) {
//            System.out.println("Not enough arguments!\nUsage: java ... parentIp parentControllerPort parentWindowPort "
//                                                                    + "streamPort childId numStreams");
//            System.exit(1);
//        }

//        final String parentIp = args[0];
//        final int parentControllerPort = Integer.parseInt(args[1]);
//        final int parentWindowPort = Integer.parseInt(args[2]);
//        final int streamPort = Integer.parseInt(args[3]);
//        final int childId = Integer.parseInt(args[4]);
//        final int numStreams = Integer.parseInt(args[5]);

        Configuration conf = new Configuration();
        final String parentIp;
        final int parentControllerPort;
        final int parentWindowPort;
        final int streamPort;
        final int childId;
        final int numStreams;

        if(conf.WINDOWS) {
            //windows
            parentIp = args[0];
            parentControllerPort = 40010;
            parentWindowPort = 40020;
            streamPort = 30010;
            childId = Integer.parseInt(args[1]);
            numStreams = 1;
        }else {
            //linux
            parentIp = args[0];
            parentControllerPort = Integer.parseInt(args[1]);
            parentWindowPort = Integer.parseInt(args[2]);
            streamPort = Integer.parseInt(args[3]);
            childId = Integer.parseInt(args[4]);
            numStreams = Integer.parseInt(args[5]);
        }

        runChild(parentIp, parentControllerPort, parentWindowPort, streamPort, childId, numStreams);
    }

    public static Thread runChild(String parentIp, int parentControllerPort, int parentWindowPort, int streamPort, int childId, int numStreams) {
        DistributedChild worker = new DistributedChild(parentIp, parentControllerPort, parentWindowPort, streamPort, childId, numStreams);
        Thread thread = new Thread(worker);
        thread.start();
        return thread;
    }
}
