/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

/**
 * Time for the simulation.
 *
 * Simulator time is maintained in Simulator steps. There are STEPS_PER_PERIOD
 * steps per time period. A time period is one week. Months are exactly four
 * weeks and years are exactly twelve months
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class SimulatorTime {

    /* STEPS_PER_PERIOD copied from Simulator. */
    public static final int STEPS_PER_PERIOD = Simulator.STEPS_PER_PERIOD;

    /* Public constants for measuring periods. */
    public static final int WEEK = 1;
    public static final int WEEKS_PER_MONTH = 4;
    public static final int MONTH = WEEKS_PER_MONTH * WEEK;
    public static final int MONTHS_PER_YEAR = 12;
    public static final int YEAR = MONTHS_PER_YEAR * MONTH;

    /**
     * Enumeration naming the months.
     */
    public enum Month {
        JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC
    }

    /**
     * Public class for times broken down into their components.
     */
    public static class TimeData {
        public final int year;
        public final Month month;
        public final int week;
        public final int step;

        /**
         * Create a time date object at step zero of the date.
         * @param year the year
         * @param month the month
         * @param week the week
         */
        public TimeData(int year, Month month, int week) {
            this(year, month, week, 0);
        }

        /**
         * Create a time date object at given step of the date.
         * @param year the year
         * @param month the month
         * @param week the week
         * @param step the step of the time
         */
        public TimeData(int year, Month month, int week, int step) {
            this.year = year;
            this.month = month;
            this.week = week;
            this.step = step;
        }

        /**
         * Provide a string form of the date as MMM DD/step YYYY.
         * @return the String for the date
         */
        @Override public String toString() {
            return String.format("%s %d/%d, %d",
                    month, week + 1, step, year);
        }

        /**
         * Equals comparison. TimeData objects are equal if all the fields
         * are equal.
         * @param other the object to compare to
         * @return true if equal, otherwise false
         */
        @Override public boolean equals(Object other) {
            if (!(other instanceof TimeData))
                return false;
            TimeData otherData = (TimeData) other;
            return otherData.year == this.year && otherData.month == this.month &&
                    otherData.week == this.week && otherData.step == this.step;
        }

        /**
         * Provide a hashcode for TimeData objects.
         * @return the hashcode
         */
        @Override public int hashCode() {
            return ((((31 + year) * 31 + month.ordinal()) * 31) + week) * 31 + step;
        }

        /**
         * Return the difference between two TimeData objects in Simulator steps.
         * The time returned is this-other
         * @param other the TimeData object to be subtracted from this one
         * @return the difference
         */
        public int diff(TimeData other) {
            return STEPS_PER_PERIOD *
                    (YEAR * (this.year - other.year) +
                            MONTH * (this.month.ordinal() - other.month.ordinal()) +
                            WEEK * (this.week - other.week)) +
                    this.step - other.step;
        }
    }

    /* Simulator times are relative to an epoch. */
    private final TimeData epoch;
    private final int stepOffset;

    /* private constructor. See factory functions getAbsoluteTime, nextStep. */
    private SimulatorTime(TimeData epoch, int stepOffset) {
        this.epoch = epoch;
        this.stepOffset = stepOffset;
    }

    /** Convert a SimulatorTime to a String. */
    @Override public String toString() {
        return getTimeData().toString();
    }

    /** Compare simulator objects. Return true if they have the same epoch and
     * offset.
     */

    @Override public boolean equals(Object other) {
        if (!(other instanceof SimulatorTime))
            return false;
        SimulatorTime otherTime = (SimulatorTime)other;
        return this.diff(otherTime) == 0;
    }

    /**
     * Compute a hashCode for a SimulatorTime object.
     * @return the hash code
     */
    @Override public int hashCode() {
        return epoch.hashCode() * 31 + stepOffset;
    }

    /**
     * Get the absolute SimulatorTime.
     *
     * Return a SimulatorTime object representing the given offset from the
     * given epoch. If the epoch is other than the start of a period, the
     * epoch is reset to the start of the period.
     * @param epoch the epoch for the time
     * @param stepOffset the offset from the epoch
     * @return the time object
     */
    public static SimulatorTime getAbsoluteTime(TimeData epoch, int stepOffset) {
        int offset = epoch.step % STEPS_PER_PERIOD;
        if (offset != 0) {
            epoch = new TimeData(epoch.year, epoch.month, epoch.week,
                    epoch.step - offset);
        }
        return new SimulatorTime(epoch, stepOffset);
    }

    /**
     * Get a Simulator time representing the next occurrence of that step in
     * the current period.
     *
     * This call is equivalent to nextStep(stepNumber, STEPS_PER_PERIOD).
     * @param stepNumber the number of the step in a period
     * @return the time
     */
    public SimulatorTime nextStep(int stepNumber) {
        return nextStep(stepNumber, STEPS_PER_PERIOD);
    }

    /**
     * Get a Simulator time representing the next occurrence of that step at the
     * given modulus relative to this time.
     *
     * @param stepNumber the number of the step in a period
     * @return the time
     */
    public SimulatorTime nextStep(int stepNumber, int modulus) {
        if (modulus <= 0 || stepNumber <= 0)
            throw new IllegalArgumentException("modulus and stepNumber must be > 0");
        stepNumber = stepNumber % modulus;
        int curModulus = stepOffset % modulus;
            int newOffset = stepOffset + stepNumber - curModulus;
        if (curModulus >= stepNumber)
            newOffset += modulus;
        return new SimulatorTime(epoch, newOffset);
    }

    /**
     * Compute the difference of two SimulatorTime objects.
     * @param other the other SimulatorTime
     * @return difference
     */
    public int diff(SimulatorTime other) {
        return this.epoch.diff(other.epoch) + this.stepOffset - other.stepOffset;
    }

    /**
     * Get a time data object for the current time.
     * @return The time data object.
     */
    public TimeData getTimeData() {
        int totalStep = epoch.step + stepOffset;
        int step = totalStep % STEPS_PER_PERIOD;
        int totalWeek = epoch.week + totalStep / STEPS_PER_PERIOD;
        int week = totalWeek % WEEKS_PER_MONTH;
        int totalMonth = epoch.month.ordinal() + totalWeek / WEEKS_PER_MONTH;
        Month month = Month.values()[totalMonth % MONTHS_PER_YEAR];
        int year = epoch.year + totalMonth / MONTHS_PER_YEAR;
        return new TimeData(year, month, week, step);
    }
}
