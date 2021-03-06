/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

/**
 * A quit Command
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class QuitCommand implements Command {

    private Runner runner;

    QuitCommand(Runner runner) {
        this.runner = runner;
    }

    @Override
    public void run() {
        runner.setQuit();
    }
}