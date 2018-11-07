/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import javafx.animation.Animation;

/**
 * Interface for a run or stop command. Only works with a GUI Runner.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public interface RunStopCommand extends Command {

    /** Return true if this is a StopCommand, false for RunCommand. */
    boolean isStopRun();

    /** Get the number of steps for this command.
     *
     * @throws UnsupportedOperationException if this is not a RunCommand.
     */
    int getNumSteps();

    /** Get the step rate for this command.
     *
     * @throws UnsupportedOperationException if this is not a RunCOmmand.
     */
    int getStepRate();
}
