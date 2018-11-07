/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Properties;

/**
 * Simulator used by the game.
 *
 * The simulator executes SimulationActions that have been scheduled with the
 * simulator. SimulatorActions are scheduled for a current or future step. When
 * the step() method is called, the simulator executes all actions for the
 * current step.
 *
 * A simulator action is executed by calling its doAction method. The doAction
 * method returns an int. If the int is positive, the action is rescheduled that
 * number of steps in the future. If the int is zero or negative, the action is
 * forgotten.
 *
 * The clock for the game is maintained by the SimulatorTime class. The constant
 * STEPS_PER_PERIOD determines the number of simulator steps per period. In
 * the game, a period is one week.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class Simulator {

    /* The epoch is the start of the simulation. */
    public static final String SIMULATOR_YEAR_PROP = "cs345opolis.simulator.epochYear";
    public static final String SIMULATOR_MONTH_PROP = "cs345opolis.simulator.epochMonth";
    public static final String SIMULATOR_WEEK_PROP = "cs345opolis.simulator.epochWeek";

    public static final int STEPS_PER_PERIOD = 8;

    private SimulatorTime.TimeData epochDate;
    private SimulatorTime startTime;

    // One up number updated each time an entry is added to the priority queue.
    // This is used as a tie-breaker for queue ordering.
    private int nextSequence = 0;

    /**
     * This is an entry in the timer queue. The entry contains the step
     * when the action is to be executed, the sequence number of the action
     * and the action itself.
     */
    private static class QEntry {
        int atStep;
        int sequence;
        SimulatorAction action;

        QEntry(int atStep, int sequence, SimulatorAction action) {
            this.atStep = atStep;
            this.sequence = sequence;
            this.action = action;
        }
    }

    /* The current step. Step zero is the first period on the epoch date. */
    private int curStep = 0;

    /* The priority queue of all queued actions. */
    private PriorityQueue<QEntry> queue;

    /**
     * Create a simulator object.
     * @param props the properties object with default values for the
     *              simulator
     */
    Simulator(Properties props) {
        setStartTime(props);
        queue = new PriorityQueue<>(
                // Comparator for QEntrys
                new Comparator<QEntry>() {
                    @Override public int compare(QEntry obj1, QEntry obj2) {
                        int stepDif = obj1.atStep - obj2.atStep;
                        if (stepDif != 0)
                            return stepDif;
                        return obj1.sequence - obj2.sequence;
                    }
                }
        );
    }

    /* Set the start time (epoch date) for the simulation. */
    private void setStartTime(Properties props) {
        int epochYear = Integer.parseInt(
                props.getProperty(SIMULATOR_YEAR_PROP, "2000"));
        SimulatorTime.Month epochMonth = SimulatorTime.Month.valueOf(
                props.getProperty(SIMULATOR_MONTH_PROP, "JAN"));
        int epochWeek = -1 + Integer.parseInt(
                props.getProperty(SIMULATOR_WEEK_PROP, "1"));
        epochDate = new SimulatorTime.TimeData(epochYear, epochMonth, epochWeek);
        startTime = SimulatorTime.getAbsoluteTime(epochDate, 0);
    }

    /**
     * Return the start time (epoch date) of the simulation.
     * @return the start time
     */
    public SimulatorTime getStartTime() {
        return startTime;
    }

    /**
     * Return the current time for the simulation.
     * @return the current time
     */
    public SimulatorTime getCurrentTime() {
        return SimulatorTime.getAbsoluteTime(epochDate, curStep);
    }

    /**
     * Add the given action to the simulation. The parameter time is the time
     * for the first execution of the action.
     * @param time the time of the action
     * @param action the action to be executed
     */
    public void addAction(SimulatorTime time, SimulatorAction action) {
        int atStep = time.diff(getStartTime());
        assert atStep >= curStep : "Scheduling event in past";
        queue.add(new QEntry(atStep, nextSequence++, action));
    }

    /**
     * Remove the given action from the simulation. If the action occurs
     * multiple times in the simulation, only one occurrence will be
     * removed.
     * @param action the action to be removed
     */
    public void removeAction(SimulatorAction action) {
        for (Iterator<QEntry> iter = queue.iterator(); iter.hasNext();) {
            QEntry entry = iter.next();
            if (entry.action.equals(action)) {
                iter.remove();
                break;
            }
        }
    }

    /* Step one step of the simulation. All actions scheduled to executed
     * at the current step are removed from the queue and executed. Actions
     * are rescheduled if requested.
     */
    public void step() {
        while (!queue.isEmpty() && queue.peek().atStep <= curStep) {
            int size = queue.size();
            QEntry entry = queue.poll();
            assert queue.size() == size - 1;
            int resched = entry.action.doAction();
            if (resched > 0) {
                entry.atStep += resched;
                queue.add(entry);
            }
        }
        curStep++;
    }
}
