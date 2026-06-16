package org.shared.kernel;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.shared.config.Config;
import org.shared.config.ConfigLoader;
import org.shared.config.DataSourceType;
import org.shared.datagenerator.*;
import org.shared.metrics.MetricTuple;
import org.shared.operators.*;
import org.shared.query.QueryGeneratorConfig;
import org.shared.result.QueryResult;
import org.shared.sink.BulkWriterFactory;

import java.util.function.Supplier;

public class Benchmark {
    public static void main(String[] args) throws Exception {
        Config config = ConfigLoader.load(args[0]);
        Configuration flinkConfig = new Configuration();

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(flinkConfig);
        env.setParallelism(config.benchmark.parallelism);

        DataSource dataSource = createDataSource(config);
        DataGenerator dataGenerator = createDataGenerator(dataSource, config);
        QueryGeneratorConfig queryGeneratorConfig = createQueryGeneratorConfig(config, dataSource);
        Supplier<MultiQueryAggregationOperator> operatorFactory = createOperatorFactory(config, dataSource);

        //MultiQueryPipeline pipeline = new MultiQueryPipeline(queryGenerator, operator);
        long firstWindowStartTime = System.nanoTime();
        KeyedMultiQueryPipeline pipeline = new KeyedMultiQueryPipeline(queryGeneratorConfig, operatorFactory, firstWindowStartTime);

        MetricLogger metricLogger = new MetricLogger();
        if (config.benchmark.outputPrefix == "") {
            config.benchmark.outputPrefix = System.currentTimeMillis() + "";
        }
        FileSink<String> resultCsvSink = createCsvSink(getOutputPath(), config);
        FileSink<String> metricCsvSink = createCsvSink(getMetricPath(), config);
        //FileSink<String> resultCsvSink = createCsvSink(getOutputPath(), config);
        //FileSink<String> metricCsvSink = createCsvSink(getMetricPath(), config);

        SingleOutputStreamOperator<QueryResult> resultStream = env.addSource(dataGenerator)
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple>forMonotonousTimestamps().withTimestampAssigner(
                        (tuple, timestamp) -> tuple.getTimestamp()))
                .keyBy(Tuple::getKey)
                .process(pipeline);

        DataStream<MetricTuple> metricStream = resultStream.getSideOutput(MultiQueryPipeline.METRIC_TAG);

        resultStream.map(QueryResult::toCSVLine).sinkTo(resultCsvSink);
        metricStream.process(metricLogger).map(MetricTuple::toCSVLine).sinkTo(metricCsvSink);

        env.execute();
    }

    private static Supplier<MultiQueryAggregationOperator> createOperatorFactory(Config config, DataSource dataSource) {
        return new OperatorFactory(config.benchmark.operator, dataSource.getFieldCount(), config.benchmark.compressBitmaps);
    }

    private static FileSink<String> createCsvSink(Path path, Config config) {
        return FileSink
                .forBulkFormat(
                    path,
                    new BulkWriterFactory()
                )
                .withOutputFileConfig(createOutputFileConfig(config))
                .build();
    }

    private static OutputFileConfig createOutputFileConfig(Config config) {
        return OutputFileConfig
                .builder()
                .withPartPrefix(config.benchmark.outputPrefix)
                .build();
    }

    private static Path getOutputPath() {
        return new Path("out/output");
    }

    private static Path getMetricPath() {
        return new Path("out/metrics");
    }

    private static DataSource createDataSource(Config config) {
        if (config.dataGenerator.dataSource == DataSourceType.Debs2013) {
            return new Debs2013DataSource(config.dataGenerator.debsPath);
        } else {
            return new SyntheticDataSource(config.dataGenerator.syntheticFieldCount, config.dataGenerator.syntheticKeyCount);
        }
    }

    private static DataGenerator createDataGenerator(DataSource dataSource, Config config) {
        return new DataGenerator(dataSource, config.benchmark.maxDurationSeconds, config.benchmark.durationWithoutRateDecrease);
    };

    private static QueryGeneratorConfig createQueryGeneratorConfig(Config config, DataSource dataSource) {
        return new QueryGeneratorConfig(
                dataSource.getFieldCount(),
                dataSource.getMinFieldValues(),
                dataSource.getMaxFieldValues(),
                config.queryGenerator.minWindowLengthSeconds,
                config.queryGenerator.maxWindowLengthSeconds,
                config.queryGenerator.batchInterval,
                config.queryGenerator.startingPerBatch,
                config.queryGenerator.endingPerBatch,
                config.queryGenerator.maxParallelism,
                config.queryGenerator.differentAggregations,
                config.queryGenerator.differentFilters,
                config.benchmark.compressBitmaps,
                config.queryGenerator.filterGenerationMode,
                config.queryGenerator.overlapRatio,
                config.queryGenerator.aggregations
        );
    }
}
