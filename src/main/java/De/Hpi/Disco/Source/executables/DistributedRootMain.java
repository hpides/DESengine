package De.Hpi.Disco.Source.executables;

import De.Hpi.Disco.generator.Configuration;
import De.Hpi.Disco.Source.utility.ResultListener;
import De.Hpi.Disco.Source.utility.DistributedRoot;
import De.Hpi.Disco.generator.DesisTuple;

public class DistributedRootMain {
    public static void main(String[] args) {
//        if (args.length < 6) {
//            System.out.println("Not enough arguments!\nUsage: java ... controllerPort windowPort resultPath "
//                                                                    + "numChildren windowsString aggFnsString");
//            System.exit(1);
//        }

//        final int rootControllerPort = Integer.parseInt(args[0]);
//        final int rootWindowPort = Integer.parseInt(args[1]);
//        final String rootResultPath = args[2];
//        final int numChildren = Integer.parseInt(args[3]);
//        final String windowsString = args[4];
//        final String aggFnsString = args[5];
        Configuration conf = new Configuration();

        if(Integer.parseInt(args[0])!= 0){
            conf.queryNumber = Integer.parseInt(args[0]);
        }

        String queries = new String();
        System.out.println(conf.queryNumber);
        for(int i = 0; i < conf.queryNumber; i++){
            queries += "TUMBLING,"+1000*(i%10 + 1)+";";
        }
        System.out.println(queries);

        final int rootControllerPort;
        final int rootWindowPort;
        final String rootResultPath;
        final int numChildren;
        final String windowsString;
        final String aggFnsString;
        if(conf.WINDOWS) {
            //windows
            rootControllerPort = 50010;
            rootWindowPort = 50020;
            rootResultPath = "E:/result.txt";
            numChildren = Integer.parseInt(args[1]);
            windowsString = "TUMBLING,1000";
            aggFnsString = "AVG";
        }else{
            //linux
            rootControllerPort = Integer.parseInt(args[1]);
            rootWindowPort = Integer.parseInt(args[2]);
            rootResultPath = args[3];
            numChildren = Integer.parseInt(args[4]);
            windowsString = queries;
            aggFnsString = "AVG";
        }

        runRoot(rootControllerPort, rootWindowPort, rootResultPath, numChildren, windowsString, aggFnsString);
    }

    public static Thread runRoot(int controllerPort, int windowPort, String resultPath, int numChildren,
                                 String windowsString, String aggFnsString) {
        ResultListener resultListener = new ResultListener(resultPath);
        Thread resultThread = new Thread(resultListener);
        resultThread.start();

        DistributedRoot worker = new DistributedRoot(controllerPort, windowPort, resultPath, numChildren,
                                                     windowsString, aggFnsString);
        Thread rootThread = new Thread(worker);
        rootThread.start();
        return rootThread;
    }
}
