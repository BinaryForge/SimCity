/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model.cell;

import cs345.model.*;
import java.util.*;

/**
 * This is a superclass for zone type cells, such as Residential.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public abstract class Zone extends Cell implements SimulatorAction {
   protected GridLocation center; // The center row and column of the zone
   protected GridRectangle zoneRect; // The rectangle containing the zone
   protected int population; //Population of the zone
 
      
   public Zone(Cs345Opolis parent, GridLocation loc) {
      super(parent);
      this.center = loc;
      this.population = 0;
      this.placeZoneInGrid(this);
      this.scheduleUpdate();
      this.fireGridChanged();
   }      
     /* Get population of zone. */
   public int getPopulation() {
      return population;
   }

   /** Call fireGridChanged in the parent for this zone.
     */
   protected void fireGridChanged() {
      parent.fireGridChanged(zoneRect);
   }

    /**
     * Bulldoze the zone.
     *
     * When any cell of the zone is bulldozed, the entire zone is bulldozed.
     */
   @Override public void bulldoze() {
      unscheduleUpdate();
      Grid grid = parent.getGrid();
      for (int x = zoneRect.x; x < zoneRect.x + zoneRect.w; x++) {
         for (int y = zoneRect.y; y < zoneRect.y + zoneRect.h; y ++) {
            grid.setCellAt(x, y, new Dirt(parent, new GridLocation(x, y)));
         }
      }
      fireBulldoze();
   }

   public boolean isBulldozeable() {
      return true;
   }
    
    /**
     * @return the rectangle containing this zone
     */
   public GridRectangle getRectangle() {
      return zoneRect;
   }

    /* Place the given cell is all cells of the zones grid. */
   protected void placeZoneInGrid(Cell cell) {
      zoneRect = new GridRectangle(center.x - 1, center.y - 1, 3, 3);
      Grid grid = parent.getGrid();
      for (int col = center.x - 1; col <= center.x + 1; col++) {
         for (int row = center.y - 1; row <= center.y + 1; row ++) {
            grid.setCellAt(col, row, cell);
         }
      }
   }

    /* Schedule the cell with the simulator. */
   protected void scheduleUpdate() {
      parent.addAction(parent.getCurrentTime().nextStep(1),this);
   }

    /* Unschedule the cell when the zone is bulldozed. */
   protected void unscheduleUpdate() {
      parent.removeAction(this);
   }
}
