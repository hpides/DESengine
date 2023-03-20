package De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.IntermediaNode;

import De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Configure.Configuration;

import java.util.ArrayList;
import java.util.Arrays;

public class IntermediateParseAddress {

    public String getRootPubAddress(Configuration configurationTopology){
        if(configurationTopology.WINDOWS == false)
            //linux
            return configurationTopology.rootPubAddr;
        else
            //window
            return configurationTopology.rootPubAddrW;
    }

    public String getInterUpperPubAddress(Configuration configurationTopology, int nodeId){
        if(configurationTopology.WINDOWS == false)
            //linux
            switch(nodeId){
                case 1:
                    return configurationTopology.interUpperPubAddr1;
                default:
                    return configurationTopology.interUpperPubAddr2;
            }
        else
            //window
            switch(nodeId){
                case 1:
                    return configurationTopology.interUpperPubAddr1W;
                default:
                    return configurationTopology.interUpperPubAddr2W;
            }

    }

    public String getInterLowerPubAddress(Configuration configurationTopology, int nodeId){
        if(configurationTopology.WINDOWS == false)
            //linux
            switch(nodeId){
                case 1:
                    return configurationTopology.interLowerPubAddr1;
                default:
                    return configurationTopology.interLowerPubAddr2;
            }
        else
            //window
            switch(nodeId){
                case 1:
                    return configurationTopology.interLowerPubAddr1W;
                default:
                    return configurationTopology.interLowerPubAddr2W;
            }

    }

    public ArrayList<String> getLocalSubAddressAll(Configuration configurationTopology, int nodeId){
        if(configurationTopology.WINDOWS == false)
            //linux
            switch(nodeId){
                case 0:
                    return new ArrayList<String>(Arrays.asList(configurationTopology.localPubAddr1, configurationTopology.localPubAddr2,
                            configurationTopology.localPubAddr3, configurationTopology.localPubAddr4));
                case 1:
                    return new ArrayList<String>(Arrays.asList(configurationTopology.localPubAddr1, configurationTopology.localPubAddr2));
                default:
                    return new ArrayList<String>(Arrays.asList(configurationTopology.localPubAddr3, configurationTopology.localPubAddr4));
            }
        else
            //window
            switch(nodeId){
                case 0:
                    return new ArrayList<String>(Arrays.asList(configurationTopology.localPubAddr1W, configurationTopology.localPubAddr2W,
                            configurationTopology.localPubAddr3W, configurationTopology.localPubAddr4W));
                case 1:
                    return new ArrayList<String>(Arrays.asList(configurationTopology.localPubAddr1W, configurationTopology.localPubAddr2W));
                default:
                    return new ArrayList<String>(Arrays.asList(configurationTopology.localPubAddr3W, configurationTopology.localPubAddr4W));
            }

    }
}
