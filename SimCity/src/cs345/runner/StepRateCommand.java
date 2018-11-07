/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

/**
 * Description
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class StepRateCommand implements Command {

    private GuiRunner runner;
    private int rate;

    StepRateCommand(Runner runner, int rate) throws CommandException {
        GuiRunner guiRunner = runner.getGuiRunner();
        if (guiRunner == null) {
            throw new CommandException("steprate command only allowed for gui");
        }
        this.runner = guiRunner;
        this.rate = rate;
    }

    public void run() {
        runner.setStepRate(rate);
    }
}
