/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

/**
 * A location on the grid. It is represented by an x,y coordinate pair
 * on the grid.
 */
public class GridLocation {

    public final int x;
    public final int y;

    /**
     * Construct a location object given its x and y.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public GridLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Return a string for this grid location.
     * @return the string
     */
    @Override public String toString() {
        return String.format("GridPoint (%d, %d)", x, y);
    }

    /**
     * Compare this to another object
     * @param other the other object
     * @return true if the other object is a GridLocation at the same location
     */
    @Override public boolean equals(Object other) {
        if (!(other instanceof GridLocation))
            return false;
        GridLocation otherLoc = (GridLocation)other;
        return x == otherLoc.x && y == otherLoc.y;
    }

    /**
     * Return a hashcode for this object.
     * @return the hashcode.
     */
    @Override public int hashCode() {
        return 31 * x + y;
    }
}