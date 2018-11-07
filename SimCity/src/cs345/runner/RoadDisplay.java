/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.Cell;
import cs345.model.GridLocation;
import cs345.model.cell.Road;
import javafx.scene.Node;

/**
 * Add road to a display.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class RoadDisplay implements Cell.CellListener {

   private final GridDisplay disp; // The grid display where it is displayed
   private final Node gridImage; // The image that is displayed there.
   protected GridLocation loc;   //Initialize for later use;
      
   RoadDisplay(GridDisplay disp, Road cell) {
      this.disp = disp;
      cell.addListener(this);
      loc = cell.getLocation(); 
      gridImage = disp.drawBackground(loc.x, loc.y, GridDisplay.ROAD_OFFSET);
      cellMatch(cell);  
   }
   
   /* Method for creating the right road picture 
    * based on the celltype selected.
    */
    public void cellMatch(Road cell){
      if(cell.getCellTypeUnder().toString() == "WOODS"){                 //Makes sure to grab
         disp.drawBackground(loc.x, loc.y, GridDisplay.ROAD_OFFSET+2);   //the right iamge of
      }
      else if(cell.getCellTypeUnder().toString() == "RIVER"){           //of road based on what 
         disp.drawBackground(loc.x, loc.y, GridDisplay.ROAD_OFFSET+1);   //type of cell it is being         
      }
      else{                                                             //built on
         disp.drawBackground(loc.x, loc.y, GridDisplay.ROAD_OFFSET);
      }
    }

   @Override
    public void cellChanged(Cell cell) { }

   @Override
    public void bulldoze(Cell cell) {
      disp.removeImage(gridImage);
   }
}
