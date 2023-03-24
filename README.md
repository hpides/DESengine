<h1 align="center">Efficent Window Aggregation in Decentralized Networks </h1>

# Desis

Desis is a hierarchical system that can be deployed into decentralized networks. Desis support tumbling windows, sliding windows, session windows, and user-defined windows.

# Desis

Deco is also a decentralized approach that is based on Desis. Deco can move calcuations of count-based windows from centers to the local nodes.

# Approach names:
- Desis
    - decentralized aggregation and share results between windows
- Deco
    - decentralized aggregation and supports count-based windows    
- Scotty
    - implemented by the `Scotty`
- Disco 
    - implemented by the `Disco`
- DesisCen
    - centralized aggregation and can not perform incremental aggregation.
- DesisIC
    - implemented based on Desis and only can perform incremental aggregation
- DesisSW
    - implemented based on Desis and can partial results between windows have different window types
- DesisMultipleKeys
    - the same as Desis and supports windows with different distinct keys
- DesisQuantile
    - the same as Desis and supports different quantile functions
    
    
# Installation

- **Requirements**: Java 8+
- **Install**
     1. Download DESengine
     2. Compile Project
     3. Set `WINDOWS = true` in `Configuration.java`
     4. Run OverallMainDriverTest.java


