package De.Hpi.Disco.Source.utility;

import De.Hpi.Disco.Source.merge.FinalWindowsAndSessionStarts;
import De.Hpi.Disco.generator.Configuration;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static De.Hpi.Disco.Source.utility.DistributedUtils.*;

public class DistributedRoot implements Runnable {
    protected final static String NODE_IDENTIFIER = "ROOT";
    protected final DistributedNode nodeImpl;

    protected final String resultPath;
    protected final ZMQ.Socket resultPusher;

    private long watermarkMs;

    // Count-related
    long currentEventTime = 0;
    long lastWatermark = 0;
    long numEvents = 0;

    //for benchmark
    long endtime;
    Configuration conf;

    //for debug
    private int latencyOverall;
    private int latencyCounter;


    public DistributedRoot(int controllerPort, int windowPort, String resultPath, int numChildren,
            String windowsString, String aggregateFunctionsString) {
        this.nodeImpl = new DistributedNode(0, NODE_IDENTIFIER, controllerPort, windowPort, numChildren, "", 0, 0);
        this.resultPath = resultPath;
        this.endtime = System.currentTimeMillis();
        this.conf = new Configuration();

        //for debug
        this.latencyOverall = 0;
        this.latencyCounter = 0;

        nodeImpl.windowStrings = windowsString.split(ARG_DELIMITER);
        if (nodeImpl.windowStrings.length == 0) {
            throw new IllegalArgumentException("Need at least one window.");
        }

        nodeImpl.aggregateFnStrings = aggregateFunctionsString.split(ARG_DELIMITER);
        if (nodeImpl.aggregateFnStrings.length == 0) {
            throw new IllegalArgumentException("Need at least one aggregate function.");
        }

        this.watermarkMs = DistributedUtils.getWatermarkMsFromWindowString(nodeImpl.windowStrings);

        nodeImpl.createDataPuller();
        this.resultPusher = nodeImpl.context.createSocket(SocketType.PUSH);
        this.resultPusher.connect(DistributedUtils.buildIpcUrl(this.resultPath));
    }

    @Override
    public void run() {
        System.out.println(nodeImpl.nodeString("Starting root worker with controller port " +
                nodeImpl.controllerPort + ", window port " + nodeImpl.dataPort +
                " and result path " + this.resultPath));

        nodeImpl.startTime = System.currentTimeMillis();
        try {

            nodeImpl.waitForChildren();
            nodeImpl.controlSender = null;
            this.processPreAggregatedWindows();
        } finally {
            nodeImpl.close();
        }
    }

    private void processPreAggregatedWindows() {
        ZMQ.Socket windowPuller = nodeImpl.dataPuller;

        int counter = 0;

        while (!nodeImpl.isInterrupted()) {
            String messageOrStreamEnd = windowPuller.recvStr();
            if (messageOrStreamEnd == null) {
                continue;
            }

            long latencyStart = System.nanoTime();

            final List<WindowResult> windowResults;
            switch (messageOrStreamEnd) {
                case STREAM_END: {
                    if (nodeImpl.isTotalStreamEnd()) {
                        System.out.println(nodeImpl.nodeString("Processed " + numEvents + " count-events in total."));
                        resultPusher.send(STREAM_END);
                        return;
                    }
                    continue;
                }
                case EVENT_STRING: {
                    windowResults = processCountEvent();
                    break;
                }
                case CONTROL_STRING: {
                    FinalWindowsAndSessionStarts controlResults = nodeImpl.handleControlInput();
                    windowResults = controlResults.getFinalWindows().stream()
                            .map(state -> nodeImpl.aggregateMerger.convertAggregateToWindowResult(state))
                            .collect(Collectors.toList());
                    break;
                }
                default: {
                    FinalWindowsAndSessionStarts processingResults = nodeImpl.processWindowAggregates();
                    windowResults = processingResults.getFinalWindows().stream()
                            .map(state -> nodeImpl.aggregateMerger.convertAggregateToWindowResult(state))
                            .collect(Collectors.toList());
                    if (++counter % 10_000 == 0) {
                        System.out.println("Received " + counter + " windows.");
                    }
                }
            }
            long latencyEnd = System.nanoTime();
            latencyOverall += (int)(latencyEnd-latencyStart);
            latencyCounter++;
            System.out.println("Root - latency  " + latencyOverall/latencyCounter);

            windowResults.forEach(this::sendResult);
        }
    }
    //processor
    private List<WindowResult> processCountEvent() {
        ZMQ.Socket windowPuller = nodeImpl.dataPuller;
        String rawEvent = windowPuller.recvStr(ZMQ.DONTWAIT);
        Event event = Event.fromString(rawEvent);
        nodeImpl.aggregateMerger.processCountEvent(event);
        currentEventTime = event.getTimestamp();
        numEvents++;
        final long watermarkTimestamp = lastWatermark + this.watermarkMs;
        if (currentEventTime < watermarkTimestamp + MAX_LATENESS) {
            return new ArrayList<>();
        }

        lastWatermark = watermarkTimestamp;
        return nodeImpl.aggregateMerger.processCountWatermark(watermarkTimestamp);
    }

    protected void sendResult(WindowResult windowResult) {
//        System.out.println(nodeImpl.nodeString("Sending result: " + windowResult));
        String finalAggregateString = String.valueOf(windowResult.getValue());
        this.resultPusher.sendMore(DistributedUtils.functionWindowIdToString(windowResult.getFinalWindowId()));
        this.resultPusher.send(finalAggregateString);

        //output debug information
        if (System.currentTimeMillis() - endtime > conf.BenchMarkDebugFrequency) {
            endtime = System.currentTimeMillis();
            System.out.println("rootNode----finalAggregation----"
//                            + window.getWindowId()
//                            + "  QueryId:  " + window.getQueryId()
//                            + "  function  " + window.getFunction()
//                            + "  windowType  " + window.getWindowType()
                            + "  result:  " + finalAggregateString
//                            + "  count:  " + window.count
//                                    + "  listSize:  " + window.tuples.size()
//                                    + "  NetworkOverhead:  " + networkOverhead
//                                    + "  Throughput:  " + window.tupleCounter / ((endtime - begintime) / 1000.0)
            );
        }
    }

    public void interrupt() {
        nodeImpl.interrupt();
    }
}
