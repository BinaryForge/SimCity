/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;

import javafx.application.Platform;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Run a CS345Opolis game from the command line.
 *
 * This class runs a game. The actual handling of input and output is provided
 * by the GuiView instance that is created by the call to Application.launch
 * in the run method.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class GuiRunner extends Application implements Runner {

    /* Property names for step rate properties. */
    public static final Map<RunCommand.StepRate, String> stepRatePropMap;

    static {
        Map<RunCommand.StepRate, String> rateMap = new HashMap<>();
        rateMap.put(RunCommand.StepRate.SLOW, "cs345opolis.slowinterval");
        rateMap.put(RunCommand.StepRate.NORMAL, "cs345opolis.stepinterval");
        rateMap.put(RunCommand.StepRate.FAST, "cs345opolis.fastinterval");
        rateMap.put(RunCommand.StepRate.VERYFAST, "cs345opolis.vertfastinterval");
        stepRatePropMap = Collections.unmodifiableMap(rateMap);
    }

    private Map<RunCommand.StepRate, Integer> stepRateMap = new HashMap<>();

    public static final String FXML_RESOURCE = "cs345opolis.guifxml";

    /* Default properties for the game. */
    private static Properties defaults;

    /* Model factory for creating a game. */
    private static ModelFactory modelFactory;

    /* The game object that is controlled by this runner. */
    private Cs345Opolis model;

    /* The associated GuiView object. */
    private GuiView view;

    /* The command parser. */
    private CommandParser parser;

    /* Set to true when it is time to quit the game. */
    private boolean quit = false;

    /* Variables for map generation.
     *
     * mapGen is the generator for the game. A single generator is used which
     * allows to generation of new maps each time the generator is used.
     *
     * newGridOK is set to false once the grid is stepped.
     */
    private MapGenerator mapGen = null;
    private BooleanProperty newGridOK = new SimpleBooleanProperty(true);

    /* A RunCommand object for use for running the model. */
    private RunStopCommand runCommand;

    /* A TimeLine object for the run command. */
    private Timeline modelRunTimeline;

    /* The step rate for stepping the model. This is the number of milliseconds
     * between steps commands.
     */
    private int stepRate = 125;

    /**
     * Start a GuiRunner.
     *
     * JavaFX applications have no constructor of interest. The object is
     * constructed by the static Application.launch method which is passed the
     * class of the Application sub-class that is to be created. The init method
     * fills the place of the constructor. It is called to do any required
     * initialization before the start method is called.
     *
     * When the launch method is called, a GuiRunner instance is created and the
     * init and start methods are called to complete the initialization and start
     * the application.
     *
     * @param args command line arguments to be passed to launch
     * @param factory a ModelFactory object that is called to construct the
     *                actual model
     * @param defaults the default properties to be used
     */
    public static void startRunner(String[] args, ModelFactory factory, Properties defaults) {
        GuiRunner.defaults = defaults;
        modelFactory = factory;

        launch(args);
    }

    @Override public void init() {
        /* Setup step rates */
        int stepRateNormal = getStepRate(RunCommand.StepRate.NORMAL, 125);
        getStepRate(RunCommand.StepRate.SLOW, 250);
        getStepRate(RunCommand.StepRate.FAST, stepRateNormal / 5);
        getStepRate(RunCommand.StepRate.VERYFAST, 5);

        /* Object initialization */
        this.model = modelFactory.makeModel(defaults);
        this.parser = new CommandParser(this);
    }

    @Override public void start(Stage stage) throws Exception {
        /* Create and initialize the GuiView */
        String resFileName = defaults.getProperty(FXML_RESOURCE);
        if (resFileName == null) {
            throw new AssertionError("Missing FXML resource in properties file");
        }
        URL resURL = getClass().getResource(resFileName);
        FXMLLoader loader = new FXMLLoader(resURL);
        view = new GuiView(this);
        loader.setController(view);
        loader.load();
        view.initView(stage);
        view.getNewGridDisabled().bind(newGridOK.not());
        runCommand(new NewGridCommand(this, true));
    }

    private int getStepRate(RunCommand.StepRate rate, int defaultRate) {
        String propertyRate = defaults.getProperty(stepRatePropMap.get(rate));
        int stepRate;
        if (propertyRate != null) {
            stepRate = Integer.parseInt(propertyRate);
        } else {
            stepRate = defaultRate;
        }
        stepRateMap.put(rate, stepRate);
        return stepRate;
    }

    /** Get the GuiRunner for this runner.
     *
     * @return the GuiRunner object or null is this is not a GuiRunner.
     */
    @Override public GuiRunner getGuiRunner() {
        return this;
    }

    /** Get the Cs345Opolis model object
     */
    public Cs345Opolis getModel() {
        return model;
    }

    /**
     * Get the associated default value.
     */
    public String getDefault(String name) {
        return defaults.getProperty(name);
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
     * Load an image.
     * @param propName property name with the resource name
     * @return the constructed Image object
     */
    Image loadImage(String propName) {
        String imageResName = getDefault(propName);
        if (imageResName == null) {
            throw new AssertionError("No Image property " + propName);
        }
        InputStream imageStream = GuiRunner.class.getResourceAsStream(imageResName);
        if (imageStream == null) {
            throw new AssertionError("Couldn't access image resource " + imageResName);
        }
        return new Image(imageStream);
    }

    /** Get the step rate for a given RunCommand.StepRate */
    public int getStepRate(RunCommand.StepRate rate) {
        return stepRateMap.get(rate);
    }

    /** Get the step rate that currently is being used */
    public int getStepRate() {
        return stepRate;
    }

    /** Set the step rate to be used by the step command. */
    public void setStepRate(int rate) {
        stepRate = rate;
    }

    /** Set quit to true. */
    @Override public void setQuit() {
        quit = true;
        Platform.exit();
    }

    /**
     * Run a list of Strings as a command.
     *
     * @param words the list of strings which is the command
     */
    public void runCommand(String[] words) {
        try {
            Command cmd = parser.parseCommand(words);
            cmd.run();
        } catch (CommandException ex) {
            view.message(ex.getMessage());
        }
    }

    /**
     * Run a Command object.
     *
     * @param cmd the command object to be run
     */
    public void runCommand(Command cmd) {
        try {
            cmd.run();
        } catch (CommandException ex) {
            view.message(ex.getMessage());
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

    /** Determine if initMap is available.
     *
     * @return true if initMap will initialize a new map
     */
    @Override public boolean newMapOK() {
        return newGridOK.get();
    }

    /** Execute a RunCommand
     *
     * If there is a timeline already running, this method will set the
     * runCommand attribute that will be recognized by the stepModel method
     * at the next available opportunity.
     *
     * If there is no timeline running, this command will cause one to be
     * started.
     *
     * @param cmd the RunCommand to be executed
     * @throws CommandException if there is another RunCommand waiting to be
     * processed
     */
    public void executeRun(RunStopCommand cmd) throws CommandException {
        if (runCommand != null) {
            throw new CommandException("Run command still being processed");
        }
        if (modelRunTimeline != null) {
            runCommand = cmd;
        } else {
            if (cmd.isStopRun()) {
                throw new CommandException("Model is not running");
            }
            runModel(cmd.getNumSteps(), cmd.getStepRate());
        }
    }

    /** Step the game the given number (num) of intervals.
     *
     * Stepping is accomplished by creating a timeline object that repeatedly
     * steps the game one step.
     *
     * @param num number of intervals
     * @param interval size of an interval
     */
    @Override public void step(int num, int interval) throws CommandException {
        executeRun(new RunCommand(this, stepRate, num * interval));
    }

    /** Step the game either a fixed number of steps or indefinitely.
     * @param cycles the number of cycles to run. Animation.INDEFINITE to run
     *     indefinitely.
     * @param stepMillis the number of milliseconds between steps.
     */
    private void runModel(int cycles, int stepMillis) {
        newGridOK.set(false); // running the model invalidates new grid
        modelRunTimeline = new Timeline(
                new KeyFrame(Duration.millis(stepMillis), this::stepModel));
        modelRunTimeline.setCycleCount(cycles);
        modelRunTimeline.setOnFinished(event -> { modelRunTimeline = null; });
        modelRunTimeline.play();
    }

    /* Run a single step of the model. Used by runModel, above. */
    private void stepModel(ActionEvent event) {
        if (runCommand != null && !runCommand.isStopRun()) {
            // Restart the model animation with a new set of parameters
            // A command will either stop the timeline or restart it
            modelRunTimeline.stop();
            modelRunTimeline = null;

            Platform.runLater(() -> {
                    runModel(runCommand.getNumSteps(), runCommand.getStepRate());
                    runCommand = null;
            });
        } else {
            try {
                runModelCommand(new ModelStepCommand());
            } catch (CommandException ex) {
                throw new AssertionError("Unexpected exception from step command", ex);
            }
            // Always stop at step zero
            if (runCommand != null && runCommand.isStopRun() &&
                    model.getCurrentTime().getTimeData().step == 0) {
                modelRunTimeline.stop();
                modelRunTimeline = null;
                runCommand = null;
            }
        }
    }
}
