package mj.aastaar;

import java.math.BigDecimal;
import java.math.MathContext;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mj.aastaar.algorithms.AStar;
import mj.aastaar.algorithms.PathfindingAlgorithm;
import mj.aastaar.algorithms.UniformCostSearch;
import mj.aastaar.map.Grid;
import mj.aastaar.map.Node;
import mj.aastaar.utils.PathfindingPerformanceTester;

/**
 * Initializing a pathfinding scenario, and a Java FX graphical user interface
 * for visualizing pathfinding maps and algorithms.
 *
 * @author MJ
 */
public class Main extends Application {

    private static Scenario scenario;
    private GraphicsContext gc;
    private String showExplored;

    /**
     * The main program.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        run();
    }

    /**
     * Initializing the scenario from configurations, providing the scenario
     * with algorithms to run and providing arrays for the algorithm's shortest
     * paths. Launching the Java FX GUI and invoking performance tests.
     */
    private static void run() {
        scenario = new Scenario();
        scenario.initConfig();
        Grid grid = scenario.getGrid();

        if (scenario.getStart() == null || scenario.getGoal() == null) {
            System.out.println("Error initializing start and goal positions");
        } else if (grid == null || grid.getGrid2D() == null || grid.getLength() < 1) {
            System.out.println("Error creating a pathfinding grid");
        } else {
            String cyan = "#00FFFF";
            String magenta = "#FF00FF";
            String[] pathColors = {cyan, magenta};
            scenario.setPathColors(pathColors);
            scenario.setShortestPaths(new Node[pathColors.length][]);

            PathfindingAlgorithm[] algorithms = {new UniformCostSearch(grid), new AStar(grid)};
            scenario.setAlgorithms(algorithms);
            String[] algoNames = {"Dijkstra", "A*"};
            scenario.setAlgoNames(algoNames);
            for (int i = 0; i < algorithms.length; i++) {
                scenario.runPathfindingAlgorithm(algorithms[i], algoNames[i], i);
            }

            System.out.println("Launching visualization. Closing the window will begin performance testing.\n");
            launch(Main.class);

            //NOTE: DISABLED FOR UI DEVELOPMENT
//            runPerformanceTests(algorithms, algoNames);
        }
    }

    /**
     * Using the performance tester class to test pathfinding speed. Setting the
     * number n, where n is the number of times the tests are run.
     *
     * @param algorithms The algorithms that are tested
     * @param algoNames The names of the algorithms that are
     */
    private static void runPerformanceTests(PathfindingAlgorithm[] algorithms, String[] algoNames) {
//        int[] nums = {10, 50, 100, 500, 1000};
        int[] nums = {10, 10, 20, 30, 50};
        PathfindingPerformanceTester tester = new PathfindingPerformanceTester(scenario);
        System.out.print("Beginning performance tests on the algorithms.\n");
        long t = System.nanoTime();
        tester.run(algorithms, algoNames, nums);
        BigDecimal elapsedTime = new BigDecimal((System.nanoTime() - t) / 1000000000);
        System.out.println(tester);
        System.out.println("Performance tests ran in a total of "
                + elapsedTime.round(new MathContext(3)) + " seconds.\n");
    }

    @Override
    public void start(Stage window) throws Exception {
        double tileSize = 2.0;
        Grid grid = scenario.getGrid();

        Canvas canvas = new Canvas(grid.getLength() * tileSize, grid.getRowLength() * tileSize);
        gc = canvas.getGraphicsContext2D();

        Pane layout = tilePane(tileSize);
        ScrollPane scrollPane = new ScrollPane(layout);
        ToolBar toolbar = toolBar(tileSize);
        BorderPane bp = new BorderPane(canvas);
        bp.setRight(toolbar);

        Group root = new Group();
        root.getChildren().addAll(scrollPane, bp);
        Scene scene = new Scene(root);

        window.setScene(scene);
        window.setTitle("Pathfinding visualization on game maps");
        window.show();
    }

    /**
     * Creating the grid visualization with JavaFX objects.
     *
     * NOTE: GridPane.add receives coordinates as (..., column, row)
     *
     * @return grid of colored rectangles, a.k.a. tiles, representing the map
     * and shortest paths
     */
//    private GridPane gridGUI(double tileSize) {
//        GridPane layout = new GridPane();
//        char[][] grid2D = scenario.getGrid2D();
//        
//        addTiles(grid2D, layout, tileSize);
//        colorStartAndGoal(tileSize, Color.RED, Color.LAWNGREEN);
////        colorExplored(layout, tileSize);
//        colorPaths(tileSize);
//        
//        return layout;
//    }
    private Pane tilePane(double tileSize) {
        Pane layout = new Pane();
        char[][] grid2D = scenario.getGrid2D();
        Canvas tileCanvas = tileCanvas(grid2D, tileSize);
        layout.getChildren().add(tileCanvas);

        colorStartAndGoal(tileSize, Color.RED, Color.LAWNGREEN);
        colorPaths(tileSize);

        return layout;
    }

    private ToolBar toolBar(double tileSize) {
        int fontSize = 16;
        ToolBar toolBar = new ToolBar();
        toolBar.setOrientation(Orientation.VERTICAL);
        toolBar.setPadding(new Insets(20));
        toolBar.setBackground(new Background(new BackgroundFill(Color.web("#130d14"), CornerRadii.EMPTY, Insets.EMPTY)));
        Button randomPositionsButton = new Button("New random positions");

        randomPositionsButton.setOnAction(value -> {
            clickRandomPositions(tileSize);
        });

        Label exploredLabel = new Label("Visualize explored nodes: ");
        exploredLabel.setTextFill(Color.WHITE);
        exploredLabel.setFont(new Font(fontSize));
        final String[] exploredCoices = {"None", scenario.getAlgoNames()[0], scenario.getAlgoNames()[1]};
        ChoiceBox exploredBox = new ChoiceBox(FXCollections.observableArrayList(exploredCoices));
        exploredBox.setValue("None");

        exploredBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue ov, Number value, Number new_value) {
                clearExplored(tileSize);
                showExplored = exploredCoices[new_value.intValue()];
                if (!showExplored.equals("None")) {
                    colorExplored(tileSize);
                }
                colorStartAndGoal(tileSize, Color.RED, Color.LAWNGREEN);
                colorPaths(tileSize);
            }
        });

        Label colorsLabel = new Label("Algorithms used: ");
        colorsLabel.setTextFill(Color.WHITE);
        colorsLabel.setFont(new Font(fontSize));
        toolBar.getItems().add(colorsLabel);
        
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setPadding(new Insets(10));
        separator.setOpacity(0.5);
        Separator separator2 = new Separator(Orientation.VERTICAL);
        separator2.setPadding(new Insets(10));
        separator2.setOpacity(0.5);

        for (int i = 0; i < scenario.getAlgorithms().length; i++) {
            Text colorsText = new Text(scenario.getAlgoNames()[i]);
            colorsText.setFill(Color.web(scenario.getPathColors()[i]));
            colorsText.setFont(Font.font(fontSize));
            toolBar.getItems().add(colorsText);
        }

        toolBar.getItems().addAll(separator, randomPositionsButton);
        toolBar.getItems().addAll(separator2, exploredLabel, exploredBox);
        return toolBar;
    }

    private void clickRandomPositions(double tileSize) {
        clearStartAndGoalColors(tileSize);
        clearPaths(tileSize);
        clearExplored(tileSize);
        scenario.initRandomPositions();
        PathfindingAlgorithm[] algorithms = scenario.getAlgorithms();
        String[] algoNames = scenario.getAlgoNames();
        for (int i = 0; i < algorithms.length; i++) {
            scenario.runPathfindingAlgorithm(algorithms[i], algoNames[i], i);
        }

        if (showExplored != null && !showExplored.equals("None")) {
            colorExplored(tileSize);
        }
        colorStartAndGoal(tileSize, Color.RED, Color.LAWNGREEN);
        colorPaths(tileSize);
    }

    private void colorStartAndGoal(double tileSize, Color startColor, Color goalColor) {
        Node start = scenario.getStart();
        Node goal = scenario.getGoal();
        gc.setFill(startColor);
        gc.fillRect((int) (start.getY() * tileSize), (int) (start.getX() * tileSize), tileSize, tileSize);
        gc.setFill(goalColor);
        gc.fillRect((int) (goal.getY() * tileSize), (int) (goal.getX() * tileSize), tileSize, tileSize);
    }

    private void clearStartAndGoalColors(double tileSize) {
        Node start = scenario.getStart();
        Node goal = scenario.getGoal();
        gc.clearRect((int) (start.getY() * tileSize), (int) (start.getX() * tileSize), tileSize, tileSize);
        gc.clearRect((int) (goal.getY() * tileSize), (int) (goal.getX() * tileSize), tileSize, tileSize);
    }

    /**
     * Adding map tiles to the GridPane.
     *
     * @param grid2D 2D character array representation of the map grid
     * @param layout JavaFX GridPane object
     * @param tileSize Pixel dimensions for each tile
     */
//    private void addTiles(char[][] grid2D, GridPane layout, double tileSize) {
//        for (int i = 0; i < grid2D.length - 1; i++) {
//            for (int j = 0; j < grid2D[i].length - 1; j++) {
//                layout.add(new Rectangle(tileSize, tileSize, tileColor(grid2D[i][j])), j, i);
//            }
//        }
//    }
    // coordinates -1 hack to fix offset between canvases
    private Canvas tileCanvas(char[][] grid2D, double tileSize) {
        Canvas tileCanvas = new Canvas(grid2D.length * tileSize, grid2D[0].length * tileSize);
        GraphicsContext tileGC = tileCanvas.getGraphicsContext2D();
        for (int i = 0; i < grid2D.length - 1; i++) {
            for (int j = 0; j < grid2D[i].length - 1; j++) {
                tileGC.setFill(tileColor(grid2D[i][j]));
                tileGC.fillRect((int) (j * tileSize) - 1, (int) (i * tileSize) - 1, tileSize, tileSize);
            }
        }
        return tileCanvas;
    }

    /**
     * Coloring different paths found by different algorithms.
     *
     * @param layout JavaFX GridPane object
     * @param tileSize Pixel dimensions for each tile
     */
    private void colorPaths(double tileSize) {
        Node[][] shortestPaths = scenario.getShortestPaths();
        String[] pathColors = scenario.getPathColors();
        for (int i = 0; i < shortestPaths.length; i++) {
            Node[] path = shortestPaths[i];
            if (path == null) {
                continue;
            }
            gc.setFill(Color.web(pathColors[i]));
            for (int j = 0; j < path.length - 1; j++) {
                gc.fillRect((int) (path[j].getY() * tileSize), (int) (path[j].getX() * tileSize), tileSize, tileSize);
            }
        }
    }

    private void clearPaths(double tileSize) {
        Node[][] shortestPaths = scenario.getShortestPaths();
        for (int i = 0; i < shortestPaths.length; i++) {
            Node[] path = shortestPaths[i];
            if (path == null) {
                continue;
            }
            for (int j = 0; j < path.length - 1; j++) {
                gc.clearRect((int) (path[j].getY() * tileSize), (int) (path[j].getX() * tileSize), tileSize, tileSize);
            }
        }
    }

    private void colorExplored(double tileSize) {
        Node[][] cameFrom = scenario.getCameFrom(showExplored);
        for (int i = 0; i < cameFrom.length; i++) {
            Node[] nodes = cameFrom[i];
            if (nodes == null) {
                continue;
            }
            for (int j = 0; j < nodes.length - 1; j++) {
                if (nodes[j] == null) {
                    continue;
                }

                gc.setFill(Color.web("#C5C3DA"));
                gc.fillRect((int) (nodes[j].getY() * tileSize), (int) (nodes[j].getX() * tileSize), tileSize, tileSize);
            }
        }
    }

    private void clearExplored(double tileSize) {
        Node[][] cameFrom = scenario.getCameFrom(showExplored);
        for (int i = 0; i < cameFrom.length; i++) {
            Node[] nodes = cameFrom[i];
            if (nodes == null) {
                continue;
            }
            for (int j = 0; j < nodes.length - 1; j++) {
                if (nodes[j] == null) {
                    continue;
                }
                gc.clearRect((int) (nodes[j].getY() * tileSize), (int) (nodes[j].getX() * tileSize), tileSize, tileSize);
            }
        }
    }

    /**
     * Determine the color for a map tile.
     *
     * @param c Character representation of the map grid node
     * @return JavaFX Color object
     */
    private Color tileColor(char c) {
        Color color = Color.RED;
        switch (c) {
            case '.':
                color = Color.web("#c49858");
                break;
            case 'T':
                color = Color.web("#005c32");
                break;
            case '@':
                color = Color.web("#130d14");
                break;
            case 'W':
                color = Color.web("#066b97");
                break;
            case 'S':
                color = Color.web("#658278");
                break;
            default:
                break;
        }
        return color;
    }
}
