/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

/**
 * An interface representing an action that can be performed by a Simulator.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public interface SimulatorAction {

    /**
     * Do the action.
     *
     * This method should return a re-schedule interval. If the reschedule
     * interval is <= 0, the action will not be rescheduled. If the interval
     * if >0, the event will be rescheduled that many steps in the future.
     *
     * @return the reschedule interval
     */
    int doAction();
}
