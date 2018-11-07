/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

/**
 * A newgrid Command
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class ModelNewGridCommand implements ModelCommand {

    boolean newGenerator;

    public ModelNewGridCommand(boolean newGenerator) {
        this.newGenerator = newGenerator;
    }

    @Override
    public void run(Cs345Opolis model) {
        model.newMapGrid(newGenerator);
    }
}