/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.*;

import java.io.PrintStream;
import java.util.*;

/**
 * Run a CS345Opolis game from the command line.
 *
 * This class runs a game. Input is taken from System.in and all output goes
 * to System.out. Command input and text output are handled by the TextView
 * class.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class TextRunner implements Runner {

    private Properties props;

    private final Cs345Opolis model;

    private boolean quit = false;

    private MapGenerator mapGen = null;
    private boolean newGridOK = true;

    private Scanner input;
    private PrintStream output;

    private final TextView view;

    /**
     * Create a text runner on a model from the given factory.
     * @param factory a factory that creates a model object
     */
    public TextRunner(ModelFactory factory, Properties props) {
        this.props = props;
        this.model = factory.makeModel(this.props);
        this.input = new Scanner(System.in);
        this.output = System.out;
        this.view = new TextView(this, model, input, output);
    }

    /** Get the TextRunner for this runner.
     *
     * @return the TextRunner object or null is this is not a GuiRunner.
     */
    @Override public TextRunner getTextRunner() {
        return this;
    }

    /**
     * Run the text runner.
     *
     * The text runner will ask the user for input until input exhausts or a
     * quit command is received. The commands will be applied to the model
     * that constructed the runner.
     *
     * Command input will be taken from System.in and all output is sent to
     * System.out.
     */
    public void run(String[] args) {
        initMap();
        view.welcomeMessage();
        while (!quit) {
            try {
                Command cmd = view.getCommand();
                if (cmd != null)
                    cmd.run();
                else
                    // No input
                    break;
            } catch (CommandException ex) {
                view.message(ex.getMessage());
            }
        }
    }

    @Override
    public void runModelCommand(ModelCommand command) throws CommandException {
        try {
            command.run(this.model);
        } catch (ModelCommandException ex) {
            throw new CommandException(ex);
        }
    }


    /** Get the Cs345Opolis game object
     */
    public Cs345Opolis getModel() {
        return model;
    }

    /* Set quit to be true. */
    @Override public void setQuit() {
        quit = true;
    }

    /**
     * Output a message. format and args are printf-like parameters. A message
     * takes a single line on the output
     * @param format the format of the message
     * @param args the arguments for the format
     */
    @Override
    public void message(String format, Object... args) {
        view.message(format, args);
    }

    /**
     * Display the grid.
     */
    public void displayGrid() {
        view.displayGrid();
    }

    /** Determine if initMap is available.
     *
     * @return true if initMap will initialize a new map
     */
    @Override public boolean newMapOK() {
        return newGridOK;
    }

    /* Initialize a map. */
    private void initMap() {
        try {
            runModelCommand(new ModelNewGridCommand(true));
        } catch (CommandException ex) {
            throw new AssertionError("Unexpected exception from new grid command", ex);
        }
    }

    /** Step the game the given number (num) of intervals.
     *
     * @param num number of intervals
     * @param interval size of an interval
     */
    @Override public void step(int num, int interval) {
        newGridOK = false; // step command invalidates new grid
        ModelCommand stepCommand = new ModelStepCommand();
        for (int step = 0; step < num * interval; step++) {
            try {
                runModelCommand(stepCommand);
            } catch (CommandException ex) {
                throw new AssertionError("Unexpected exception from step command", ex);
            }
        }
    }
}
