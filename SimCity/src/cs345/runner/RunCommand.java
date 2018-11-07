/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import javafx.animation.Animation;

/**
 * The run command. This command only works with a GUI Runner.
 *
 * This command can be used to start, stop, or changed the rate of a running
 * simulation. The command w
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class RunCommand implements RunStopCommand {

    private GuiRunner runner;

    /**
     * Enumeration of the step rates.
     */
    public enum StepRate {SLOW, NORMAL, FAST, VERYFAST}

    /**
     * Get the step rate for a given StepRate value.
     *
     * The runner has the actual value for the step rate. This method
     * retrieves the rate from the runner.
     * @param runner the runner with the specified step rate values
     * @param rate the StepRate
     * @return an int giving the actual step rate
     */
    public static int getStepRate(GuiRunner runner, StepRate rate) {
        return runner.getStepRate(rate);
    }

    /** The number of steps to run. Animation.INDEFINITE for indefinite. */
    public final int numSteps;

    /** The step rate to use for the model. Must be > 0. */
    public final int stepRate;

    /**
     * Construct a new RunCommand for a runner and a given StepRate.
     *
     * The number of steps to run is indefinite.
     * @param runner the associated GuiRunner object
     * @param rate the StepRate
     */
    public RunCommand(GuiRunner runner, StepRate rate) {
        this(runner, getStepRate(runner, rate), Animation.INDEFINITE);
    }

    /**
     * Construct a new RunCommand for a runner for the given stepRate and
     * number of steps.
     * @param runner the associated GuiRunner object
     * @param stepRate the step rate in milliseconds per step
     * @param numSteps the number of steps to run
     */
    public RunCommand(GuiRunner runner, int stepRate, int numSteps) {
        this.runner = runner;
        this.stepRate = stepRate;
        this.numSteps = numSteps;
    }

    /**
     * Return false indicating that this is not a Stop.
     * @return false
     */
    @Override
    public boolean isStopRun() {
        return false;
    }

    /**
     * Return the number of steps for this command
     * @return the number of steps for this command
     */
    @Override
    public int getNumSteps() {
        return numSteps;
    }

    /**
     * Return the step rate for this command
     * @return the step rate in milliseconds per step
     */
    @Override
    public int getStepRate() {
        return stepRate;
    }

    /** Run the command. */
    @Override
    public void run() throws CommandException {
        runner.executeRun(this);
    }
}
