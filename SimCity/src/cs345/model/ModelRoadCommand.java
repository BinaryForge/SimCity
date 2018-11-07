/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

import cs345.model.cell.Road;
import cs345.model.GridLocation;

/**
 * A bulldoze Command, includes the region to be bulldozed.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class ModelRoadCommand implements ModelCommand {

   private GridRectangle rect;
   private Grid grid;
  
    /** Construct a new road command.
     *
     * @param rect the rectangle to create a new road
     */
   public ModelRoadCommand(GridRectangle rect) {
      this.rect = rect;
   }
    
   @Override
    public void run(Cs345Opolis model) throws ModelCommandException { 
     grid = model.getGrid();             
      
      /**Conditional Statements to check for...
      * 1)Rectangle has the correct shape
      * 2)Each location in the rectangle is buildable or a river
      * 3)If conditions meet a new Road Object at every location in the rectangle is constructed
      **/   
      if (((rect.w == 1 && rect.h > 0) || (rect.w > 0 && rect.h == 1))){
         if(model.buildLocCheck(rect)){
            for (int a = rect.x; a < rect.x + rect.w; a++) {
               for (int b = rect.y; b < rect.y + rect.h; b++) {
                  grid.setCellAt(a, b, new Road(model, new GridLocation(a,b)));   
               }
            }
            model.fireGridChanged(rect);
         }
      }
      else{
         throw new ModelCommandException("Cannot build, invalid rectangle.");
      }
   }
}               
