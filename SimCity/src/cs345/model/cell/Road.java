/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model.cell;

import cs345.model.Cell;
import cs345.model.Cs345Opolis;
import cs345.model.GridLocation;
import cs345.model.Grid;
import java.util.*;

/**
 * Cell for roads.
 *
 * Road cells have CellType ROADS, that are not buildable and bulldozeable.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class Road extends SimpleCell  {
   protected Grid grid;      //Initialize a grid which will be used later; 
   protected Cell underCell; //Initialize cell for detecting type;
   protected List<Integer> roadValues; //Initialize to store connection values;
  
    
   public Road(Cs345Opolis parent, GridLocation loc) {
      super(parent, loc);
      grid = parent.getGrid();
      underCell = grid.cellAt(loc.x,loc.y);
   }
   
   @Override public CellType getCellType() {
      return CellType.ROAD;
   }

   @Override public boolean isBuildable() {
      return false;
   }
   
   @Override public boolean isBulldozeable() {
      return true;
   }
   
   //Method for finding if a road connects to zones.
   //Returns an int
   //Still a work in progress.
   public List findConnections(GridLocation loc){    
      loc = getLocation(); 
      int x = loc.x;
      int y = loc.y;

      int searchRight = (loc.x-1)+3;
      int searchLeft = (loc.x-1)-3;
      int searchDown = (loc.y-1)+3;
      int searchUp = (loc.x-1)-3;
      
      Cell cellRight = grid.cellAt(searchRight,y);
      Cell cellLeft = grid.cellAt(searchLeft,y);
      Cell cellUp = grid.cellAt(x,searchUp);
      Cell cellDown = grid.cellAt(x,searchDown);
      
      if(cellRight.getCellType().toString() == "ROAD"){
         for(int n = searchRight; n < grid.getWidth(); n++){
            Cell checkNext = grid.cellAt(searchRight+1,y);
            if(checkNext.getCellType().toString() == "RESIDENTIAL"){
              roadValues.add(0);
            }
            if(checkNext.getCellType().toString() == "INDUSTRIAL"){
              roadValues.add(1);
            }
         }
      }
      else if(cellLeft.getCellType().toString() == "ROAD"){
         for(int n = searchLeft; n > 0; n--){
            Cell checkNext = grid.cellAt(searchLeft-1,y);
            if(checkNext.getCellType().toString() == "RESIDENTIAL"){
               roadValues.add(0);
            }
            if(checkNext.getCellType().toString() == "INDUSTRIAL"){
               roadValues.add(1);
            }
         }
      }
      else if(cellUp.getCellType().toString() == "ROAD"){
         for(int n = searchUp; n > 0; n--){
            Cell checkNext = grid.cellAt(x,searchUp-1);
            if(checkNext.getCellType().toString() == "RESIDENTIAL"){
              roadValues.add(0);
            }
            if(checkNext.getCellType().toString() == "INDUSTRIAL"){
              roadValues.add(1);
            }
         }
      }
      else if(cellDown.getCellType().toString() == "ROAD"){
         for(int n = searchUp; n < grid.getHeight(); n++){
            Cell checkNext = grid.cellAt(x,searchDown+1);
            if(checkNext.getCellType().toString() == "RESIDENTIAL"){
               roadValues.add(0);
            }
            if(checkNext.getCellType().toString() == "INDUSTRIAL"){
               roadValues.add(1);
            }
         }
      }
      else{
          System.out.println("Road not found");
          roadValues.add(-1);
      }
     //returns empty List if zone is not connected 
     return roadValues;  
                          
   }


   //Getter Method for the cell under the road
   //Returns Celltype
   public CellType getCellTypeUnder(){
      return underCell.getCellType();
   }
   
   //Method Bulldoze which restores original CellType
   //after a road object is removed. 
   @Override public void bulldoze() {
      GridLocation loc = getLocation();
      CellType celltype = underCell.getCellType();
      switch (celltype) {
         case DIRT:
            parent.getGrid().setCellAt(loc.x, loc.y, new Dirt(parent, loc));
            break;
         case WOODS:
            parent.getGrid().setCellAt(loc.x, loc.y, new Woods(parent, loc));
            break;
         case RIVER:
            parent.getGrid().setCellAt(loc.x,loc.y, new River(parent, loc));
         default:
                    
      }
   }
}
