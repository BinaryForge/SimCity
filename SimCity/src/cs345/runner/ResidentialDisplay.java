/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.Cell;
import cs345.model.GridRectangle;
import cs345.model.cell.Residential;
import javafx.scene.Node;

/**
 * Add river (water) to a display.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class ResidentialDisplay implements Cell.CellListener {

    private final GridDisplay disp; // The grid display where it is displayed
    private Node gridImage = null; // The image that is displayed there.
    private int offset = -1; // The offset that is currently displayed.

    ResidentialDisplay(GridDisplay disp, Residential cell) {
        this.disp = disp;
        cell.addListener(this);
        updateImage(cell);
    }

    private void updateImage(Residential cell) {
        int resPop = (cell.getPopulation() + 7) / 8;
        resPop = Math.min(resPop, 9);
        if (resPop != offset) {
            // Number of industries changed. Update image.
            if (gridImage != null) {
                disp.removeImage(gridImage);
            }
            GridRectangle rect = cell.getRectangle();
            offset = resPop;
            gridImage = disp.drawResidential(rect.x, rect.y, offset);
        }
    }

    @Override
    public void cellChanged(Cell cell) {
        updateImage((Residential)cell);
    }

    @Override
    public void bulldoze(Cell cell) {
        disp.removeImage(gridImage);
    }
}
