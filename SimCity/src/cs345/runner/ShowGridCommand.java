/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

/**
 * Display the Grid. TextRunner only.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class ShowGridCommand implements Command {

    private TextRunner runner;

    ShowGridCommand(Runner runner) throws CommandException {
        TextRunner textRunner = runner.getTextRunner();
        if (textRunner == null) {
            throw new CommandException("show grid command only allowed for text");
        }
        this.runner = textRunner;
    }

    @Override
    public void run() throws CommandException {
        runner.displayGrid();
    }
}
