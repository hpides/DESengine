package De.Hpi.Scotty;

import De.Hpi.Scotty.Configure.Configuration;
import De.Hpi.Scotty.source.core.AggregateWindow;
import De.Hpi.Scotty.source.core.windowType.TumblingWindow;
import De.Hpi.Scotty.source.core.windowType.WindowMeasure;
import De.Hpi.Scotty.source.slicing.SlicingWindowOperator;
import De.Hpi.Scotty.source.state.StateFactory;
import De.Hpi.Scotty.source.state.memory.MemoryStateFactory;
import org.apache.flink.types.Value;

import java.util.HashMap;
import java.util.List;

public class ScottyQuantile {
    public long lastWatermark = 0;
    public MemoryStateFactory stateFactory;
    public HashMap<String, SlicingWindowOperator<Integer>> slicingWindowOperatorMap =
            slicingWindowOperatorMap = new HashMap<>();
    private long debugTimer;
    private long beginTimer;
    private Configuration conf;

    //for debug
    private int latencyOverall;
    private int latencyCounter;


    public ScottyQuantile(Configuration conf){
        this.debugTimer = System.currentTimeMillis();
        this.beginTimer = System.currentTimeMillis();
        this.conf = conf;

        //for debug
        this.latencyOverall = 0;
        this.latencyCounter = 0;

    }

    public SlicingWindowOperator<Integer> initWindowOperator() {
        StateFactory stateFactory = new MemoryStateFactory();
        SlicingWindowOperator<Integer> slicingWindowOperator = new SlicingWindowOperator<>(stateFactory);


//        windowFunction = new Sum();
//        windowFunction = new QuantileWindowFunction(0.5);
        for(int i = 0; i < conf.queryNumber; i++){
            slicingWindowOperator.addWindowAssigner(new TumblingWindow(WindowMeasure.Time, 1000*(i%10 + 1)));
            System.out.println(1000*(i%10 + 1));
        }
//        slicingWindowOperator.addAggregation(new Quantile(0.5));
        slicingWindowOperator.addAggregation(new Quantile(0.9));

        slicingWindowOperator.setMaxLateness(0);
        return slicingWindowOperator;
    }

    public void processWatermark(String currentKey, long timeStamp, long tupleCounter) {
        // Every tuple represents a Watermark with its timestamp.
        // A watermark is processed if it is greater than the old watermark, i.e. monotonically increasing.
        // We process watermarks every watermarkEvictionPeriod in event-time

        //for debug
        long latencyStart = System.nanoTime();

        long watermarkEvictionPeriod = 1000;
        if (timeStamp > lastWatermark + watermarkEvictionPeriod) {

            for (SlicingWindowOperator<Integer> slicingWindowOperator : this.slicingWindowOperatorMap.values()) {
                List<AggregateWindow> aggregates = slicingWindowOperator.processWatermark(timeStamp);
                for (AggregateWindow<Value> aggregateWindow : aggregates) {
//                    basicOutputCollector.emit(new Values(currentKey, aggregateWindow));

                    long latencyEnd = System.nanoTime();
                    latencyOverall += (int)(latencyEnd-latencyStart);
                    latencyCounter++;
                    System.out.println("local - latency  " + latencyOverall/latencyCounter);


                    if (System.currentTimeMillis() - debugTimer > conf.BenchMarkDebugFrequency) {
                        debugTimer = System.currentTimeMillis();
                        //out put final result
                        System.out.println("Scotty----finalAggregation----"
//                                        + window.getWindowId()
//                                        + "  QueryId:  " + window.getQueryId()
//                            + "  function  " + window.getFunction()
//                            + "  windowType  " + window.getWindowType()
                                        + "  result:  " + aggregateWindow.getAggValues()
                                        + "  count:  " + tupleCounter*1000/(System.currentTimeMillis() - beginTimer + 1)
//                                    + "  listSize:  " + window.tuples.size()
//                                    + "  NetworkOverhead:  " + networkOverhead
//                                    + "  Throughput:  " + window.tupleCounter / ((endtime - begintime) / 1000.0)
                        );
                    }
                }
            }
            lastWatermark = timeStamp;
        }
    }
}
