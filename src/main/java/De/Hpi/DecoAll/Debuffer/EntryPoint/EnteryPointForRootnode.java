package De.Hpi.DecoAll.Debuffer.EntryPoint;

import De.Hpi.DecoAll.Debuffer.Configure.Configuration;
import De.Hpi.DecoAll.Debuffer.RootNode.RootNode;

public class EnteryPointForRootnode {

    public static void main(String[] args)
    {
        Configuration conf = new Configuration();
        int queryNumber = conf.queryNumber;

        if(Integer.valueOf(args[1]) != 0){
            conf.queryModes = Integer.valueOf(args[1]);
        }

        if(Integer.valueOf(args[2]) != 0){
            conf.GeneratorThreadNumber = Integer.valueOf(args[2]);
        }
        RootNode rootNode = new RootNode(conf, Integer.valueOf(args[0]));
    }

}
