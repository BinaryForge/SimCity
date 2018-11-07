/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.GridLocation;
import cs345.model.ModelZoneCommand;

/**
 * A zone Command, includes the type of zone to be created.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class ZoneCommand extends RunnerModelCommand {

    ZoneCommand(Runner runner, String zoneType, GridLocation loc) {
        super(runner, new ModelZoneCommand(zoneType, loc));
    }
}