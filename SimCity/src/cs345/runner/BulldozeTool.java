/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.GridLocation;
import cs345.model.GridRectangle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Tool for bulldozing a rectangular region in the grid.
 *
 * This tool can be used for bulldozing multiple regions.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class BulldozeTool extends GridTool {

    public static final String BULLDOZE_COLOR = "saddlebrown";

    private Rectangle toolRect = null;
    private boolean inDisplay = false;
    private GridLocation priorLoc = null;
    private GridLocation pressedLoc = null;

    private Color toolColor = Color.web(BULLDOZE_COLOR, 0.5);
    private Color strokeColor = Color.web(BULLDOZE_COLOR, 0.75);

    /**
     * Construct a Bulldoze tool.
     * @param runner the associated GuiRunner for this tool
     * @param display the GridDisplay object for this tool
     */
    BulldozeTool(GuiRunner runner, GridDisplay display) {
        super(runner, display);
        makeRectangle();
    }

    /**
     * Create a toolRect with the correct colors and border to display the tool.
     */
    private void makeRectangle() {
        toolRect = new Rectangle(GridDisplay.CELL_SIZE, GridDisplay.CELL_SIZE);
        toolRect.setFill(toolColor);
        toolRect.setStrokeWidth(2.0);
        toolRect.setStroke(strokeColor);
    }

    /**
     * Utility routine for displaying the rectangle. This routine determines the
     * size and location of the rectangle before calling the GridDisplay object
     * to display/change the rectangle.
     *
     * Since the displayed rectangle can change size when it is dragged, this
     * routine checks to see if there is a drag in progress (pressedLoc != null).
     * If there is no drag in progress, a 1x1 rectangle is displayed. Otherwise,
     * a w x h rectangle is displayed.
     *
     * @param loc the current location of the mouse in grid coordinates
     */
    private void displayRect(GridLocation loc) {
       if (inDisplay && !loc.equals(priorLoc)) {
           if (pressedLoc == null) {
               display.setToolRect(toolRect, loc, 1, 1);
           } else {
               int rectX = Math.min(loc.x, pressedLoc.x);
               int rectY = Math.min(loc.y, pressedLoc.y);
               int width = Math.abs(loc.x - pressedLoc.x) + 1;
               int height = Math.abs(loc.y - pressedLoc.y) + 1;
               display.setToolRect(toolRect, rectX, rectY, width, height);
           }
           priorLoc = loc;
       }
    }

    /**
     * Clears all the attributes of the tool. Called from GridDisplay when the
     * tool goes inactive.
     */
    @Override
    public void disable() {
        inDisplay = false;
        display.clearToolRect();
        priorLoc = null;
        pressedLoc = null;
    }

    /* For these methods, see comments in GridTool abstract class. */

    /* The mouse was moved, change the location (maybe) of the
     * displayed rectangle.
     */
    @Override
    public void onMouseMoved(GridLocation loc) {
        // System.out.printf("MouseMoved %s%n", loc);
        if (loc != null) {
            displayRect(loc);
        }
    }

    /* The mouse entered the grid. The rectangle is now displayed, assuming
     * it wasn't already displayed.
     */
    @Override
    public void onMouseEntered(GridLocation loc) {
        // System.out.printf("MouseEntered %s%n", loc);
        inDisplay = true;
        if (loc != null) {
            displayRect(loc);
        }
    }

    /* The mouse exited the grid. Display of the rectangle is suppressed. */
    @Override
    public void onMouseExited(GridLocation loc) {
        // System.out.printf("MouseExited %s%n", loc);
        inDisplay = false;
        display.clearToolRect();
        priorLoc = null;
    }

    /* The mouse button was pressed. This begins a drag operation. Note that
     * loc is always non-null for this operation.
     */
    @Override
    public void onMousePressed(GridLocation loc) {
        // System.out.printf("MousePressed %s%n", loc);
        pressedLoc = loc;
        displayRect(loc);
    }

    /* The mouse button was released. If both loc and pressedLoc are non-null,
     * a BulldozeCommand is created for the resulting rectangle. The rectangle
     * will be recreated once the mouse moves to a new grid location.
     */
    @Override
    public void onMouseReleased(GridLocation loc) {
        // System.out.printf("MouseReleased %s%n", loc);
        if (loc != null && pressedLoc != null) {
            int rectX = Math.min(loc.x, pressedLoc.x);
            int rectY = Math.min(loc.y, pressedLoc.y);
            int width = Math.abs(loc.x - pressedLoc.x) + 1;
            int height = Math.abs(loc.y - pressedLoc.y) + 1;
            Command cmd = new BulldozeCommand(runner,
                    new GridRectangle(rectX, rectY, width, height));
            runner.runCommand(cmd);
        }
        pressedLoc = null;
        display.clearToolRect();
    }

    /* The mouse was moved (dragged) while the button was pressed. Change
     * the displayed rectangle is the mouse is inside the grid.
     */
    @Override
    public void onMouseDragged(GridLocation loc) {
        // System.out.printf("MouseDragged %s%n", loc);
        if (loc != null) {
            displayRect(loc);
        } else {
            display.clearToolRect();
            priorLoc = null;
        }
    }
}
