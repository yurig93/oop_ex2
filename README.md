- [What does this tool do?](#what-does-this-tool-do-)
    + [Requirements](#requirements)
    + [Usage](#usage)
  * [Features](#features)
    + [Graph features](#graph-features)
    + [Game features](#game-features)
    + [Visual features](#visual-features)
  * [Performance](#performance)

## What does this tool do?
This repo contains code and examples for multiple strategies for managing a cluster of agents, pointing the to points 
of interest on a directed graph.

### Requirements
- OpenJDK 12 or higher.
- Unix based machine (optional).

### Usage
First clone this repo. Then you can either run the jar in `out/Ex2.jar` or compile `src/gameClient/Ex2.java` 
Provide the jar your ID as the first argument and optionally the scenario ID as the second argument.

```sh
# git clone this repository
$ git clone https://github.com/yurig93/oop_ex2.git
$ cd oop_ex2

# run the jar
$ java -jar out/Ex2.jar 1234 1
```

## Features
### Graph features
This repo contains an implementation for a direct graph under `src/api`.
It also contains a set of graph algorithms to be used found in `DWGraph_Algo` class.
- Save / Load graph.
- Check if graph is strongly conncted using Tarjan's algorithm.
- Find the shortest path between nodes (Dijkstra).
- Calculate the weight between two nodes.

### Game features
This repo contains a couple of strategy implementations which can be found at `src/gameClient/strategy`  
Each strategy implments a interface which helps the main game engine `GameEngine` generate move command for the agent.
- `BaseStrategy` - Contains basic logic and serves as a skeleton. It does not contains move deciding logic.
- `SimpleStrategy` - Aims to send the closest agent free to a pokemon (based on sum of edge weights).
- `HeatmapStrategy` (Default) - Aims to spread the agents as best as it can across the whole map (avoids clustering) by setting heat borders (best effort). Falls back to `SimpleStrategy` if not possible.
- `MapDistanceStrategy` - Aims to send the closest agent free to a pokemon based on XY positions instead of edge weight.


### Visual features
The implementation visualizes the graph on the screen. It shows the moving agents and pokemons on the map along with 
additional metadata. It also contains controls for setting the scenario and the strategy.
`Gray` dots are agents with their ID and score above. `Green` dots are non targeted pokemons. `Red` dots are targeted pokemons.
If `HeatmapStrategy` is chosen, then it will also visualize the borders.

![Alt Text](https://media.giphy.com/media/vPTsO4fPUolruaw5Nq/giphy.gif)


## Performance
Please visit the wiki :)