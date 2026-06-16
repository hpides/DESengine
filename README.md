# Filter-Sliced Aggregation for Ad-hoc Stream Processing

This repository contains the main implementation code for the paper:

**Filter-Sliced Aggregation for Ad-hoc Stream Processing**

## Paper Abstract

Most stream processing systems focus on long-running queries that are executed independently. In practice, many use cases also involve a large number of short-running ad-hoc queries that are continuously created and stopped by users. To support many parallel queries efficiently, computation and resources should be shared between multiple queries.

This paper introduces **filter slicing**, a technique for sharing partial aggregates between aggregation queries that filter on a numerical column using lower and upper bounds. By combining filter slicing with window slicing and aggregation decomposition, the proposed shared aggregation operator can reuse partial aggregates between queries with different windows, filters, and aggregation functions.

The evaluation shows that the operator achieves similar single-query performance compared to existing approaches and significantly outperforms baselines when multiple queries are active simultaneously, with up to **175× higher throughput**.

## Repository Structure

The main implementation code can be found in:

```text
mt-flink-1-20-min/src/main/java/org/shared
```

This directory contains the core Flink-based implementation of the shared aggregation operator and related code.

## Dataset

```text
data/full-game.csv
```

The dataset can be downloaded separately from:

```text
http://www2.iis.fraunhofer.de/sports-analytics/full-game.gz
```

