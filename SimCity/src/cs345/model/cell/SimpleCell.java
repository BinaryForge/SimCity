/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

/**
 * A cell which occupies a single location.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
package cs345.model.cell;

import cs345.model.Cell;
import cs345.model.Cs345Opolis;
import cs345.model.GridLocation;

/**
 * An abstract class for a cell which occupies a single location.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public abstract class SimpleCell extends Cell {

    private GridLocation location;

    /**
     * Constructor
     *
     * @param parent the parent model of this cell
     * @param loc the location of the cell
     */
    public SimpleCell(Cs345Opolis parent, GridLocation loc) {
        super(parent);
        location = loc;
    }

    /**
     * Getter for the location.
     */
    public GridLocation getLocation() {
        return location;
    }

}
