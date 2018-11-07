/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import javafx.animation.Animation;

/**
 * The stop command. This command only works with a GUI Runner.
 *
 * This command is used to stop a running simulation.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class StopCommand implements RunStopCommand {

    private GuiRunner runner;

    /**
     * Create a new StopCommand for the given runner.
     * @param runner the runner object for this command.
     */
    public StopCommand(GuiRunner runner) {
        this.runner = runner;
    }

    /** Return true indicating this is a stop command.
     *
     * @return true
     */
    @Override
    public boolean isStopRun() {
        return true;
    }

    /** Throw an UnsupportedOperationException. */
    @Override
    public int getNumSteps() {
        throw new UnsupportedOperationException("Not valid for StopCommand");
    }

    /** Throw an UnsupportedOperationException. */
    @Override
    public int getStepRate() {
        throw new UnsupportedOperationException("Not valid for StopCommand");
    }

    /** Run the command. */
    @Override
    public void run() throws CommandException {
        runner.executeRun(this);
    }
}
