/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model.cell;

import cs345.model.*;

/**
 * Beginning of a class for Industrial zones
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class Industrial extends Zone implements SimulatorAction {
	
   /**
    * Construct a new Industrial zone.
    * @param parent The parent game
    * @param loc the GridLocation of the center of the zone
    */

   public Industrial(Cs345Opolis parent, GridLocation loc) {
      super(parent,loc);         
   }

   public CellType getCellType() {
      return CellType.INDUSTRIAL;
   }

  /**
    * Do the periodic update for the zone.
    *
    * The periodic update adjust the population toward the desired population.
    *
    * The action will be rescheduled for the next PERIOD.
    * @return Simulator.STEPS_PER_PERIOD
    */
   @Override
   public int doAction() {
      //Road.findConnections();
      //model has not been updated to handle roads due to failure of find Path
      
      // Do we want to adjust the population
      if (parent.prngNextInt(2 * SimulatorTime.MONTH ) == 0) {
         int populationDifference = parent.curResPop - (8 * parent.curIndCount);
         int populationDelta = parent.prngNextInt(4) + 2;
        
         //Add or reduce industries
         if (populationDifference > -populationDelta) {
            if (population < 5) {
               population += 1;
               fireCellChanged();
            }
         } 
         else if (populationDifference < (-2 * populationDelta)) {
            if (population > 0) {
               population -= 1;
               fireCellChanged();
               
            }
         }
      }
      // System.out.printf("  pop(new) = %d%n", population);
   
   
      // Update total population in parent
      parent.newIndCount += population;
   
      // Reschedule for next week
      return SimulatorTime.WEEK * Simulator.STEPS_PER_PERIOD;
   }

}

