package org.shared.query;

import org.shared.config.FilterGenerationMode;

import java.io.Serializable;
import java.util.List;

public class QueryGeneratorConfig implements Serializable {
    public final int fields;
    public final List<Integer> minFieldValues;
    public final List<Integer> maxFieldValues;
    public final int minWindowSizeSeconds;
    public final int maxWindowSizeSeconds;
    public final long intervalSeconds;
    public final int startingPerBatch;
    public final int endingPerBatch;
    public final int maxParallelism;
    public final int differentAggregations;
    public final int differentFilters;
    public final boolean compressChangelogSets;
    public final FilterGenerationMode filterGenerationMode;
    public final double overlapRatio;
    public final List<String> aggregationNames;

    public QueryGeneratorConfig(
            int fields,
            List<Integer> minFieldValues,
            List<Integer> maxFieldValues,
            int minWindowSizeSeconds,
            int maxWindowSizeSeconds,
            long intervalSeconds,
            int startingPerBatch,
            int endingPerBatch,
            int maxParallelism,
            int differentAggregations,
            int differentFilters,
            boolean compressChangelogSets,
            FilterGenerationMode filterGenerationMode,
            double overlapRatio,
            List<String> aggregationNames) {
        this.fields = fields;
        this.minFieldValues = minFieldValues;
        this.maxFieldValues = maxFieldValues;
        this.minWindowSizeSeconds = minWindowSizeSeconds;
        this.maxWindowSizeSeconds = maxWindowSizeSeconds;
        this.intervalSeconds = intervalSeconds;
        this.startingPerBatch = startingPerBatch;
        this.endingPerBatch = endingPerBatch;
        this.maxParallelism = maxParallelism;
        this.differentAggregations = differentAggregations;
        this.differentFilters = differentFilters;
        this.compressChangelogSets = compressChangelogSets;
        this.filterGenerationMode = filterGenerationMode;
        this.overlapRatio = overlapRatio;
        this.aggregationNames = aggregationNames;
    }
}
