/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

/**
 * A command object. It is assumed that a Command object contains all the
 * information required to run the command.
 */
public interface Command {

    /**
     * Run the command.
     *
     * If a CommandException is thrown, the message of the exception is
     * the error message.
     *
     * @throws CommandException if there is an error running the command
     */
    void run() throws CommandException;
}
