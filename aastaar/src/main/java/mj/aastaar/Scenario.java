package mj.aastaar;

import mj.aastaar.algorithms.PathFindingAlgorithm;
import mj.aastaar.map.Grid;
import mj.aastaar.map.MapCreator;
import mj.aastaar.map.Node;

/**
 * Pathfinding scenario, containing starting and goal positions,
 * pathfinding grid based on a game map and a method for running pathfinding
 * algorithms and retrieving their shortest paths.
 * 
 * @author MJ
 */
public class Scenario {

    private Grid grid;
    private Node start;
    private Node goal;
    private Node[][] shortestPaths;
    private String[] pathColors;

    /**
     * @return Grid
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * @return Grid as character array
     */
    public char[][] getGrid2D() {
        return grid.getGrid2D();
    }

    /**
     * @return Start node
     */
    public Node getStart() {
        return start;
    }

    /**
     * @return Goal node
     */
    public Node getGoal() {
        return goal;
    }

    /**
     * @return Array of shortest paths (as node arrays)
     */
    public Node[][] getShortestPaths() {
        return shortestPaths;
    }

    /**
     * @return Color hexadecimal representations for shortest path visualization
     */
    public String[] getPathColors() {
        return pathColors;
    }

    /**
     * @param grid Grid object
     */
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    /**
     * @param start Start node
     */
    public void setStart(Node start) {
        this.start = start;
    }

    /**
     * @param goal Goal node
     */
    public void setGoal(Node goal) {
        this.goal = goal;
    }

    /**
     * @param shortestPaths Array of shortest paths (as node arrays)
     */
    public void setShortestPaths(Node[][] shortestPaths) {
        this.shortestPaths = shortestPaths;
    }

    /**
     * @param pathColors Colors as hex strings for shortest path visualization
     */
    public void setPathColors(String[] pathColors) {
        this.pathColors = pathColors;
    }

    /**
     * Checking if the current starting point is a valid location on the map.
     *
     * @return True if node is in bounds and passable, otherwise false
     */
    public boolean startNodeIsValid() {
        int x = start.getX();
        int y = start.getY();
        int gridLength = grid.getLength();
        if (x >= 0 && y >= 0 && x < gridLength && y < gridLength) {
            char startChar = grid.getGrid2D()[x][y];
            if (grid.isPassable(startChar)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Running the specified algorithm,
     * printing the shortest path length and cost
     * and adding the shortest path (array of nodes) for visualization.
     *
     * @param algorithm Object implementing the PathFindingAlgorithm interface
     * @param name What the algorithm is called
     * @param i Index for shortestPaths and pathColors arrays
     * @param color Color as hex strings for shortest path visualization
     */
    public void runPathfindingAlgorithm(PathFindingAlgorithm algorithm, String name, int i, String color) {
        if (startNodeIsValid()) {
            System.out.println("Starting " + name);
            int pathLength = algorithm.search(grid, start, goal, 4);
            System.out.print(name + " shortest path length: " + pathLength);
            System.out.println(", cost: " + algorithm.getCost(goal));
            shortestPaths[i] = algorithm.getPath().shortestPath(goal, start, pathLength);
            System.out.println("Retrieved " + name + " shortest path as array \n");
            pathColors[i] = color;
        } else {
            System.out.println("The starting position is not valid");
        }
    }

    /**
     * Create the pathfinding grid based on a specific map.
     * Work in progress: no ability to read configuration files yet.
     *
     * @param configFilePath Path to file containing configurations.
     */
    public void initConfig(String configFilePath) {
        if (configFilePath == null) {
            initDefaultGrid();
            initDefaultPositions();
        }
    }

    /**
     * Initializing an example map due to the lack of a configuration file.
     * Initializing grid representation of the map, along with
     * character representations of map nodes that are marked impassable for
     * pathfinding, and the penalty for moving through heavier terrain,
     * in this case "shallow water".
     * NOTE: The Warcraft 3 maps (wc3maps512-map/*) contain shallow water.
     */
    private void initDefaultGrid() {
        MapCreator mapCreator = new MapCreator();
        //mapCreator.createMapFromFile("mapdata/sc1-map/Aftershock.map");
        //mapCreator.createMapFromFile("mapdata/wc3maps512-map/tranquilpaths.map");
        mapCreator.createMapFromFile("mapdata/wc3maps512-map/divideandconquer.map");
        //mapCreator.createMapFromFile("mapdata/wc3maps512-map/timbermawhold.map");
        if (mapCreator.getGrid() != null) {
            char[][] gridArray = mapCreator.getGrid();
            char[] impassable = {'T', 'W', '@'};
            double edgeWeight = 2.0;
            grid = new Grid(gridArray, impassable, edgeWeight);
        }
    }

    /**
     * Initializing example starting and goal coordinates
     * due to the lack of a configuration file.
     */
    private void initDefaultPositions() {
        // Aftershock
//        int startX = 82;
//        int startY = 203;
//        int goalX = 400;
//        int goalY = 390;
        // arena2
//        int startX = 84;
//        int startY = 106;
//        int goalX = 77;
//        int goalY = 85;
        // arena2 other
//        int startX = 104;
//        int startY = 44;
//        int goalX = 70;
//        int goalY = 190;
        // divideandconquer
        int startX = 95;
        int startY = 270;
        int goalX = 420;
        int goalY = 440;
        // timbermawhold
//        int startX = 70;
//        int startY = 380;
//        int goalX = 394;
//        int goalY = 101;

        start = new Node(startX, startY, 0.0);
        goal = new Node(goalX, goalY, 0.0);
    }
}
