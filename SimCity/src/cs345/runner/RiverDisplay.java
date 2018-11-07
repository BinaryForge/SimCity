/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.Cell;
import cs345.model.GridLocation;
import cs345.model.cell.River;
import javafx.scene.Node;

/**
 * Add river (water) to a display.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class RiverDisplay implements Cell.CellListener {

    private final GridDisplay disp; // The grid display where it is displayed
    private final Node gridImage; // The image that is displayed there.

    RiverDisplay(GridDisplay disp, River cell) {
        this.disp = disp;
        cell.addListener(this);
        GridLocation loc = cell.getLocation();
        gridImage = disp.drawBackground(loc.x, loc.y, GridDisplay.RIVER_OFFSET);
    }

    @Override
    public void cellChanged(Cell cell) { }

    @Override
    public void bulldoze(Cell cell) {
        disp.removeImage(gridImage);
    }
}
