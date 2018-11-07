/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

/**
 * If a command on the cannot be processed, a ModelCommandException is thrown.
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class ModelCommandException extends Exception {

    /**
     * Construct a CommandException
     * @param msg the error message for the exception
     */
    ModelCommandException(String msg) {
        super(msg);
    }

    /**
     * Construct a CommandException object.
     * @param format the format for the error message
     * @param args arguments to complete the error message format
     */
    ModelCommandException(String format, Object... args) {
        super(String.format(format, args));
    }

}

