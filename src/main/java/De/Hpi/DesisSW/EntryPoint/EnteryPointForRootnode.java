package De.Hpi.DesisSW.EntryPoint;

import De.Hpi.DesisSW.Configure.Configuration;
import De.Hpi.DesisSW.RootNode.RootNode;

public class EnteryPointForRootnode {

    public static void main(String[] args)
    {
        Configuration conf = new Configuration();
        int queryNumber = conf.queryNumber;
        if(Integer.valueOf(args[1]) != 0){
            conf.queryNumber = Integer.valueOf(args[1]);
        }

        if(Integer.valueOf(args[2]) != 0){
            conf.queryModes = Integer.valueOf(args[2]);
        }

        if(Integer.valueOf(args[3]) != 0){
            conf.GeneratorThreadNumber = Integer.valueOf(args[3]);
        }
        RootNode rootNode = new RootNode(conf, Integer.valueOf(args[0]));
    }

}
