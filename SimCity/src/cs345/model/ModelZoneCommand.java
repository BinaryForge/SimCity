/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

import cs345.model.cell.Industrial;
import cs345.model.cell.Residential;

/**
 * A zone Command, includes the type of zone to be created.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class ModelZoneCommand implements ModelCommand {

    private String zoneType;
    private GridLocation loc;

    public ModelZoneCommand(String zoneType, GridLocation loc) {
        this.zoneType = zoneType;
        this.loc = loc;
    }

    @Override
    public void run(Cs345Opolis model) throws ModelCommandException {
        int col = loc.x;
        int row = loc.y;

        // Validate location
        GridRectangle zoneRect = new GridRectangle(col - 1, row - 1, 3, 3);
        if (!model.isBuildable(zoneRect)) {
            throw new ModelCommandException("Cannot build at %s", loc);
        }

        switch (zoneType) {
            case "residential":
                new Residential(model, loc);
                break;
            case "industrial":
                new Industrial(model, loc);
                break;    
            default:
                throw new AssertionError("Unknown zone type " + zoneType);
        }
    }
}