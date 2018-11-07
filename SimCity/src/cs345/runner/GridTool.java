/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.Cs345Opolis;
import cs345.model.GridLocation;
import javafx.scene.input.MouseEvent;

/**
 * Interface for Tool classes that receive mouse events from the Grid.
 *
 * By default all the event handlres do nothing. It is expected that specific
 * tool classes will override the specific ones that are needed by that
 * tool. For all event handlers, the parameters are the column/row location
 * on the grid where the event occurred.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public abstract class GridTool {

    protected GridDisplay display;
    protected GuiRunner runner;

    public GridTool(GuiRunner runner, GridDisplay display) {
        this.runner = runner;
        this.display = display;
    }

    /** Enable the tool */
    public void enable() {}

    /** Disable the tool */
    public void disable() {}

    /* The methods below are called when a mouse event is passed to the
     * GridDisplay object. The grid display object will convert the mouse
     * coordinates (in pixels) to grid coordinates. If the mouse is outside
     * the grid, the method will either be called with loc == null or
     * not called.
     */

    /** The mouse was moved in the grid with no button pressed. Not called
     * when the mouse is outside the grid. */
    public void onMouseMoved(GridLocation loc) {}

    /** The mouse entered the grid. Called with loc == null if the mouse is
     * ouside the grid (probably shouldn't happen.)*/
    public void onMouseEntered(GridLocation loc) {}

    /** The mouse exited the grid. Called with loc == null if the mouse is
     * outside the grid.*/
    public void onMouseExited(GridLocation loc) {}

    /** The mouse button was clicked. Not called when the mouse is outside
     * the grid. */
    public void onMouseClicked(GridLocation loc) {}

    /** The mouse button was pressed. Not called when the mouse is outside the
     * grid. */
    public void onMousePressed(GridLocation loc) {}

    /** The mouse button was released. Called with loc == null if the mouse is
     * outside the grid. */
    public void onMouseReleased(GridLocation loc) {}

    /** The mouse was moved in the grid with the button pressed. Called with
     * loc == null if the mouse is outside the grid. */
    public void onMouseDragged(GridLocation loc) {}
}
