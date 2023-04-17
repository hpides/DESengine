<h1 align="center">Efficent Window Aggregation in Decentralized Networks </h1>

# Desis

Desis is a hierarchical system that can be deployed into decentralized networks. Desis support tumbling windows, sliding windows, session windows, and user-defined windows.

# Deco

Deco is also a decentralized approach that is based on Desis. Deco can move calcuations of count-based windows from centers to the local nodes.

# Approach Introduction:
- Desis
    - decentralized aggregation and share results between windows
- Deco
    - decentralized aggregation and supports count-based windows    
- Scotty
    - implemented by the `Scotty`
- Disco 
    - implemented by the `Disco`
- DesisCen(DeCen)
    - centralized aggregation and can not perform incremental aggregation.
- DesisIC(DeBuket)
    - implemented based on Desis and only can perform incremental aggregation
- DesisSW(DeSW)
    - implemented based on Desis and can partial results between windows have different window types
- DesisMultipleKeys
    - the same as Desis and supports windows with different distinct keys
- DesisQuantile
    - the same as Desis and supports different quantile functions
- FigureScripts
  - the python scripts that draw experiments plots
    
    
# Installation

- **Requirements**: Java 8+
- **Install**
     1. Download DESengine
     2. Compile Project
     3. Set `WINDOWS = true` in `Configuration.java`
     4. Run OverallMainDriverTest.java

# Input

- Desis
  - Node Id: 
    - The id of the node that is deployed with Desis.
  - Query Number: 
    - How many queries are processed simultaneously.
  - Query Modes: 
    - The query mode is to choose the query patter that is set into query generation file.
  - Generator Thread Number: 
    - How many generator threads are initialized. One thread can produce at least 10 million tuples/s.

- Deco
  - Node Id:
    - The id of the node that is deployed with Desis.
  - Query Number:
    - How many queries are processed simultaneously.
  - Query Modes:
    - The query mode is to choose the query patter that is set into query generation file.
  - Generator Thread Number:
    - How many generator threads are initialized. One thread can produce at least 10 million tuples/s.
