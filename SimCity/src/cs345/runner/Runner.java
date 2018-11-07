/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.Cs345Opolis;
import cs345.model.ModelCommand;

/**
 * Interface implemented by the TextRunner and GuiRunner classes.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public interface Runner {

    /* Constants for properties for the size of the grid.
     */
    String GRID_WIDTH = "cs345opolis.grid.width";
    String GRID_HEIGHT = "cs345opolis.grid.height";

    /** Get the GuiRunner for this runner.
     *
     * @return the GuiRunner object or null is this is not a GuiRunner.
     */
    default GuiRunner getGuiRunner() {
        return null;
    }

    /** Get the TextRunner for this runner.
     *
     * @return the TextRunner object or null is this is not a TextRunner.
     */
    default TextRunner getTextRunner() {
        return null;
    }

    /** Return the associated model for this runner.
     * @return the model
     */
    Cs345Opolis getModel();

    /** Set quit to true. */
    void setQuit();

    /**
     * Output a message. format and args are printf-like parameters. A message
     * takes a single line on the output
     * @param format the format of the message
     * @param args the arguments for the format
     */
    void message(String format, Object... args);

    /**
     * Run a ModelCommand.
     *
     * @param command the command to be run
     * @throws CommandException if there is an exception running the command
     */
    void runModelCommand(ModelCommand command) throws CommandException;

    /** Determine if initMap is available.
     *
     * @return true if initMap will initialize a new map
     */
    boolean newMapOK();

    /** Step the game the given number (num) of intervals.
     *
     * Stepping is accomplished by creating a timeline object that repeatedly
     * steps the game one step.
     *
     * @param num number of intervals
     * @param interval size of an interval
     */
    void step(int num, int interval) throws CommandException;
}
