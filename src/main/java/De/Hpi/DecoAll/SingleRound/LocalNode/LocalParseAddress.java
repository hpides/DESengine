package De.Hpi.DecoAll.SingleRound.LocalNode;

import De.Hpi.DecoAll.SingleRound.Configure.Configuration;

public class LocalParseAddress {

    public String getRootPubAddress(Configuration configurationTopology){
        if(configurationTopology.WINDOWS == false)
            //linux
            return configurationTopology.rootPubAddr;
        else
            //window
            return configurationTopology.rootPubAddrW;
    }

    public static String getLocalPubAddress(Configuration configurationTopology, int nodeId){
        if(configurationTopology.WINDOWS == false)
            //linux
            switch(nodeId){
                case 1:
                    return configurationTopology.localPubAddr1;
                case 2:
                    return configurationTopology.localPubAddr2;
                case 3:
                    return configurationTopology.localPubAddr3;
                default:
                    return configurationTopology.localPubAddr4;
            }
        else
            //linux
            switch(nodeId){
                case 1:
                    return configurationTopology.localPubAddr1W;
                case 2:
                    return configurationTopology.localPubAddr2W;
                case 3:
                    return configurationTopology.localPubAddr3W;
                default:
                    return configurationTopology.localPubAddr4W;
            }

    }

    public static String getFilePath(Configuration configurationGenerator, int nodeId){
        if(!configurationGenerator.WINDOWS) {
            //windows
            switch (nodeId) {
                case 0:
                    return configurationGenerator.LINUX_FILEADDRESS_0;
                case 1:
                    return configurationGenerator.LINUX_FILEADDRESS_0;
                case 2:
                    return configurationGenerator.LINUX_FILEADDRESS_1;
                case 3:
                    return configurationGenerator.LINUX_FILEADDRESS_2;
                default:
                    return configurationGenerator.LINUX_FILEADDRESS_3;
            }
        }else{
            //linux
            switch (nodeId) {
                case 0:
                    return configurationGenerator.WINDOWS_FILEADDRESS;
                case 1:
                    return configurationGenerator.WINDOWS_FILEADDRESS_0;
                case 2:
                    return configurationGenerator.WINDOWS_FILEADDRESS_1;
                case 3:
                    return configurationGenerator.WINDOWS_FILEADDRESS_2;
                default:
                    return configurationGenerator.WINDOWS_FILEADDRESS_3;
            }
        }

    }

}
