package De.Hpi.Disco.Source.utility;

import De.Hpi.Disco.generator.InputStream;
import De.Hpi.Disco.Source.aggregation.DistributedAggregateWindowState;
import De.Hpi.Disco.Source.merge.ChildMerger;
import De.Hpi.Disco.generator.Configuration;
import De.Hpi.Disco.generator.DesisTuple;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static De.Hpi.Disco.Source.utility.DistributedUtils.DEFAULT_SOCKET_TIMEOUT_MS;
import static De.Hpi.Disco.Source.utility.DistributedUtils.MAX_LATENESS;

public class DistributedChild implements Runnable {
    private final static String NODE_IDENTIFIER = "CHILD";

    protected final DistributedNode nodeImpl;

    private long currentEventTime;
    private long lastWatermark;
    private long numEvents;
    private long numOutOfOrderEvents;

    public final static int STREAM_REGISTER_PORT_OFFSET = 100;
    public final static long STREAM_REGISTER_TIMEOUT_MS = 10 * 1000;

    private long processingTime;
    private long receivingTime;
    private long numEventsInLastSecond;
    private long lastSecondEnd;

    private ChildMerger childMerger;

    private boolean hasCountWindow;
    private boolean hasDistributedWindow;

    //for debug
    private int latencyOverall;
    private int latencyCounter;

    public DistributedChild(String parentIp, int parentControllerPort, int parentWindowPort,
                            int streamInputPort, int childId, int numStreams) {
        this.nodeImpl = new DistributedNode(childId, NODE_IDENTIFIER, streamInputPort + STREAM_REGISTER_PORT_OFFSET,
                streamInputPort, numStreams, parentIp, parentControllerPort, parentWindowPort);
        this.hasCountWindow = false;

        //for debug
        this.latencyOverall = 0;
        this.latencyCounter = 0;

        nodeImpl.createDataPuller();
        nodeImpl.createWindowPusher(parentIp, parentWindowPort);
    }

    @Override
    public void run() {
        System.out.println(nodeImpl.nodeString("Starting child worker on port " + nodeImpl.dataPort +
                " with " + nodeImpl.numChildren + " stream(s). Connecting to parent at " +
                nodeImpl.parentIp + " with controller port " + nodeImpl.parentControllerPort +
                " and window port " + nodeImpl.parentWindowPort));

        try {
            WindowingConfig config = this.nodeImpl.registerAtParent();
            this.hasCountWindow = !config.getCountWindows().isEmpty();
            this.hasDistributedWindow = !config.getTimeWindows().isEmpty();

//            boolean registerSuccess = this.registerStreams(config);
//            if (!registerSuccess) {
//                return;
//            }
            //for new generator
            this.childMerger = new ChildMerger(config.getTimeWindows(),
                    config.getAggregateFunctions(), nodeImpl.nodeId);

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
        numOutOfOrderEvents = 0;
        processingTime = 0;
        receivingTime = 0;
        numEventsInLastSecond = 0;
        lastSecondEnd = System.currentTimeMillis() + 1000;

        Configuration conf = new Configuration();
        ConcurrentLinkedQueue<ArrayList<DesisTuple>> dataQueue = new ConcurrentLinkedQueue<ArrayList<DesisTuple>>();
        ArrayList<Thread> threadsList = new ArrayList<Thread>();
        threadsList.add(new Thread(new InputStream(conf, dataQueue, conf.GeneratorThreadNumber)));
        threadsList.forEach(thread -> thread.start());

        final long startTime = System.currentTimeMillis();
        while (!nodeImpl.isInterrupted()) {
            if (!dataQueue.isEmpty()) {
                ArrayList<DesisTuple> bufferTemp = dataQueue.poll();
                bufferTemp.forEach(tuple -> {

                    final long realTimestamp = System.currentTimeMillis();
                    final long eventTimestamp = realTimestamp - startTime;
                    final Double eventValue = tuple.DATA;
                    final int key = tuple.KEY;
                    final Event event = new Event(eventValue, eventTimestamp, tuple.KEY);
                    final String msg = event.asString();
                    if (this.hasCountWindow) {
                        nodeImpl.forwardEvent(msg);
                    }else {
                        this.processEvent(msg);
                    }
                });
//                counterTest-= bufferTemp.size();
                //We only process the Value of a tuple
            }
        }

        /*
        while (!nodeImpl.isInterrupted()) {
//            final long receivingStart = System.nanoTime();
            String eventOrStreamEnd = streamInput.recvStr();
//            final long receivingEnd = System.nanoTime();

            if (eventOrStreamEnd == null) {
                continue;
            }

            if (eventOrStreamEnd.equals(DistributedUtils.STREAM_END)) {
                if (!nodeImpl.isTotalStreamEnd()) {
                    continue;
                }

                System.out.println(nodeImpl.nodeString("Processed " + numEvents + " events in total."));
                final double outOfOrderPercentage = (numOutOfOrderEvents / (double) numEvents) * 100;
                System.out.println(nodeImpl.nodeString(outOfOrderPercentage + "% of events out-of-order "
                        + "(" + numOutOfOrderEvents + "/" + numEvents + ")"));
                final long watermarkTimestamp = currentEventTime + nodeImpl.watermarkMs;
                handleWatermark(watermarkTimestamp);
                System.out.println(nodeImpl.nodeString("No more data to come. Ending child worker..."));
                nodeImpl.endChild();
                return;
            }

            if (this.hasCountWindow) {
                nodeImpl.forwardEvent(eventOrStreamEnd);
            }

            if (!this.hasDistributedWindow) {
                continue;
            }

            this.processEvent(eventOrStreamEnd);
//            receivingTime += (receivingEnd - receivingStart);

//            if (System.currentTimeMillis() > lastSecondEnd) {
//                System.out.println("Processed " + numEventsInLastSecond + " events in last second.");
//                System.out.println("Avg receiving  time: " + (receivingTime / numEventsInLastSecond) + " ns.");
//                System.out.println("Avg processing time: " + (processingTime / numEventsInLastSecond) + " ns.");
//                processingTime = 0;
//                receivingTime = 0;
//                numEventsInLastSecond = 0;
//                lastSecondEnd += 1000;
//            }
        }
        */

        System.out.println(nodeImpl.nodeString("Interrupted while processing streams."));
    }

    private void processEvent(String eventString) {
        //for debug
        long latencyStart = System.nanoTime();

//        final long processingStart = System.nanoTime();
        final Event event = Event.fromString(eventString);
        this.childMerger.processElement(event);
//        final long processingEnd = System.nanoTime();

        if (event.getTimestamp() < currentEventTime) {
            numOutOfOrderEvents++;
        }

        currentEventTime = Math.max(currentEventTime, event.getTimestamp());
        numEvents++;
        numEventsInLastSecond++;
//        processingTime += (processingEnd - processingStart);

        // If we haven't processed a watermark in watermarkMs milliseconds and waited for the maximum lateness of a
        // tuple, process it.
        final long watermarkTimestamp = lastWatermark + nodeImpl.watermarkMs;
        if (currentEventTime >= watermarkTimestamp + MAX_LATENESS) {
            handleWatermark(watermarkTimestamp);
        }
        if (currentEventTime >= watermarkTimestamp + MAX_LATENESS) {
            handleWatermark(watermarkTimestamp);
            long latencyEnd = System.nanoTime();
            latencyOverall += (int)(latencyEnd-latencyStart);
            latencyCounter++;
                    System.out.println("local - latency  " + latencyOverall/latencyCounter);
        }

    }

    private void handleWatermark(long watermarkTimestamp) {
//        final long watermarkStart = System.nanoTime();
        List<DistributedAggregateWindowState> finalWindows =
                this.childMerger.processWatermarkedWindows(watermarkTimestamp);
//        final long watermarkEnd = System.nanoTime();
//        System.out.println("Watermark processing took " + (watermarkEnd - watermarkStart) + " ns.");
//        final long sendingStart = System.nanoTime();
        nodeImpl.sendPreAggregatedWindowsToParent(finalWindows);

//        if(!finalWindows.isEmpty())
//        System.out.println("asgsgd" + finalWindows.get(0));


//        final long sendingEnd = System.nanoTime();
//        System.out.println("Watermark sending took " + (sendingEnd - sendingStart) + " ns.");

//        final long sessionStart = System.nanoTime();
        finalWindows.stream()
                .map(state -> childMerger.getNextSessionStart(state.getFunctionWindowId()))
                .forEach(newSession -> newSession.ifPresent(nodeImpl::sendSessionStartToParent));
//        final long sessionEnd = System.nanoTime();
//        System.out.println("Session start took " + (sessionEnd - sessionStart) + " ns.");

        lastWatermark = watermarkTimestamp;
    }

    protected boolean registerStreams(final WindowingConfig windowingConfig) {
        final ZMQ.Socket streamReceiver = nodeImpl.context.createSocket(SocketType.REP);
        streamReceiver.setReceiveTimeOut(DEFAULT_SOCKET_TIMEOUT_MS);
        streamReceiver.bind(DistributedUtils.buildBindingTcpUrl(nodeImpl.dataPort + STREAM_REGISTER_PORT_OFFSET));

        String startTimeResponse = String.valueOf(nodeImpl.startTime);

        int numRegisteredStreams = 0;
        while (!nodeImpl.isInterrupted()) {
            final String rawStreamId = streamReceiver.recvStr();

            if (rawStreamId == null) {
                continue;
            }

            final int streamId = Integer.parseInt(rawStreamId);
            System.out.println(nodeImpl.nodeString("Registering stream " + streamId));
            streamReceiver.send(startTimeResponse);
            numRegisteredStreams++;

            if (numRegisteredStreams == nodeImpl.numChildren) {
                // All streams registered
                System.out.println(nodeImpl.nodeString("Registered all streams (" + numRegisteredStreams + " in total)"));
                this.childMerger = new ChildMerger(windowingConfig.getTimeWindows(),
                        windowingConfig.getAggregateFunctions(), nodeImpl.nodeId);
                return true;
            }
        }

        System.out.println(nodeImpl.nodeString("Interrupted while registering streams."));
        return false;
    }

    public void interrupt() {
        nodeImpl.interrupt();
    }


}
