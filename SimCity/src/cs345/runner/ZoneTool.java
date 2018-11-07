/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;


import cs345.model.Cs345Opolis;
import cs345.model.Grid;
import cs345.model.GridLocation;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * A tool for placing a Zone on the Grid.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class ZoneTool extends GridTool {

    /** Color for a Residential tool. */
    public static final String RESIDENTIAL_COLOR = "green";
    /** Color for an Industrial tools. */
    public static final String INDUSTRIAL_COLOR = "yellow";

    /** The type of zone. */
    final String zoneType;

    private Rectangle toolRect = null;
    private boolean inDisplay = false;
    private GridLocation priorLoc = null;
    private GridLocation pressedLoc = null;

    /* The actual JavaFX color objects for the fill (toolColor) and boundary
     * (strokeColor) of the tool.
     */
    private final Color toolColor;
    private final Color strokeColor;

    /** Construct the zone tool object. */
    ZoneTool(GuiRunner runner, GridDisplay display, String zoneType) {
        super(runner, display);
        this.zoneType = zoneType;

        String toolColorName = getToolColorName(zoneType);
        toolColor = Color.web(toolColorName, 0.5);
        strokeColor = Color.web(toolColorName, 0.75);

        toolRect = new Rectangle(GridDisplay.CELL_SIZE, GridDisplay.CELL_SIZE);
        toolRect.setFill(toolColor);
        toolRect.setStrokeWidth(2.0);
        toolRect.setStroke(strokeColor);
    }

    /* Internal utility to get the color given the type of zone. */
    private String getToolColorName(String zoneType) {
        if (zoneType.equals("residential")) {
            return RESIDENTIAL_COLOR;
        }else if (zoneType.equals("industrial")) {
            return INDUSTRIAL_COLOR;
        }else{
            throw new IllegalArgumentException("Invalid type of zone");
        }
    }

    private void displayRect(GridLocation loc) {
       if (!loc.equals(priorLoc)) {
            if (validCenter(loc)) {
                display.setToolRect(toolRect, loc.x - 1, loc.y - 1, 3, 3);
            } else {
                display.clearToolRect();
            }
            priorLoc = loc;
        }
    }

    private boolean validCenter(GridLocation loc) {
        Grid grid = runner.getModel().getGrid();
        return grid.validCoords(loc.x - 1, loc.y - 1) &&
                grid.validCoords(loc.x + 1, loc.y + 1);
    }

    @Override
    public void disable() {
        inDisplay = false;
        display.clearToolRect();
        priorLoc = null;
    }

    @Override
    public void onMouseMoved(GridLocation loc) {
        // System.out.printf("MouseMoved %s%n", loc);
        if (inDisplay) {
            displayRect(loc);
        }
    }

    @Override
    public void onMouseEntered(GridLocation loc) {
        // System.out.printf("MouseEntered %s%n", loc);
        inDisplay = true;
        if (loc != null) {
            displayRect(loc);
        }
    }

    @Override
    public void onMouseExited(GridLocation loc) {
        // System.out.printf("MouseExited %s%n", loc);
        inDisplay = false;
        display.clearToolRect();
        priorLoc = null;
    }

    @Override
    public void onMouseClicked(GridLocation loc) {
        // System.out.printf("MouseClicked %s%n", loc);
        if (loc != null) {
            if (loc.equals(pressedLoc)) {
                if (validCenter(loc)) {
                    Command cmd = new ZoneCommand(runner, zoneType, loc);
                    runner.runCommand(cmd);
                }
            } else {
                displayRect(loc);
            }
        }
        pressedLoc = null;
    }

    @Override
    public void onMousePressed(GridLocation loc) {
        // System.out.printf("MousePressed %s%n", loc);
        pressedLoc = loc;
    }

    @Override
    public void onMouseDragged(GridLocation loc) {
        // System.out.printf("MouseDragged %s%n", loc);
        if (pressedLoc != null && !pressedLoc.equals(loc)) {
            // Mouse was dragged to a new location
            pressedLoc = null;
        }
    }
}
