/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

/**
 * A command object. It is assumed that a Command object contains all the
 * information required to run the command.
 */
public interface ModelCommand {

    /**
     * Run the command.
     *
     * If a ModelCommandException is thrown, the message of the exception is
     * the error message.
     *
     * @param model the model this command is to be run against
     *
     * @throws ModelCommandException if there is an error running the command
     */
    void run(Cs345Opolis model) throws ModelCommandException;
}
