package org.shared.query;

import org.shared.util.BitmapCompressed;
import org.shared.util.BitmapUncompressed;
import org.shared.util.BitmapWrapper;

import java.util.ArrayList;
import java.util.List;

public class QueryChangelog {
    private List<AggregationQuery> newQueries;
    private List<Integer> newQueryPositions;
    private List<Integer> endingQueryPositions;
    private BitmapWrapper changelogSet;
    private long creationTime;
    private long receivedTime;

    public QueryChangelog(
            List<AggregationQuery> newQueries,
            List<Integer> newQueryPositions,
            List<Integer> endingQueryPositions,
            BitmapWrapper changelogSet,
            long creationTime
    ) {
        this.newQueries = newQueries;
        this.newQueryPositions = newQueryPositions;
        this.endingQueryPositions = endingQueryPositions;
        this.changelogSet = changelogSet;
        this.creationTime = creationTime;
    }

    public static QueryChangelog empty(int length, long creationTime, boolean compressed) {
        BitmapWrapper changelogSet;
        if (compressed) {
            changelogSet = new BitmapCompressed();
        } else {
            changelogSet = new BitmapUncompressed();
        }
        changelogSet.set(0, length);
        return new QueryChangelog(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                changelogSet,
                creationTime
        );
    }

    public List<AggregationQuery> getNewQueries() {
        return newQueries;
    }

    public List<Integer> getNewQueryPositions() {
        return newQueryPositions;
    }

    public List<Integer> getEndingQueryPositions() {
        return endingQueryPositions;
    }

    public BitmapWrapper getChangelogSet() {
        return changelogSet;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public void receiveAtTime(int receivedTime) {
        this.receivedTime = receivedTime;
        for (AggregationQuery query : newQueries) {
            query.window.setStart(receivedTime);
        }

    }
}
