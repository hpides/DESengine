package org.shared.query;

import org.shared.config.FilterGenerationMode;
import org.shared.query.aggregation.*;
import org.shared.query.filter.*;
import org.shared.util.BitmapCompressed;
import org.shared.util.BitmapUncompressed;
import org.shared.util.BitmapWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class QueryGenerator implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(QueryGenerator.class);

    private final QueryGeneratorConfig config;

    private int activeQueryCount;
    private int bitsetSize;
    private Random random;
    private Random reseededRandom;
    private int lastTime;

    private int nextQueryId;

    public QueryGenerator(
            QueryGeneratorConfig config
    ){
        this.config = config;

        activeQueryCount = 0;
        bitsetSize = 0;
        random = new Random();
        reseededRandom = new Random();
        lastTime = -1;
        nextQueryId = 0;
    }


    public QueryChangelog changelogForTime(int time, long changelogTimestamp) {
        assert time == lastTime + 1;
        lastTime = time;
        random.setSeed(time);

        if (time % config.intervalSeconds != 0) {
            return null;
        }

        return generateQueryBatch(time, changelogTimestamp);
    }

    private QueryChangelog generateQueryBatch(int time, long changelogTimestamp) {
        int ending = Math.min(config.endingPerBatch, activeQueryCount);
        int starting = Math.min(config.startingPerBatch, config.maxParallelism - (activeQueryCount - ending));

        int spaceInFront = bitsetSize - activeQueryCount;

        int newBitsetSize = Math.max(bitsetSize, bitsetSize + starting - ending);
        BitmapWrapper newBitSet;
        if (config.compressChangelogSets) {
            newBitSet = new BitmapCompressed();
        } else {
            newBitSet = new BitmapUncompressed(newBitsetSize);
        }
        newBitSet.set(spaceInFront + ending, bitsetSize);

        List<Integer> endingPositions = new ArrayList<>();
        for (int pos = spaceInFront; pos < spaceInFront + ending; pos++) {
            endingPositions.add(pos);
        }


        List<Integer> startingPositions = new ArrayList<>();
        List<AggregationQuery> startingQueries = new ArrayList<>();

        // Fill potential empty slots in front
        for (int pos = spaceInFront + ending - 1; pos >= 0 && startingPositions.size() < starting; pos--) {
            startingPositions.add(pos);
            startingQueries.add(generateSingleQuery(time));
        }
        // Fill potential new slots in back
        for (int pos = newBitsetSize - 1; pos >= bitsetSize; pos--) {
            startingPositions.add(pos);
            startingQueries.add(generateSingleQuery(time));
        }
        assert startingPositions.size() == starting;

        bitsetSize = newBitsetSize;
        activeQueryCount += starting - ending;
        return new QueryChangelog(startingQueries, startingPositions, endingPositions, newBitSet, changelogTimestamp);
    }

    private AggregationQuery generateSingleQuery(int startTime) {
        Filter filter;
        if (config.filterGenerationMode == FilterGenerationMode.Random) {
            filter = generateFilter(config.differentFilters);
        } else {
            filter = generateEquallySpacedOutFilter(nextQueryId, config.overlapRatio);
        }
        AggregationQuery query = new AggregationQuery(
                generateWindow(startTime),
                filter,
                generateAggregation(config.differentAggregations));
        nextQueryId++;
        return query;
    }

    private Window generateWindow(int startTime) {
        int lengthSeconds = random.nextInt(config.maxWindowSizeSeconds - config.minWindowSizeSeconds + 1) + config.minWindowSizeSeconds;
        int slideSeconds = random.nextInt(lengthSeconds) + 1;
        return new Window(startTime, lengthSeconds, slideSeconds);
    }

    private Filter generateFilter(int numberOfDifferentFilters) {
        int seed = random.nextInt(numberOfDifferentFilters);
        return generateFilterFromSeed(seed);
    }

    private Filter generateEquallySpacedOutFilter(int queryId, double overlapPercentage) {
        int filterId = queryId % config.maxParallelism;

        // How many queries overlap at each point
        int overlap = (int) ((config.maxParallelism - 1) * overlapPercentage / (double) config.fields);

        // distribute query filters over all columns. Compute the index of the filter inside it's column
        int column = filterId % config.fields;
        int idInColumn = (filterId - column) / config.fields;

        int filtersPerColumn = config.maxParallelism / config.fields + 1;
        int min = config.minFieldValues.get(column);
        int max = config.maxFieldValues.get(column);


        double idealStepSize = ((max - min) / (double) filtersPerColumn);
        double idealFilterSize = (overlap + 1) * idealStepSize;

        int steps = idInColumn % (overlap + 1);
        int fullFilterSizeSteps = (idInColumn - steps) / (overlap + 1);

        int low = min + (int)(idealStepSize * steps) + (int)(idealFilterSize * fullFilterSizeSteps);
        int high = min + (int)(idealStepSize * steps) + (int)(idealFilterSize * (fullFilterSizeSteps + 1)) - 1;

        if (fullFilterSizeSteps == 0) {
            low = min;
        }
        if (high > max) {
            high = max;
        }
        return new Range(column, low, high);
    }

    private Filter generateFilterFromSeed(int seed) {
        reseededRandom.setSeed(seed + 42);
        int column = reseededRandom.nextInt(config.fields);
        int comparisonValue = reseededRandom.nextInt(config.maxFieldValues.get(column) - config.minFieldValues.get(column)) + config.minFieldValues.get(column);

        Supplier<Filter>[] suppliers = new Supplier[]{
                () -> new Less(column, comparisonValue),
                () -> new LessOrEqual(column, comparisonValue),
                () -> new More(column, comparisonValue),
                () -> new MoreOrEqual(column, comparisonValue),
                () -> new Equal(column, comparisonValue)
        };
        return suppliers[reseededRandom.nextInt(suppliers.length)].get();
    }

    private AggregationFunction generateAggregation(int numberOfDifferentAggregations) {
        int id = random.nextInt(numberOfDifferentAggregations);
        return aggregationFunctionFromId(id);
    }

    private AggregationFunction aggregationFunctionFromId(int id) {
        int column = id % config.fields;
        int aggregationId = (id / config.fields) % config.aggregationNames.size();
        String aggregationName = config.aggregationNames.get(aggregationId);
        switch (aggregationName) {
            case "min":
                return new Min(column);
            case "max":
                return new Max(column);
            case "count":
                return new Count(column);
            case "sum":
                return new Sum(column);
            case "avg":
                return new Average(column);
            case "avg_shared":
                return new AverageReusingSumAndCount(column);
            default:
                throw new IllegalArgumentException("Unknown aggregation name: " + aggregationName);
        }
    }
}

