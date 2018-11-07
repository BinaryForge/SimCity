/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

/**
 * A rectangle on the grid. It is represented by an x,y coordinate pair
 * on the grid and a width and a height.
 *
 * The rectangle is normalized so that x and y are the upper left hand
 * corner and width and height are positive. It is assumed that the cell
 * at x, y is inside the Rectangle is all cases.
 */
public class GridRectangle {

    public final int x;
    public final int y;
    public final int w;
    public final int h;

    /**
     * Construct a rectangle on the grid given its x,y and width and height.
     *
     * If width or height is negative, x and w (width < 0) and/or y and h
     * (height < 0) are adjusted to ensure that the rectangle is normalized.
     * @param x the x coordinate of an included corner of the rectangle
     * @param y the y coordinate of an included corner of the rectangle
     * @param w the width of the rectangle
     * @param h the height of the rectangle
     */
    public GridRectangle(int x, int y, int w, int h) {
        if (w >= 0) {
            this.x = x;
            this.w = w;
        } else {
            this.x = x - w + 1;
            this.w = -w;
        }
        if (h >= 0) {
            this.y = y;
            this.h = h;
        } else {
            this.y = y - h + 1;
            this.h = -h;
        }
    }

    /**
     * Construct a rectangle on the grid given a location and width and height.
     *
     * If width or height is negative, x and w (width < 0) and/or y and h
     * (height < 0) are adjusted to ensure that the rectangle is normalized.
     * @param loc the location of an included corner of the rectangle
     * @param w the width of the rectangle
     * @param h the height of the rectangle
     */
    public GridRectangle(GridLocation loc, int w, int h) {
        int x = loc.x;
        int y = loc.y;
        if (w >= 0) {
            this.x = x;
            this.w = w;
        } else {
            this.x = x - w + 1;
            this.w = -w;
        }
        if (h >= 0) {
            this.y = y;
            this.h = h;
        } else {
            this.y = y - h + 1;
            this.h = -h;
        }
    }

    /**
     * Return a string for this grid rectangle.
     * @return the string
     */
    @Override public String toString() {
        return String.format("GridRectangle (%d, %d) %dx%d", x, y, w, h);
    }

    /**
     * Compare this to another object
     * @param other the other object
     * @return true if the other object is a GridRectangle at the same location
     *      with the same size
     */
    @Override public boolean equals(Object other) {
        if (!(other instanceof GridRectangle))
            return false;
        GridRectangle otherRec = (GridRectangle)other;
        return x == otherRec.x && y == otherRec.y &&
                w == otherRec.w && h == otherRec.h;
    }

    /**
     * Return a hashcode for this object.
     * @return the hashcode.
     */
    @Override public int hashCode() {
        return 31 * (31 * (31 * x + y) + w) + h;
    }
}
