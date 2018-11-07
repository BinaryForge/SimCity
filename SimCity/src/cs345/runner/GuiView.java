/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.*;
import cs345.model.Cell;
import cs345.model.cell.*;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * The JavaFX application. Run the GUI for the game.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class GuiView implements Cs345Opolis.ModelListener {

    /* Names for the properties for the icons for the tools. */
    public static final String RES_TOOL_ICON = "cs345opolis.image.restoolicon";
    public static final String IND_TOOL_ICON = "cs345opolis.image.indtoolicon";
    public static final String BULLDOZE_ICON = "cs345opolis.image.bulldozeicon";
    public static final String ROAD_TOOL_ICON = "cs345opolis.image.roadtoolicon";

    /* The associated GuiRunner and model objects. */
    private GuiRunner parent;
    private Cs345Opolis model;

    /* The JavaFX primaryStage for the display. */
    private Stage primaryStage;

    /* The BorderPane that is the game. */
    @FXML private Pane gamePane;

    /* Menu items for adding actions. */
    @FXML private MenuItem newGridMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private MenuItem textInputMenuItem;
    @FXML private MenuItem runMenuItem;
    @FXML private MenuItem runFastMenuItem;
    @FXML private MenuItem runVeryFastMenuItem;
    @FXML private MenuItem runSlowMenuItem;
    @FXML private MenuItem stopMenuItem;

    /* Texts for date, population, and industry */
    @FXML private Text dateValue;
    @FXML private Text populationValue;
    @FXML private Text industryValue;

    /* Toggle group for the tool buttons. */
    @FXML private ToggleGroup toolButtons;

    /* Tools for editing grid */
    @FXML private ToggleButton resTool;
    @FXML private ToggleButton indTool;
    @FXML private ToggleButton bulldozeTool;
    @FXML private ToggleButton roadTool;

    /* The Text object containing the message output. */
    @FXML private Text messageArea;

    /* The area for user input. */
    @FXML private TextField inputField = null;

    /* Components that display the grid. */
    @FXML private Pane gridContainer;
    @FXML private GridPane gridPane;

    /* Properties for tracking model attributes. */
    IntegerProperty resPopProperty = new SimpleIntegerProperty(0);
    IntegerProperty indPopProperty = new SimpleIntegerProperty(0);
    ObjectProperty<SimulatorTime> curTimeProperty = new SimpleObjectProperty<SimulatorTime>();

    /* The GridDisplay object for this view. */
    private GridDisplay gridDisplay;

    /* Is the newGridCommand Enabled? */
    private BooleanProperty newGridDisabled;

    BooleanProperty getNewGridDisabled() {
        return newGridDisabled;
    }

    /**
     * Create a GuiView object.
     */
    GuiView(GuiRunner runner) {
        this.parent = runner;
        this.model = parent.getModel();
    }

    /* Initialize the window for the game. */
    void initView(Stage stage) {
        primaryStage = stage;

        // Setup the window.
        Scene scene = new Scene(gamePane, 600,400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CS345Opolis");

        setupMenus();
        setupFields();
        setupTools();
        setupInputField();

        // Setup the GridDisplay for this view
        gridDisplay = new GridDisplay(parent, gridContainer, gridPane);

        // Add this view as a listener for changes to the model
        model.addListener(this);

        // Display the window
        primaryStage.show();
    }

    /* Setup the menu bar. The menu bar node is returned. */
    private void setupMenus() {
        exitMenuItem.setOnAction(e -> runCommand(new QuitCommand(parent)));

        newGridMenuItem.setOnAction(e -> runCommand(new NewGridCommand(parent, false)));
        newGridDisabled = newGridMenuItem.disableProperty();
        newGridDisabled.set(true);

        textInputMenuItem.setOnAction(e -> inputField.setVisible(!inputField.isVisible()));

        runMenuItem.setOnAction(e -> runCommand(new RunCommand(parent, RunCommand.StepRate.NORMAL)));
        runFastMenuItem.setOnAction(e -> runCommand(new RunCommand(parent, RunCommand.StepRate.FAST)));
        runVeryFastMenuItem.setOnAction(e -> runCommand(new RunCommand(parent, RunCommand.StepRate.VERYFAST)));
        runSlowMenuItem.setOnAction(e -> runCommand(new RunCommand(parent, RunCommand.StepRate.SLOW)));
        stopMenuItem.setOnAction(e -> runCommand(new StopCommand(parent)));
    }

    /* Helper for running Command objects. */
    private void runCommand(Command cmd) {
        parent.runCommand(cmd);
    }

    /* Setup for the date, population, and industry fields. */
    private void setupFields() {
        // Bind the dateValue, populationValue, and industryValue to the
        // corresponding fields in the model.
        curTimeProperty.set(model.getCurrentTime());
        dateValue.textProperty().bind(curTimeProperty.asString());
        populationValue.textProperty().bind(resPopProperty.asString());
        industryValue.textProperty().bind(indPopProperty.asString());
    }

    /* Set up the tool buttons. */
    private void setupTools() {
        resTool.setOnAction(e -> zoneAction(e, "residential"));
        indTool.setOnAction(e -> zoneAction(e, "industrial"));
        bulldozeTool.setOnAction(this::bulldozeAction);
        roadTool.setOnAction(this::roadAction);
    }

    private void zoneAction(ActionEvent event, String zoneType) {
        ToggleButton theButton = (ToggleButton)event.getSource();
        if (theButton.isSelected()) {
            gridDisplay.setTool(new ZoneTool(parent, gridDisplay, zoneType));
        } else if (toolButtons.getSelectedToggle() == null) {
            gridDisplay.clearTool();
        }
        // System.out.printf("%s active: %s, source: %s target: %s%n",
        //     zoneType, theButton.isSelected(), event.getSource(), event.getTarget());
    }

    private void bulldozeAction(ActionEvent event) {
        ToggleButton theButton = (ToggleButton)event.getSource();
        if (theButton.isSelected()) {
            gridDisplay.setTool(new BulldozeTool(parent, gridDisplay));
        } else if (toolButtons.getSelectedToggle() == null) {
            gridDisplay.clearTool();
        }
        // System.out.printf("bulldoze active: %s, source: %s target: %s%n",
        //     theButton.isSelected(), event.getSource(), event.getTarget());
    }
   
    //A method that creates and activates a new road tool. 
    private void roadAction(ActionEvent event) {
        ToggleButton theButton = (ToggleButton)event.getSource();
        if (theButton.isSelected()) {
            gridDisplay.setTool(new RoadTool(parent, gridDisplay));
        } else if (toolButtons.getSelectedToggle() == null) {
            gridDisplay.clearTool();
        }
        // System.out.printf("road active: %s, source: %s target: %s%n",
        //     theButton.isSelected(), event.getSource(), event.getTarget());
    }
   
    private void setupInputField() {
        inputField.setOnAction(
                event -> {
                    // Action to be taken when user pressed enter on keyboard
                    handleInput(inputField.getText());
                    inputField.setText("");
                }
        );
        // inputField only appears in window when the field is visible
        inputField.managedProperty().bind(inputField.visibleProperty());
    }

    /**
     * Output a message. format and args are printf-like parameters. A message
     * takes a single line on the output
     * @param format the format of the message
     * @param args the arguments for the format
     */
    public void message(String format, Object... args) {
        String text = String.format(format, args);
        messageArea.setText(messageArea.getText() + "\n" + text);
    }

    /**
     * Handle user input.
     *
     * @param input the user's input
     */
    private void handleInput(String input) {
        String line = input.trim();
        if (line.length() != 0) {
            String[] words = line.split("\\s+");
            parent.runCommand(words);
        }
    }

    /**
     * Called when one or more cells in the grid have changed type. This method
     * creates new cell display objects for the grid cells that have changed.
     *
     * @param rect the rectangle for the changed region
     */
    @Override
    public void gridChanged(GridRectangle rect) {
        GridRectangle zoneRect; // Declaration for later use
        Grid grid = model.getGrid();
        for (int x = rect.x; x < rect.x + rect.w; x++) {
            for (int y = rect.y; y < rect.y + rect.h; y++) {
                Cell cell = grid.cellAt(x, y);
                CellType cellType = cell.getCellType();
                switch (cellType) {
                    case DIRT:
                        new DirtDisplay(gridDisplay, (Dirt)cell);
                        break;
                    case RIVER:
                        new RiverDisplay(gridDisplay, (River)cell);
                        break;
                    case WOODS:
                        new WoodsDisplay(gridDisplay, (Woods)cell);
                        break;
                    case ROAD:
                        new RoadDisplay(gridDisplay, (Road)cell);      
                        break;
                    case RESIDENTIAL:
                        // Only build a new Zone if the given location is
                        // the upper left hand corner of the zone.
                        Residential resCell = (Residential) cell;
                        zoneRect = resCell.getRectangle();
                        if (zoneRect.x == x && zoneRect.y == y) {
                            new ResidentialDisplay(gridDisplay, resCell);
                        }
                        break;
                    case INDUSTRIAL:
                        // Only build a new Zone if the given location is
                        // the upper left hand corner of the zone.
                        Industrial indCell = (Industrial) cell;
                        zoneRect = indCell.getRectangle();
                        if (zoneRect.x == x && zoneRect.y == y) {
                           new IndustrialDisplay(gridDisplay, indCell);
                        }
                        break;
                    default:

                }
            }
        }
    }

    /**
     * Called when the time in the model changes
     */
    @Override
    public void timeChanged() {
        curTimeProperty.set(model.getCurrentTime());
    }

    /**
     * Call when the population and or industry changes
     */
    @Override
    public void censusChanged() {
        resPopProperty.set(model.curResPop);
        indPopProperty.set(model.curIndCount);
    }
}
