package De.Hpi.Disco.Source.merge;

import De.Hpi.Disco.Source.aggregation.DistributedAggregateWindowState;
import De.Hpi.Disco.Source.aggregation.FunctionWindowAggregateId;

import java.util.List;

public class FinalWindowsAndSessionStarts {
    private final List<DistributedAggregateWindowState> finalWindows;
    private final List<FunctionWindowAggregateId> newSessionStarts;

    public FinalWindowsAndSessionStarts(
            List<DistributedAggregateWindowState> finalWindows,
            List<FunctionWindowAggregateId> newSessionStarts) {
        this.finalWindows = finalWindows;
        this.newSessionStarts = newSessionStarts;
    }

    public List<DistributedAggregateWindowState> getFinalWindows() {
        return finalWindows;
    }

    public List<FunctionWindowAggregateId> getNewSessionStarts() {
        return newSessionStarts;
    }
}
