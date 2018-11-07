/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.ModelNewGridCommand;

/**
 * A newgrid Command
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class NewGridCommand implements Command {

    private Runner runner;
    private ModelNewGridCommand modelCommand;

    /**
     * Create a new grid.
     *
     * @param runner the associated Runner
     * @param newGenerator use a new map generator
     */
    NewGridCommand(Runner runner, boolean newGenerator) {
        this.runner = runner;
        this.modelCommand = new ModelNewGridCommand(newGenerator);
    }

    @Override
    public void run() throws CommandException {
        if (runner.newMapOK()) {
            runner.runModelCommand(modelCommand);
        } else {
            throw new CommandException("New grid not allowed");
        }
    }
}