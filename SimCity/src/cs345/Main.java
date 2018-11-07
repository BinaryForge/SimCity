/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345;

import cs345.model.ModelFactory;
import cs345.runner.GuiRunner;
import cs345.runner.Runner;
import cs345.runner.TextRunner;

import java.io.InputStream;
import java.util.Properties;

/* Main class for the CS345Opolis game.
 *
 * This class performs the following actions:
 *   1) Fetch the properties file and parse it.
 *   2) construct an instance of a TextRunner or GuiRunner class and call
 *      the run method on that instance.
 *
 * The globals defaults, modelFactory, and runner are provided to allow
 * other application components to access these variables.
 */
public class Main {

    public static String PROPERTIES_FILE = "cs345opolis.properties";
    public static String RUNNER = "cs345opolis.runner";
    public static String TEXT_RUNNER = "TEXT";
    public static String GUI_RUNNER = "GUI";

    private static Properties commandLineDefines = new Properties();
    private static Properties defaults;

    private static Runner runner;

    /**
     * Process the command line arguments. Get the external Properties file and
     * parse it. Create the needed runner and call it to run the game..
     *
     * @param args command line arguments (unused)
     * @throws Exception allows Exceptions to pass out of main
     */
    public static void main(String[] args) throws Exception {
        if (!processArgs(args)) {
            System.err.println("Error processing arguments");
            return;
        }

        InputStream propsFile = Main.class.getResourceAsStream(PROPERTIES_FILE);
        if (propsFile == null) {
            throw new AssertionError("Could not access properties file: " + PROPERTIES_FILE);
        }
        defaults = new Properties();
        defaults.load(propsFile);
        defaults.putAll(commandLineDefines);

        String runType = defaults.getProperty(RUNNER, TEXT_RUNNER);
        if (runType.equals(TEXT_RUNNER)) {
            TextRunner runner = new TextRunner(new ModelFactory(), defaults);
            runner.run(args);
        } else if (runType.equals(GUI_RUNNER)) {
            GuiRunner.startRunner(args, new ModelFactory(), defaults);
        } else {
            throw new IllegalStateException("Invalid Runner Type: " + runType);
        }
    }

    /* Internal routine for processing command line arguments.
     *
     * Look for arguments of the form -Dname=value. The property with name name
     * will be added to defaults with the value value. This will override
     * any definition that might have appeared in the properties file.
     */
    private static boolean processArgs(String[] args) {
        boolean result = true;
        for (String arg : args) {
            if (arg.startsWith("-D")) {
                String defString = arg.substring(2);
                int eqindex = defString.indexOf('=');
                if (eqindex == -1) {
                    System.err.println("Illegal property definition: " + arg);
                    result = false;
                }
                String propName = defString.substring(0, eqindex);
                String propValue = defString.substring(eqindex + 1);
                commandLineDefines.setProperty(propName, propValue);
            } else {
                System.err.println("Unknown property: " + arg);
                result = false;
            }
        }
        return result;
    }
}
