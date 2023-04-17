package De.Hpi.DesisAll.DesisCen.EntryPoint;

import De.Hpi.DesisAll.DesisCen.Configure.Configuration;
import De.Hpi.DesisAll.DesisCen.RootNode.RootNode;

public class EnteryPointForRootnode {

    public static void main(String[] args)
    {
        Configuration conf = new Configuration();
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
