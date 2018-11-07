/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

/**
 * A step Command, includes an interval size and a number of intervals.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class StepCommand implements Command {

    private Runner runner;
    private int num;
    private int interval;

    StepCommand(Runner runner, int n, int interval) {
        this.runner = runner;
        this.num = n;
        this.interval = interval;
    }

    @Override
    public void run() throws CommandException {
        runner.step(num, interval);
    }
}