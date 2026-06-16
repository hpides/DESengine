package org.shared.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Config implements Serializable {

    public BenchmarkConfig benchmark;
    public DataGeneratorConfig dataGenerator;
    public QueryGeneratorConfig queryGenerator;

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class BenchmarkConfig {
        public OperatorType operator;
        public boolean compressBitmaps;
        public int maxDurationSeconds;
        public int durationWithoutRateDecrease;
        public String outputPrefix;
        public int parallelism;
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DataGeneratorConfig {
        public DataSourceType dataSource;
        public String debsPath;
        public int syntheticFieldCount;
        public int syntheticKeyCount;
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class QueryGeneratorConfig {
        public int minWindowLengthSeconds;
        public int maxWindowLengthSeconds;
        public int batchInterval;
        public int startingPerBatch;
        public int endingPerBatch;
        public int maxParallelism;
        public int differentAggregations;
        public int differentFilters;
        public FilterGenerationMode filterGenerationMode;
        public double overlapRatio;
        public List<String> aggregations;
    }
}
