package De.Hpi.Disco.generator;


public class LocalParseAddress {

    public static String getFilePath(Configuration configurationGenerator, int nodeId){
        if(!configurationGenerator.WINDOWS) {
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
