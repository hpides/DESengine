# mt-flink

This repository contains the main code for the `mt-flink` project.

The original local project directory was very large, so several files and directories have been removed before uploading this repository. The uploaded version keeps the main code, but excludes large dependencies, datasets, local outputs, and plotting utilities.



## Removed Files and Directories

The following files and directories were removed from the original local version before uploading:

```text
flink-1.20.2/
```

This directory contained the Apache Flink version used to run the experiments on the cluster.

```text
out/
```

This directory contained outputs and metrics from previous local runs.

```text
data/full-game.csv
```

This file contained the dataset used for the experiments. The dataset can be downloaded separately from:

```text
http://www2.iis.fraunhofer.de/sports-analytics/full-game.gz
```

The raw outputs from the cluster experiments were also removed.