/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

/**
 * A step Command, includes an interval size and a number of intervals.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class ModelStepCommand implements ModelCommand {

    public ModelStepCommand() {
    }

    @Override
    public void run(Cs345Opolis model) {
        model.step();
    }
}