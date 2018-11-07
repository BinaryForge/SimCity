/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model.cell;

import cs345.model.Cell;
import cs345.model.Cs345Opolis;
import cs345.model.GridLocation;

/**
 * Cell for water: rivers, lakes, etc..
 *
 * River cells have CellType RIVER. Rivers are not buildable by zones but
 * are buildable by roads. Rivers are not bulldozeable.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class River extends SimpleCell {

    public River(Cs345Opolis parent, GridLocation loc) {
        super(parent, loc);
    }

    @Override public CellType getCellType() {
        return CellType.RIVER;
    }

    @Override public boolean isWater() {
        return true;
    }
}
