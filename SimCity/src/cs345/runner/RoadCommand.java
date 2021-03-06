/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.GridRectangle;
import cs345.model.ModelRoadCommand;

/**
 * A bulldoze Command, includes the region to be bulldozed.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class RoadCommand implements Command {

    private Runner runner;
    private ModelRoadCommand modelCommand;

    /** Construct a new road command.
     *
     * @param runner the Runner associated with this command.
     * @param rect the rectangle where the road is the be place. 
     */
    RoadCommand(Runner runner, GridRectangle rect) {
        this.runner = runner;
        modelCommand = new ModelRoadCommand(rect);
    }

    @Override
    public void run() throws CommandException {
        runner.runModelCommand(modelCommand);
    }
}