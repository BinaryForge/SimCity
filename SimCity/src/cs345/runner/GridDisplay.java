/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.*;
import cs345.model.cell.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.Properties;

/**
 * Display the Grid for a model.
 *
 * A GridDisplay object manages a GridPane which displays the grid. Two
 * services are performed in this regard:
 *  (1) Provides an interface for GridTools. Mouse events can be passed
 *      to the current GridTool. GridTools must extend the GridTool class. 
 *      * The methods setTool and clearTool are used to set/clear the
 *        GridTool.
 *      * If there is no gridTool, mouse events are ignored. The
 *        handleMouseEvent method is used to pass mouse events to grid tools.
 *      * The method setToolRect and clearToolRect are used to display and
 *        undisplay the rectangle for a tool.
 *  (2) Displays images on the grid. The images come from one of three
 *      images loaded by this class. These are background images,
 *      (dirt, river, woods), residential images, and industrial images.
 *      The methods drawBackground, drawResidential, and drawIndustrial
 *      are used to draw the corresponding class of image on the grid.
 *      Passing the Node returned by one of these classes to removeImage
 *      will remove the image from the grid.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class GridDisplay {

    /* The cell size of the grid. */
    public static final int CELL_SIZE = 16;
    public static final int ZONE_SIZE = 3;
    public static final int ZONE_CELL_SIZE = ZONE_SIZE * CELL_SIZE;

    /* These strings are the name of the images used for the grid
     * in the properties file.
     */
    public static final String BKG_IMG = "cs345opolis.image.backgrounds";
    public static final String RES_IMG = "cs345opolis.image.resimage";
    public static final String IND_IMG = "cs345opolis.image.indimage";

    /* Offsets into the background image for various types of cells. */
    public static final int DIRT_OFFSET = 0;
    public static final int RIVER_OFFSET = 1;
    public static final int WOODS_OFFSET = 2;
    public static final int ROAD_OFFSET = 3;

    /* The global properties. Needed for accessing the images. */
    private Properties props;

    /* The images for the grid. */
    private Image backgroundTiles = null;
    private Image resZones = null;
    private Image indZones = null;
    

    /* The associated GuiRunner for this grid. */
    private final GuiRunner runner;
    /* The associated model object for this grid. */
    private final Cs345Opolis model;
    /* The grid pane that is used to display the grid. */
    private final GridPane grid;
    /* The Group that contains the grid pane. */
    private final Pane gridContainer;

    /* The current GridTool that is in control of mouse movements on the
     * grid. This is null if there is no current tool.
     */
    private GridTool tool = null;

    /* Rectangle displayed for tool. */
    private Rectangle toolRect = null;

    /**
     * Construct a gridDisplay object.
     * @param runner the GuiRunner
     * @param grid the GridPane where the grid is to be displayed
     */
    GridDisplay(GuiRunner runner, Pane gridContainer, GridPane grid) {
        this.runner = runner;
        this.model = runner.getModel();
        this.gridContainer = gridContainer;
        this.grid = grid;
        setUpGrid();
    }

    /**
     * Set up the grid.
     *
     * This method does two things. (1) Sets the grid with the correct number
     * of rows and columns each of the correct size. (2) Retrieves the images
     * needed for the grid display.
     */
    private void setUpGrid() {
        Grid grid = model.getGrid();
        RowConstraints rowConstraints = new RowConstraints(CELL_SIZE);
        for (int row = 0; row < grid.getHeight(); row++) {
            this.grid.getRowConstraints().add(rowConstraints);
        }
        ColumnConstraints colConstraints = new ColumnConstraints(CELL_SIZE);
        for (int col = 0; col < grid.getWidth(); col++) {
            this.grid.getColumnConstraints().add(colConstraints);
        }

        backgroundTiles = runner.loadImage(BKG_IMG);
        resZones = runner.loadImage(RES_IMG);
        indZones = runner.loadImage(IND_IMG);
        

        // Capture the mouse events occurring in the group that overlays the
        // grid pane.
        gridContainer.setOnMouseMoved(event -> handleMouseEvent(event, GridTool::onMouseMoved, false));
        gridContainer.setOnMouseEntered(event -> handleMouseEvent(event, GridTool::onMouseEntered, true));
        gridContainer.setOnMouseExited(event -> handleMouseEvent(event, GridTool::onMouseExited, true));
        gridContainer.setOnMouseClicked(event -> handleMouseEvent(event, GridTool::onMouseClicked, false));
        gridContainer.setOnMousePressed(event -> handleMouseEvent(event, GridTool::onMousePressed, false));
        gridContainer.setOnMouseReleased(event -> handleMouseEvent(event, GridTool::onMouseReleased, true));
        gridContainer.setOnMouseDragged(event -> handleMouseEvent(event, GridTool::onMouseDragged, true));
    }

    /* Internal interface for methods that handle a mouse event on the grid. */
    @FunctionalInterface
    private interface GridToolEventHandler {
        void handleEvent(GridTool tool, GridLocation loc);
    }

    /**
     * Handle a mouse event that occurs on the grid.
     *
     * If there is a GridTool currently active, the mouse coordinates are
     * converted to grid coordinates and the appropriate mouse event handler
     * is called on the grid tool.
     *
     * @param event the JavaFX MouseEvent. The mouse coordinates are here.
     * @param handler the handler for the event
     * @param force if true, the handler will be called even if the mouse is
     *              located outside the grid
     */
    private void handleMouseEvent(MouseEvent event, GridToolEventHandler handler, boolean force) {
        // System.out.printf("%s -> %s (%s, %s)%n",
        //         event.getEventType(), event.getTarget(), event.getX(), event.getY());
        if (tool != null) {
            GridLocation loc = getGridLocation((int)event.getX(), (int)event.getY());
            if (loc != null || force) {
                // Location is within the grid coordinates or event is called anyway
                handler.handleEvent(tool, loc);
            }
        }
    }

    /** Convert pixel coordinates to grid coordinates. This method works with
     * local coordinates (the upper left hand corner of the grid is (0,0).
     *
     * @param x the x coordinate in pixels in local coordinates
     * @param y the y coordiante in pixels in local coordinates
     * @return the GridLocation for the given coordinates or null if the
     *         coordinates are not located in the grid.
     */
    public GridLocation getGridLocation(int x, int y) {
        int col = x / CELL_SIZE;
        int row = y / CELL_SIZE;
        if (model.getGrid().validCoords(col, row)) {
            return new GridLocation(col, row);
        } else {
            return null;
        }
    }

    /** Return the current GridTool.
     *
     * @return the current GridTool.
     */
    public GridTool getTool() {
        return tool;
    }

    /** Set the current GridTool.
     *
     * This will disable the prior tool if required.
     *
     * @param newTool the new GridTool
     */
    public void setTool(GridTool newTool) {
        clearTool();
        tool = newTool;
        tool.enable();
    }

    /** Clear the current GridTool.
     *
     * Disable the current tool if there is one.
     */
    public void clearTool() {
        if (tool != null) {
            tool.disable();
        }
        tool = null;
    }

    /** Set the location and size of the currently displayed tool rectangle.
     *
     * @param rect the JavaFX rectangle object
     * @param gridRect the GridRectangle giving the location and size for the
     *                 rect in grid coordinates
     */
    public void setToolRect(Rectangle rect, GridRectangle gridRect) {
        setToolRect(rect, gridRect.x, gridRect.y, gridRect.w, gridRect.h);
    }

    /** Set the location and size of the currently displayed tool rectangle.
     *
     * @param rect the JavaFX rectangle object
     * @param loc the location of the upper left hand corner for  x in grid
     *            coordinates
     * @param w the width in grid squares of the rectangle
     * @param h the height in grid squares of the rectangle
     */
    public void setToolRect(Rectangle rect, GridLocation loc, int w, int h) {
        setToolRect(rect, loc.x, loc.y, w, h);
    }

    /** Set the location and size of the currently displayed tool rectangle.
     *
     * @param rect the JavaFX rectangle object
     * @param x the x coordinate of the upper left hand corner of the rectangle
     *          in grid coordiantes
     * @param y the y coordinate of the upper left hand corner of the rectangle
     *          in grid coordiantes
     * @param w the width in grid squares of the rectangle
     * @param h the height in grid squares of the rectangle
     */
    public void setToolRect(Rectangle rect, int x, int y, int w, int h) {
        rect.setX(x * CELL_SIZE);
        rect.setY(y * CELL_SIZE);
        rect.setWidth(w * CELL_SIZE + 1);
        rect.setHeight(h * CELL_SIZE + 1);

        if (rect != toolRect) {
            if (toolRect != null) {
                gridContainer.getChildren().remove(toolRect);
            }
            toolRect = rect;
            gridContainer.getChildren().add(rect);
        }
    }

    /** Clear the tool rectangle so that it is no longer displayed. */
    public void clearToolRect() {
        if (toolRect != null) {
            gridContainer.getChildren().remove(toolRect);
        }
        toolRect = null;
    }
    Node drawBackground(int col, int row, int offset) {           
        return drawImage(backgroundTiles, 1, offset, col, row);   
    }

    Node drawResidential(int col, int row, int pop) {
        return drawImage(resZones, ZONE_SIZE, pop, col, row);
    }

    Node drawIndustrial(int col, int row, int count) {
        return drawImage(indZones, ZONE_SIZE, count, col, row);
    }

    private Node drawImage(Image image, int numCells, int offset, int col, int row) {
        ImageView view = new ImageView(image);
        int cellSize = CELL_SIZE * numCells;
        view.setViewport(new Rectangle2D(0, offset * cellSize, cellSize, cellSize));
        grid.add(view, col, row, numCells, numCells);
        return view;
    }

    void removeImage(Node image) {
        grid.getChildren().remove(image);
    }
}
