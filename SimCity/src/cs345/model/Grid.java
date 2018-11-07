/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

import java.util.Arrays;

/**
 * A two-dimensional array of Cells.
 *
 * Grids have a width and a height. Grid location are designated by an x, y
 * coordinate pair with 0 <= x < width, and 0 <= y < height. conceptually,
 * the origin of the grid is assumed to be in the upper-left hand corner,
 * the same as screen display coordinates. The minimum width and height for a
 * grid is 20.
 *
 * The underlying array is maintained as a one-dimensional array. The methods
 * cellAt and setCellAt, used to manipulate the Cells, both take x and y
 * coordinates.
 *
 * Utility routines are provided for standard operations on the grid.
 *
 * Two factory functions are provided for constructing a grid:
 *   1. emptyGrid(width, height, default) creates a grid of the given width
 *      and height, setting each location in the grid to the default cell.
 *   2. newMapGrid(width, height, generator) creates a grid of the given
 *      width and height, filling the grid by calling the given map
 *      generator object.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class Grid {

    /** Minimum size for each edge of the grid. */
    public static final int MIN_GRID_SIZE = 20;

    // The model for this grid.
    private Cs345Opolis model;

    private int width; // Width of the grid
    private int height; // Height of the grid
    private Cell[] grid; // The grid

    /**
     * Return width of grid
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Return the height of the grid
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Return true if the given x, y are valid grid coordinates
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if the coordinates are valid, otherwise false
     */
    public boolean validCoords(int x, int y) {
        return 0 <= x && x < width && 0 <= y && y < height;
    }

    /**
     * Return true if the given grid location is valid
     * @param loc the location to check
     * @return true if the coordinates are valid, otherwise false
     */
    public boolean validCoords(GridLocation loc) {
        return validCoords(loc.x, loc.y);
    }

    /**
     * Return true if the given grid rectangle is valid
     * @param rect the rectangle to check
     * @return true if the rectangle is inside the grid, otherwise false
     */
    public boolean validRegion(GridRectangle rect) {
        return validCoords(rect.x, rect.y)
                && validCoords(rect.x + rect.w - 1, rect.y + rect.h - 1);
    }

    /**
     * Get the cell at the given x, y coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the cell object at that coordinate
     * @throws IndexOutOfBoundsException if the coordinates are not valid
     */
    public Cell cellAt(int x, int y) {
        if (!validCoords(x, y))
            throw new IndexOutOfBoundsException(
                    String.format("(%d, %d) is not a valid grid coordinate", x, y));
        return grid[x + width * y];
    }

    /**
     * Interface for filling a grid.
     */
    @FunctionalInterface
    public interface MakeGridCell {
        Cell newCell(Cs345Opolis parent, GridLocation loc);
    }
    /**
     * Fill the grid with the specified cell.
     * @param factory a MakeGridCell object that creates cells given a
     *                GridLocation
     */
    public void fill(MakeGridCell factory) {
        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y++) {
                setCellAt(x, y, factory.newCell(model, new GridLocation(x, y)));
            }
        }
    }

    /**
     * Set the cell at the given x, y coordinates to cell.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param cell the cell object
     * @throws IndexOutOfBoundsException if the coordinates are not valid
     * @throws NullPointerException if the passed cell is null
     */
    public void setCellAt(int x, int y, Cell cell) {
        if (cell == null)
            throw new NullPointerException("Cannot set null cell");
        if (!validCoords(x, y))
            throw new IndexOutOfBoundsException(
                    String.format("(%d, %d) is not a valid grid coordinate", x, y));
        grid[x + width * y] = cell;
    }

    /**
     * Generate a empty grid.
     *
     * Note that the created grid is null. It is expected that the calling
     * factory function (emptyGrid or newMapGrid)  will fill the grid.
     * @param model the Cs345Opolis model this grid is part of
     * @param width the grid width
     * @param height the grid height
     * @throws IllegalArgumentException if either the width or height is < MIN_GRID_SIZE
     */
    private Grid(Cs345Opolis model, int width, int height) {
        if (width < MIN_GRID_SIZE || height < MIN_GRID_SIZE)
            throw new IllegalArgumentException(
                    String.format("Grid width and height must both be >= %d",
                            MIN_GRID_SIZE));
        this.model = model;
        this.width = width;
        this.height = height;
        this.grid = new Cell[width * height];
    }

    /**
     * Return a new grid with all cells set to the default cell.
     * @param model the model this grid is part of
     * @param width the grid width
     * @param height the grid height
     * @param defaultCell the default value for grid cells
     * @return the created grid
     * @throws IllegalArgumentException if either the width or height is <= MIN_GRID_SIZE
     * @throws NullPointerException if default is null
     */
    static Grid emptyGrid(Cs345Opolis model, int width, int height, MakeGridCell defaultCell) {
        if (defaultCell == null)
            throw new IllegalArgumentException("Cannot fill grid with null cell");
        Grid result = new Grid(model, width, height);
        result.fill(defaultCell);
        return result;
    }
}
