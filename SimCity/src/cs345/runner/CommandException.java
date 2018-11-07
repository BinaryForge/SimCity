/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.ModelCommandException;

/**
 * If a command cannot be processed, a CommandException is thrown.
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class CommandException extends Exception {

    ModelCommandException cause = null;

    /**
     * Construct a CommandException
     * @param msg the error message for the exception
     */
    CommandException(String msg) {
        super(msg);
    }

    /**
     * Construct a CommandException object.
     * @param format the format for the error message
     * @param args arguments to complete the error message format
     */
    CommandException(String format, Object... args) {
        super(String.format(format, args));
    }

    /**
     * Construct a CommandException object from a ModelCommandException
     * @param cause the causing ModelCommandException
     */
    CommandException(ModelCommandException cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

}

