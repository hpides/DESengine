package De.Hpi.Scotty.source.core;

import De.Hpi.Scotty.source.core.windowType.WindowMeasure;

public interface WindowCollector {

    public void trigger(long start, long end, WindowMeasure measure);
}
