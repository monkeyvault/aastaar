package mj.aastaar.algorithms;

import mj.aastaar.map.Grid;
import mj.aastaar.map.Node;

/**
 *
 * @author MJ
 */
public interface PathFindingAlgorithm {
    
    // the main method for running the pathfinding algorithm
    // returns the amount of steps in a shortest path or -1 if not found
    // NOTE: cannot do multiple searches with the same object
    public int search(Grid grid, Node start, Node goal, int directions);
    
    // get the found shortest path
    public Path getPath();
    
    // get the cost of the found shortest path
    public double getCost(Node goal);
}
