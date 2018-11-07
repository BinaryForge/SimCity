/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

import cs345.model.cell.CellType;
import cs345.model.cell.Dirt;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Model for the CS345Opolis game.
 *
 * The model acts as a container for the components of the game. The Simulator
 * and Grid for the game are attributes of this class and can be retrieved
 * by calling the getSim and getGrid methods.
 *
 * This class is the holder for global properties of the game, such as
 * the total residential population and the random number generator used by
 * the game.
 *
 * Finally, this class is where global SimulationActions, are located. (See
 * PeriodInitAction and PeriodEndAction.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class Cs345Opolis {

   /* Names for initialization properties */
   public static final String GRID_WIDTH = "cs345opolis.grid.width";
   public static final String GRID_HEIGHT = "cs345opolis.grid.height";
   public static final String PRNG_SEED = "cs345opolis.randomseed";

   private Properties props;
   private Simulator sim;
   private Grid grid;
   private MapGenerator generator;

   // These really should be private, However, these are accessed from a
   // number of different places. So, these are now public.
   public int curResPop = 0; // Total residential population, start of period
   public int newResPop = 0; // Total residential population, accumulating
   public int curIndCount = 0; // Total number of industries, start of period
   public int newIndCount = 0; // Total number of industries, accumalating

   // Pseudo Random Number Generator for the model
   private Random prng;

   /**
    * This interface should be implemented by objects that listen for changes
    * to the Model.
    *
    * ModelListeners are added for this model by calling addListener.
    *
    * Note that all methods have a default implementation that does nothing.
    * This allows listeners to only implement those events that are of
    * interest to them.
    */
   public interface ModelListener {
      /**
       * Called when one or more cells in the grid have changed type. The cells
       * that have changed are defined as the  rectangular region given
       * by the GridRectangle rect.
       *
       * @param rect the rectangle for the changed region
       */
      default void gridChanged(GridRectangle rect) { }
   
      /**
       * Called when the time changes, specifically, after a step command.
       */
      default void timeChanged() { }
   
      /**
       * Called when the population of the number of industries changes.
       */
      default void censusChanged() { }
   }

   /* The list of GridListeners. */
   private List<ModelListener> modelListeners= new ArrayList<>();

   /**
    * Add a listener for changes in the grid for this model.
    * @param listener the listener to be added
    */
   public void addListener(ModelListener listener) {
      modelListeners.add(listener);
   }

   /**
    * Factory function for constructing a new game.
    *
    * @param props the Properties object containing initialization values
    *              for the game model
    * @return the game object.
    */
   public static Cs345Opolis newCity(Properties props) {
      return new Cs345Opolis(props);
   }

   /**
    * Construct a CS345Opolis object.
    *
    * The constructed Grid object is all DIRT. It is up to the object creator,
    * Runner, to initialize the Grid.
    *
    * The simulator for the game is constructed and initialized with the
    * PeriodInitAction and PeriodEnd Action.
    *
    * The random number generator for the game is created. It either initialized
    * using the seed specified in the properties or, if the initialization
    * value is missing, uses the default initialization for the generator.
    */
   private Cs345Opolis(Properties props) {
      this.props = props;
      String prngSeed = props.getProperty(PRNG_SEED);
      if (prngSeed != null) {
         prng = new Random(Long.parseLong(prngSeed));
      } 
      else {
         prng = new Random();
      }
   
      newGrid();
      newSimulator();
   
      makeSimulatorActions();
   }

   private void makeSimulatorActions() {
      // Simulator always initializes with stepOffset == 0
      sim.addAction(sim.getCurrentTime(), new PeriodInitAction());
      sim.addAction(sim.getCurrentTime().nextStep(SimulatorTime.STEPS_PER_PERIOD - 1),
             new PeriodEndAction());
   }

   /**
    * Call the grid changed listeners for this model.
    * @param rect the rectangle designating the part of the grid that
    *             changed.
    *
    * TODO a good idea here would be to accumulate all the changes to the
    *     grid that have occurred during a command and to deduplicate those
    *     changes to minimize the number of calls to the listeners.
    */
   public void fireGridChanged(GridRectangle rect) {
      modelListeners.forEach(listener -> listener.gridChanged(rect));
   }

   /**
    * Call the time changed listeners for this model.
    */
   void fireTimeChanged() {
      modelListeners.forEach(ModelListener::timeChanged);
   }

   /**
    * Call the time changed listeners for this model.
    */
   void fireCensusChanged() {
      modelListeners.forEach(ModelListener::censusChanged);
   }

   /* Create a new grid object. The width and height are taken from the
    * properties object. The grid is initialized to all DIRT.
    */
   private void newGrid() {
      int width = Integer.parseInt(props.getProperty(GRID_WIDTH));
      int height = Integer.parseInt(props.getProperty(GRID_HEIGHT));
      grid = Grid.emptyGrid(this, width, height, Dirt::new);
   }

   private boolean gridSizeChanged() {
      return grid.getWidth() != Integer.parseInt(props.getProperty(GRID_WIDTH)) ||
             grid.getHeight() != Integer.parseInt(props.getProperty(GRID_HEIGHT));
   }

   /**
    * Generate a new grid initialized by either the current map generator or
    * a new map generator.
    *
    * A new map generator will always be created if newGenerator is true.
    * Additionally, a new generator will be created if the grid height and width
    * properties have changes or the existing generator returns true when
    * queried as to whether the map parameters have changed.
    *
    * @param newGenerator if true, a new generator will be created and used
    * @throws IllegalArgumentException if either the width or height is <= MIN_GRID_SIZE
    */
   void newMapGrid(boolean newGenerator) {
      if (gridSizeChanged()) {
         newGrid();
         newGenerator = true;
      }
      if (newGenerator || generator == null || !generator.sameProperties(props)) {
         generator = new MapGenerator(props);
      }
      generator.generateMap(this);
      fireGridChanged(new GridRectangle(0, 0, grid.getWidth(), grid.getHeight()));
   }

   /* Create a new Simulator for the game. */
   private void newSimulator() {
      sim = new Simulator(props);
   }

   /**
    * Return the associated Grid object for this game.
    * @return the Grid object
    */
   public Grid getGrid() {
      return grid;
   }

   /**
    * Step the model a single step.
    */
   void step() {
      sim.step();
      fireTimeChanged();
   }

   /**
    * Add the given action to the simulation. The action will be scheduled
    * for its first execution at the given time.
    * @param time the time for first execution of the action
    * @param action the action to be executed
    */
   public void addAction(SimulatorTime time, SimulatorAction action) {
      sim.addAction(time, action);
   }

   /**
    * Remove the given action from the simulation.
    * @param action the action to be executed
    */
   public void removeAction(SimulatorAction action) {
      sim.removeAction(action);
   }

   /**
    * Get the current time for the simulation.
    * @return a SimulatorTime object with the current time.
    */
   public SimulatorTime getCurrentTime() {
      return sim.getCurrentTime();
   }

   /**
    * Return a random int in the range 0 <= return value < n.
    *
    * The value returned is from the random number generator that is
    * associated with the game.
    *
    * @param n the range for the value to be returned
    * @return the random integer.
    */
   public int prngNextInt(int n) {
      return prng.nextInt(n);
   }

   /**
    * Return true is the given rectangle in the grid is buildable.
    *
    * A given cell is buildable if the isBuildable method on the cell returns
    * true. A region is buildable if all the cells in the regions are
    * buildable.
    *
    * If the region is not valid (extends outside the grid), false is returned.
    *
    * @param rect the GridRectangle to check
    * @return true if all cells in the rectangle are buildable
    */
   boolean isBuildable(GridRectangle rect) {
      if (!grid.validRegion(rect))
         return false;
      for (int col = rect.x; col < rect.x + rect.w; col++) {
         for (int row = rect.y; row < rect.y + rect.h; row++) {
            if (!grid.cellAt(col, row).isBuildable())
               return false;
         }
      }
      return true;
   }
    /**
    * Return true is the given rectangle in the grid is water.
    *
    * A given cell is water if the isWater method on the cell returns
    * true. A region is water if all the cells in the regions is
    * water.
    *
    * If the region is not valid (extends outside the grid), false is returned.
    *
    * @param rect the GridRectangle to check
    * @return true if all cells in the rectangle is water
    */
   boolean isWater(GridRectangle rect) {
      if (!grid.validRegion(rect))
         return false;
      for (int col = rect.x; col < rect.x + rect.w; col++) {
         for (int row = rect.y; row < rect.y + rect.h; row++) {
            if (!grid.cellAt(col, row).isWater())
               return false;
         }
      }
      return true;
   }
   
   //Method buildLocCheck
   //Return True if rect does not contain a Road, Indstrial, or Residential cell.
   boolean buildLocCheck(GridRectangle rect) {
      for (int col = rect.x; col < rect.x + rect.w; col++) {
         for (int row = rect.y; row < rect.y + rect.h; row++) {
            if (grid.cellAt(col, row).getCellType().toString() =="ROAD" 
            || grid.cellAt(col, row).getCellType().toString() =="INDUSTRIAL"
            || grid.cellAt(col, row).getCellType().toString() =="RESIDENTIAL"){
               return false;
            }
         }
      }
      return true;
   }

   /**
    * Return true is the given rectangle in the grid can be bulldozed..
    *
    * A given cell is bulldozeable if the isBulldozeable method on the cell returns
    * true. A region is bulldozeable if all the cells in the regions are
    * bulldozeable.
    *
    * If the region is not valid (extends outside the grid), false is returned.
    *
    * @param rect the GridRectangle to check
    * @return true if the region is bulldozeable, otherwise false
    */
   boolean isBulldozeable(GridRectangle rect) {
      if (!grid.validRegion(rect))
         return false;
      for (int col = rect.x; col < rect.x + rect.w; col++) {
         for (int row = rect.y; row < rect.y + rect.h; row++) {
            if (!grid.cellAt(col, row).isBulldozeable())
               return false;
         }
      }
      return true;
   }

   /**
    * Bulldoze the given region. If the region is not valid (extends outside)
    * the grid, an IndexOutOfBoundsException will be thrown.
    * @param rect the GridRectangle to bulldoze
    */
   public void bulldoze(GridRectangle rect) {
      for (int col = rect.x; col < rect.x + rect.w; col++) {
         for (int row = rect.y; row < rect.y + rect.h; row++) {
            Cell cell = grid.cellAt(col, row);
            cell.bulldoze();
         }
      }
      fireGridChanged(rect);
   }

   /**
    * Perform initialization actions at the start of a period.
    *
    * This action is run at step 0 of each simulation period. It's purpose
    * is to do initialization for the beginning of a period.
    */
   private class PeriodInitAction implements SimulatorAction {
      @Override public int doAction() {
         newResPop = 0;
         newIndCount = 0;
      
         // Reschedule to run at beginning of next period
         return Simulator.STEPS_PER_PERIOD;
      }
   }

   /**
    * Perform actions at the end of a period.
    *
    * This action is run at the last step of each simulation period. It's
    * purpose is to do cleanup/finalization for the period.
    */
   private class PeriodEndAction implements SimulatorAction {
      @Override public int doAction() {
         curResPop = newResPop;
         curIndCount = newIndCount;
         fireCensusChanged();
      
         // Reschedule to run at end of next period
         return Simulator.STEPS_PER_PERIOD;
      }
   }
}
