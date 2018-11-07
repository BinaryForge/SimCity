/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

import cs345.model.cell.CellType;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a Cell in the the grid map for a game.
 *
 * This abstract class provides the mechanism for supporting cell listeners
 * that respond to changes in the cell.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public abstract class Cell {

    // The parent model for this cell
    protected Cs345Opolis parent;

    // The change listeners for this cell
    private List<CellListener> listeners = new ArrayList<>();

    /**
     * Construct a Cell
     * @param parent the parent model of this cell
     */
    public Cell(Cs345Opolis parent) {
        this.parent = parent;
    }

    /**
     * Return the CellType for this cell
     * @return the CellType
     */
    public abstract CellType getCellType();

    /**
     * Return true if this cell is some form of water.
     *
     * Default implementation is that the cell is not water.
     *
     * @return true if this is water
     */
    public boolean isWater() {
        return false;
    }

    /**
     * Return true if this cell is some form of woods.
     *
     * Default implementation is that the cell is not woods.
     *
     * @return true if this is woods
     */
    public boolean isTree() {
        return false;
    }

    /**
     * Return true if this cell can be built on.
     *
     * Default implementation is that the cell is not buildable.
     *
     * @return true if the cell is buildable
     */
    public boolean isBuildable() {
        return false;
    }

    /**
     * Return true if this cell can be bulldozed.
     *
     * Default implementation is that the cell cannot be bulldozed.
     *
     * @return true if the cell can be bulldozed
     */
    public boolean isBulldozeable() {
        return false;
    }

    /**
     * Bulldoze the cell.
     *
     * This method provides an opportunity for any cleanup required when the
     * cell is being bulldozed. The default implementation calls the
     * bulldoze operation on any cell listeners if the cell can be bulldozed
     * and throws an UnsupportedOperationException if the cell cannot
     * be bulldozed.
     *
     * This method must be overridden for cells that can be bulldozed.
     */
    public void bulldoze() {
        throw new UnsupportedOperationException("Illegal bulldoze operation");
    }

    /**
     * This is the interface for objects that listen for changes in the status
     * of a cell.
     */
    public interface CellListener {

        /**
         * Tell any listeners that this location is being bulldozed.
         * @param cell the cell being bulldozed.
         */
        void bulldoze(Cell cell);

        /**
         * Tell any listeners that some property of the cell has changed.
         * @param cell this cell
         */
        void cellChanged(Cell cell);
    }

    /**
     * Add a listener to the listeners for this cell.
     * @param listener the CellListener to add
     */
    public void addListener(CellListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listeners from the listeners for this cell.
     * @param listener the Cell Listener to remove
     */
    public void removeListener(CellListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify any cell listeners that this cell has been bulldozed.
     */
    protected void fireBulldoze() {
        listeners.forEach(listener -> listener.bulldoze(this));
    }

    /**
     * Notify any cell listeners that some property of this cell has changed.
     */
    protected void fireCellChanged() {
        listeners.forEach(listener -> listener.cellChanged(this));
    }
}
