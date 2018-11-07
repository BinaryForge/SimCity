/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model.cell;

import cs345.model.*;
import cs345.model.cell.Road;
import cs345.model.GridLocation;
import cs345.model.Cs345Opolis;
import cs345.model.Grid;

/**
 * A cell representing a Residential zone.
 *
 * A residential zone is a 3x3 grid of cells. All cell in the zone reference
 * the same residential zone instance.
 *
 * A residential zone implements SimulatorAction. This allows the zone to
 * update its attributes every period.
 *
 * Residential zones have CellType RESIDENTIAL and are bulldozeable but not
 * buildable.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class Residential extends Zone implements SimulatorAction {
   
   /**
    * Construct a new Residential zone.
    * @param parent The parent game
    * @param loc the GridLocation of the center of the zone
    */
   public Residential(Cs345Opolis parent, GridLocation loc) {
      super(parent,loc);
   }

   public CellType getCellType() {
      return CellType.RESIDENTIAL;
   }

   /**
    * Do the periodic update for the zone.
    *
    * The periodic update adjust the population toward the desired population.
    *
    * The action will be rescheduled for the next PERIOD.
    * @return Simulator.STEPS_PER_PERIOD
    */
   @Override public int doAction() {
      //Road.findConnections(loc);
      //model has not been updated to handle roads due to failure of find Path
      //Do we want to adjust the population
      if (parent.prngNextInt(2 * SimulatorTime.MONTH) == 0) {
         // 1 in every 8 cycles (random) adjust population
         // Get a random number 20 .. 60. If we're less than that, add
         // people. If we're more than that, subtract.
      
         int totalToAdd = 0;
         int totalToReduce = 0;
         int force = 0;
         int populationDifference = (8 * parent.curIndCount) - parent.curResPop;
         int density = (population / 32) + 1;
         int populationDelta = parent.prngNextInt(6) + 2;
         
         if (populationDifference > populationDelta) {
            force = 1;
         } 
         else if (populationDifference < -populationDelta) {
            force = -1;
         } 
         else {
            force = 0;
         }
            
         if(force == 1 || (force == 0 && density == 1)) {
            totalToAdd = (3-density)*(parent.prngNextInt(2)+1);
            population += totalToAdd;
            fireCellChanged();
         } 
         else if(force == -1) {
            totalToReduce = density * (parent.prngNextInt(2) + 1);
            population -= totalToReduce;
            if(population < 0){
               population = 0;
            }
            fireCellChanged();
         }
      }
   // System.out.printf("  pop(new) = %d%n", population);
   
      // Update total population in parent
      parent.newResPop += population;
   
      // Reschedule for next week
      return SimulatorTime.WEEK * Simulator.STEPS_PER_PERIOD;
   }
}
