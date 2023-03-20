package De.Hpi.Scotty.source.core.windowType;

import De.Hpi.Scotty.source.core.WindowCollector;

public interface ContextFreeWindow extends Window {

    long assignNextWindowStart(long position);

    void triggerWindows(WindowCollector aggregateWindows, long lastWatermark, long currentWatermark);

    long clearDelay();
}
