package De.Hpi.DecoAll.SingleRound.EntryPoint;

import De.Hpi.DecoAll.SingleRound.Configure.Configuration;
import De.Hpi.DecoAll.SingleRound.LocalNode.LocalNode;

public class EntryPointForLocalNode {

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
        LocalNode localNode = new LocalNode(conf, Integer.valueOf(args[0]));
    }

}
