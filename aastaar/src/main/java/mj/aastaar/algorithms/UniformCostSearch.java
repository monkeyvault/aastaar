package mj.aastaar.algorithms;

import mj.aastaar.algorithms.frontier.UCSFrontier;
import mj.aastaar.datastructures.CustomPriorityQueue;
import mj.aastaar.map.Grid;

/**
 *
 * @author MJ
 */
public class UniformCostSearch extends BestFirstSearch {

    public UniformCostSearch(Grid grid) {
        super(grid);
    }

    @Override
    public void initFrontier() {
        int nx = grid.getLength();
        int ny = grid.getRowLength();
        CustomPriorityQueue cpq = new CustomPriorityQueue(nx * ny);
        boolean[][] visited = new boolean[nx][ny];
        UCSFrontier frontier = new UCSFrontier(cpq, visited);
        super.frontier = frontier;
    }

}
