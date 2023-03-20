package De.Hpi.Disco.Source.executables;

import De.Hpi.Disco.Source.input.EventGenerator;
import De.Hpi.Disco.Source.input.InputStream;
import De.Hpi.Disco.Source.input.InputStreamConfig;
import De.Hpi.Disco.Source.input.ThroughputEventGenerator;

import java.util.function.Function;

public class InputStreamMain {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Not enough arguments!\nUsage: java ... streamId nodeAddress numEvents");
            System.exit(1);
        }

        final int streamId = Integer.parseInt(args[0]);
        final String nodeAddress = args[1];
        final int numEvents = Integer.parseInt(args[2]);

        runInputStream(streamId, nodeAddress, numEvents);
    }

    public static Thread runInputStream(int streamId, String nodeAddress, int numEvents) {
        Function<Long, Long> valueGenerator = (timestamp) -> timestamp;

        long startTime = System.currentTimeMillis() + 1000;
        InputStreamConfig config = new InputStreamConfig(numEvents, 0, 0, startTime, valueGenerator);

        EventGenerator eventGenerator = new ThroughputEventGenerator(streamId, config);

        String[] ipParts = nodeAddress.split(":");
        String nodeIp = ipParts[0];
        int nodePort = Integer.parseInt(ipParts[1]);

        InputStream stream = new InputStream(streamId, config, nodeIp, nodePort, eventGenerator);

        Thread thread = new Thread(stream);
        thread.start();
        return thread;
    }
}
