/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.ModelCommand;

/**
 * A Command that simply translates into a ModelCommand.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
abstract class RunnerModelCommand implements Command{

    private Runner runner;
    private ModelCommand modelCommand;

    RunnerModelCommand(Runner runner, ModelCommand modelCommand) {
        this.runner = runner;
        this.modelCommand = modelCommand;
    }

    @Override
    public void run() throws CommandException {
        runner.runModelCommand(modelCommand);
    }
}
