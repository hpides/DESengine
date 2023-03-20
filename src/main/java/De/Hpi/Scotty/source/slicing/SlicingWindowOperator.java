package De.Hpi.Scotty.source.slicing;

import De.Hpi.Scotty.source.core.AggregateWindow;
import De.Hpi.Scotty.source.core.WindowOperator;
import De.Hpi.Scotty.source.core.windowFunction.AggregateFunction;
import De.Hpi.Scotty.source.core.windowType.PunctuationWindow;
import De.Hpi.Scotty.source.core.windowType.Window;
import De.Hpi.Scotty.source.core.windowType.windowContext.WindowContext;
import De.Hpi.Scotty.source.slicing.aggregationstore.AggregationStore;
import De.Hpi.Scotty.source.slicing.aggregationstore.LazyAggregateStore;
import De.Hpi.Scotty.source.slicing.slice.SliceFactory;
import De.Hpi.Scotty.source.state.StateFactory;

import java.util.List;


/**
 * Implementation of the slicing window operator based on the Scotty technique:
 * Scotty: Efficient AbstractWindow Aggregation for out-of-order Stream Processing:
 * Jonas Traub, Philipp M. Grulich, Alejandro Rodrıguez Cuéllar, Sebastian Breß, Asterios Katsifodimos, Tilmann Rabl, Volker Markl *
 * @param <InputType>
 */
public class SlicingWindowOperator<InputType> implements WindowOperator<InputType> {

    private final StateFactory stateFactory;

    private final WindowManager windowManager;
    private final SliceFactory<Integer,Integer> sliceFactory;
    private final SliceManager<InputType> sliceManager;
    private final StreamSlicer slicer;

    public SlicingWindowOperator(StateFactory stateFactory) {
        AggregationStore<InputType> aggregationStore = new LazyAggregateStore<>();
        this.stateFactory = stateFactory;
        this.windowManager = new WindowManager(stateFactory, aggregationStore);
        this.sliceFactory = new SliceFactory<>(windowManager, stateFactory);
        this.sliceManager = new SliceManager<>(sliceFactory, aggregationStore, windowManager);
        this.slicer = new StreamSlicer(sliceManager, windowManager);
    }

    @Override
    public void processElement(InputType element, long ts) {
        /*added for Punctuation Window*/
        for (WindowContext windowContext : this.windowManager.getContextAwareWindows()) {
            if(windowContext instanceof PunctuationWindow.PunctuationContext){
                ((PunctuationWindow.PunctuationContext)windowContext).processPunctuation(element, ts);
            }
        }
        slicer.determineSlices(ts);
        sliceManager.processElement(element, ts);
    }

    @Override
    public List<AggregateWindow> processWatermark(long watermarkTs) {
        return windowManager.processWatermark(watermarkTs);
    }

    @Override
    public void addWindowAssigner(Window window) {
        windowManager.addWindowAssigner(window);
    }

    @Override
    public <OutputType> void addAggregation(AggregateFunction<InputType, ?, OutputType> windowFunction) {
        windowManager.addAggregation(windowFunction);
    }

    public <Agg, OutputType> void addWindowFunction(AggregateFunction<InputType, Agg, OutputType> windowFunction) {
        windowManager.addAggregation(windowFunction);
    }

    @Override
    public void setMaxLateness(long maxLateness) {
        windowManager.setMaxLateness(maxLateness);
    }
}
