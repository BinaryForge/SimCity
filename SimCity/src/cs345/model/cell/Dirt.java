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
 * Cell for dirt.
 *
 * Dirt cells have CellType DIRT, are buildable and bulldozeable.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class Dirt extends SimpleCell {

    /* Constructor is only used above. */
    public Dirt(Cs345Opolis parent, GridLocation loc) {
        super(parent, loc);
    }

    @Override public CellType getCellType() {
        return CellType.DIRT;
    }

    @Override public boolean isBuildable() {
        return true;
    }

    @Override public boolean isBulldozeable() {
        return true;
    }

    @Override public void bulldoze() {
        // Bulldozing dirt gives dirt. Do nothing.
    }
}
