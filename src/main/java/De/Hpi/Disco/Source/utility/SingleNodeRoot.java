package De.Hpi.Disco.Source.utility;

import De.Hpi.Disco.Source.aggregation.DistributedAggregateWindowState;
import De.Hpi.Disco.Source.merge.ChildMerger;
import de.tub.dima.scotty.core.windowFunction.AggregateFunction;
import de.tub.dima.scotty.core.windowType.Window;
import org.zeromq.ZMQ;

import java.util.List;
import java.util.stream.Collectors;

import static De.Hpi.Disco.Source.utility.DistributedUtils.MAX_LATENESS;
import static De.Hpi.Disco.Source.utility.DistributedUtils.STREAM_END;

public class SingleNodeRoot extends DistributedRoot implements Runnable {
    private final ChildMerger childMerger;

    public SingleNodeRoot(int controllerPort, int windowPort, String resultPath, int numChildren,
            String windowsString, String aggregateFunctionsString) {
        super(controllerPort, windowPort, resultPath, numChildren, windowsString, aggregateFunctionsString);
        List<Window> windows = DistributedUtils.createWindowsFromStrings(this.nodeImpl.windowStrings);
        List<AggregateFunction> aggFns = DistributedUtils.createAggFunctionsFromString(aggregateFunctionsString);
        nodeImpl.watermarkMs = DistributedUtils.getWatermarkMsFromWindows(windows);
        this.childMerger = new ChildMerger(windows, aggFns, nodeImpl.nodeId);
    }

    @Override
    public void run() {
        System.out.println(nodeImpl.nodeString("Starting single node root with controller port " +
                nodeImpl.controllerPort + ", window port " + nodeImpl.dataPort +
                " and result path " + this.resultPath));

        nodeImpl.startTime = System.currentTimeMillis();
        try {
            nodeImpl.waitForChildren();
            nodeImpl.controlSender = null;
            this.processStreams();
        } finally {
            nodeImpl.close();
        }
    }

    private void processStreams() {
        ZMQ.Socket streamInput = nodeImpl.dataPuller;
        System.out.println(nodeImpl.nodeString("Waiting for stream data."));

        currentEventTime = 0;
        lastWatermark = 0;
        numEvents = 0;

        while (!nodeImpl.isInterrupted()) {
            String eventOrStreamEnd = streamInput.recvStr();

            if (eventOrStreamEnd == null) {
                continue;
            }

            if (eventOrStreamEnd.equals(STREAM_END)) {
                if (!nodeImpl.isTotalStreamEnd()) {
                    continue;
                }

                System.out.println(nodeImpl.nodeString("Processed " + numEvents + " events in total."));
                final long watermarkTimestamp = currentEventTime + nodeImpl.watermarkMs;
                handleWatermark(watermarkTimestamp);
                resultPusher.send(STREAM_END);
                return;
            }

            this.processEvent(eventOrStreamEnd);
        }

        System.out.println(nodeImpl.nodeString("Interrupted while processing streams."));
    }

    private void processEvent(String eventString) {
        final Event event = Event.fromString(eventString);
        this.childMerger.processElement(event);

        currentEventTime = event.getTimestamp();
        numEvents++;

        // If we haven't processed a watermark in watermarkMs milliseconds and waited for the maximum lateness of a
        // tuple, process it.
        final long watermarkTimestamp = lastWatermark + nodeImpl.watermarkMs;
        if (currentEventTime >= watermarkTimestamp + MAX_LATENESS) {
            handleWatermark(watermarkTimestamp);
        }

    }

    private void handleWatermark(long watermarkTimestamp) {
        List<DistributedAggregateWindowState> finalWindows =
                this.childMerger.processWatermarkedWindows(watermarkTimestamp);

        List<WindowResult> windowResults = finalWindows.stream()
                .map(state -> nodeImpl.aggregateMerger.convertUntypedAggregateToWindowResult(state))
                .collect(Collectors.toList());

        windowResults.forEach(this::sendResult);
        lastWatermark = watermarkTimestamp;
    }
}
