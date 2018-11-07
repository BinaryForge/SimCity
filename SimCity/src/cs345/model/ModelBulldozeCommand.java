/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

/**
 * A bulldoze Command, includes the region to be bulldozed.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class ModelBulldozeCommand implements ModelCommand {

    private GridRectangle rect;

    /** Construct a new bulldoze command.
     *
     * @param rect the rectangle to bulldoze
     */
    public ModelBulldozeCommand(GridRectangle rect) {
        this.rect = rect;
    }

    @Override
    public void run(Cs345Opolis model) throws ModelCommandException {
        if (!model.isBulldozeable(rect)) {
            throw new ModelCommandException("Cannot bulldoze %s", rect);
        }
        model.bulldoze(rect);
    }
}