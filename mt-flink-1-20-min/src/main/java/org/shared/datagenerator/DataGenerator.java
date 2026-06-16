package org.shared.datagenerator;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DataGenerator extends RichSourceFunction<Tuple>  {
    private static final Logger log = LoggerFactory.getLogger(DataGenerator.class);

    private final DataSource dataSource;
    private final long maxDurationSeconds;
    private final long durationWithoutRateDecrease;

    private long startTime;

    public static double tuplesPerSecond = 5_000_000;
    //private double tuplesPerSecond = 10_000_000;

    // how many seconds of tuples can be queued - if the queue is full, the rate will decrease
    private double queueDuration = 8.0;
    // number of tuples that can be stored in queue (calculated from queue duration and rate)
    private int queueCapacity = 0;
    // when the queue is half full for this time, the rate will decrease
    private double quarterFullDuration = 15.0;

    private long currentRateStartTime = 0;
    private long currentRateTupleCount = 0;
    private long lastQueueSizeLog = 0;
    private long quarterFullSince = 0;

    private volatile boolean running = true;

    private BlockingQueue<Tuple> queue;

    public DataGenerator(DataSource dataSource, long maxDurationSeconds, long durationWithoutRateDecrease) {
        this.dataSource = dataSource;
        this.maxDurationSeconds = maxDurationSeconds;
        this.durationWithoutRateDecrease = durationWithoutRateDecrease;
    }

    @Override
    public void run(SourceContext<Tuple> ctx) throws Exception {
        queueCapacity = (int) Math.ceil(queueDuration * tuplesPerSecond);
        queue = new ArrayBlockingQueue<>(queueCapacity);

        Thread producerThread = new Thread(() -> {
            try {
                produceTuples();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "DataGenerator-Producer");
        Thread consumerThread = new Thread(() -> {
            try {
                consumeTuples(ctx);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "DataGenerator-Consumer");

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();
    }

    public void produceTuples() throws Exception {
        dataSource.initialize();
        startTime = System.currentTimeMillis();
        currentRateStartTime = startTime;
        currentRateTupleCount = 0;

        while (running && System.currentTimeMillis() - startTime < maxDurationSeconds * 1e3 && (System.currentTimeMillis() - currentRateStartTime < durationWithoutRateDecrease * 1e3)) {
            if (System.currentTimeMillis() - lastQueueSizeLog >= 1e3) {
                long percentage = (long)((double)queue.size() / queueCapacity * 100.0);

                log.info("queue size: " + queue.size() + " / " + queueCapacity + " (" + percentage + "%)");
                log.info("time since last <25%: " + (System.currentTimeMillis() - quarterFullSince) / 1e3);
                lastQueueSizeLog = System.currentTimeMillis();
            }
            long timeSinceStart = System.currentTimeMillis() - currentRateStartTime;
            double targetTuples = timeSinceStart * tuplesPerSecond * 1e-3;
            currentRateTupleCount++;
            if (currentRateTupleCount > targetTuples) {
                double sleepTimeSeconds = (currentRateTupleCount - targetTuples) / tuplesPerSecond;
                long sleepTimeMillis = Math.round(sleepTimeSeconds * 1000);
                if (sleepTimeMillis > 1) {
                    Thread.sleep(sleepTimeMillis);
                }
            }

            if (queue.size() * 4 < queueCapacity) {
                quarterFullSince = System.currentTimeMillis();
            }

            try {
                Tuple tuple = dataSource.nextTuple();
                tuple.setTimestamp(System.nanoTime());
                if (!queue.offer(tuple) || System.currentTimeMillis() - quarterFullSince > quarterFullDuration * 1e3) {
                    reduceRate();
                }
            } catch (Exception e) {
                running = false;
                dataSource.close();
                throw e;
            }

        }
        running = false;
        dataSource.close();
    }

    public void reduceRate() {
        double currentRateTime = System.currentTimeMillis() - currentRateStartTime;
        double queueFillRate = queue.size() / currentRateTime * 1e3;
        double tupleProcessingRate = tuplesPerSecond - queueFillRate;

        double newRate = tupleProcessingRate - 300;
        newRate = Math.max(Math.min(newRate, tuplesPerSecond * 0.95), tuplesPerSecond * 0.65 - 300);

        log.info("Current Emission Rate: " + tuplesPerSecond);
        log.info("Queue Fill Rate: " + queueFillRate);
        log.info("Tuple Processing Rate: " + tupleProcessingRate);
        tuplesPerSecond = newRate;

        queueCapacity = (int) Math.ceil(queueDuration * tuplesPerSecond);
        queue = new ArrayBlockingQueue<>(queueCapacity);

        log.info("New Rate: " + tuplesPerSecond + " T/s");
        currentRateTupleCount = 0;
        currentRateStartTime = System.currentTimeMillis() + 500;
        quarterFullSince = currentRateStartTime;
    }

    public void consumeTuples(SourceContext<Tuple> ctx) throws Exception {
        while (running) {
            Tuple tuple = queue.poll(2000, TimeUnit.MILLISECONDS); // blocks for 5s if empty
            if (tuple != null) {
                ctx.collect(tuple);
            }
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
