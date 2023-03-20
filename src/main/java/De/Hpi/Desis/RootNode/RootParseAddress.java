package De.Hpi.Desis.RootNode;

import De.Hpi.Desis.Configure.Configuration;

import java.util.ArrayList;
import java.util.Arrays;

public class RootParseAddress {

    public String getRootPubAddress(Configuration configurationTopology){
        if(configurationTopology.RASBERRYPI == true){
            return configurationTopology.rootPubAddrR;
        }

        if(configurationTopology.WINDOWS == false )
            //linux
            return configurationTopology.rootPubAddr;
        else
            //windows
            return configurationTopology.rootPubAddrW;
    }

    public ArrayList<String> getInterSubAddressAll(Configuration configurationTopology){
        if(configurationTopology.RASBERRYPI == true){
            return new ArrayList<String>(Arrays.asList(configurationTopology.interUpperPubAddr1R, configurationTopology.interUpperPubAddr2R));
        }

        if(configurationTopology.WINDOWS == false )
            //linux
            return new ArrayList<String>(Arrays.asList(configurationTopology.interUpperPubAddr1, configurationTopology.interUpperPubAddr2));
        else
            //windows
            return new ArrayList<String>(Arrays.asList(configurationTopology.interUpperPubAddr1W, configurationTopology.interUpperPubAddr2W));
    }

    public ArrayList<String> getLocalSubAddressAll(Configuration configurationTopology){
        if(configurationTopology.RASBERRYPI == true){
            return new ArrayList<String>(Arrays.asList(configurationTopology.localPubAddr1R, configurationTopology.localPubAddr2R
                    , configurationTopology.localPubAddr3R, configurationTopology.localPubAddr4R));
        }

        if(configurationTopology.WINDOWS == false )
            //linux
            return new ArrayList<String>(Arrays.asList(configurationTopology.localPubAddr1, configurationTopology.localPubAddr2
                    , configurationTopology.localPubAddr3, configurationTopology.localPubAddr4));
        else
            //windows
            return new ArrayList<String>(Arrays.asList(configurationTopology.localPubAddr1W, configurationTopology.localPubAddr2W
                    , configurationTopology.localPubAddr3W, configurationTopology.localPubAddr4W));
    }

}