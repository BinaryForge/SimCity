/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.GridLocation;
import cs345.model.GridRectangle;
import cs345.model.Simulator;
import cs345.model.SimulatorTime;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Class for parsing commands.
 *
 * Unless otherwise noted, commands apply to both GUI runners and TEXT runners.
 *
 * The commands processed are:
 *
 * quit
 *     quits the game immediately
 * newgrid
 *     create a new grid for the game. This command is only valid if no steps
 *     have been done
 * step [n [period]]
 *     step the simulator the indicated number of steps. n is the number of
 *     steps, a positive integer. The number n can be followed by a period
 *     which is one of "week", "month", or "year" (case insensitive). If a
 *     period is specified, the simulator will be stepped n * (number of steps
 *     per period). If step is given with no additional arguments, n = 1 is
 *     used. The number of steps in a period is determined by the constants
 *     in Simulator.
 * steprate n
 *     Set the rate at which commands are stepped to one step every n
 *     milliseconds. n must be positive. GUI runners only.
 * show grid
 *     output the grid to the console. Text runners only.
 * show time
 *     output the current time to the console.
 * show population
 *     output the current population to the console.
 * show industrial
 *     output the current industry count to the console.
 * zone residential x y
 *     create a residential zone centered at the given grid coordinates
 * zone industrial x y
 *     create an industrial zone centered at the given grid coordinates
 * road x y w h
 *     build a road starting at the given x y with width w and height h. One
 *     of w and h must be one.
 * bulldoze x y w h
 *     bulldoze the rectangle bounded by the given coordinates. The coordinates
 *     are assumed to be corners of a rectangle. The left and upper edges of the
 *     rectangle are inclusive and the right and lower edges are exclusive.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class CommandParser {

    /* The parent runner for this parser. */
    private Runner parent;

    CommandParser(Runner parent) {
        this.parent = parent;
    }

    /**
     * This class represents a single alternative for a command or a
     * subcommmand. It is assumed that this class will be extended with
     * additional attributes for specific uses.
     *
     * command is the String represented the command, subcommand, or option.
     *
     * minChars is the minimum number of characters that must match.
     */
    private static class CommandString {
        final String command;
        final int minChars;

        CommandString(String command, int minChars) {
            this.command = command;
            this.minChars = minChars;
        }
    }

    /**
     * Find the CommandString object in the collection corresponding to word.
     *
     * word will be converted to lower case so that matches are case
     * insensitive.
     *
     * If multiple objects match (probably should not occur), the first matching
     * object will be returned.
     * @param list a collection of CommandString objects
     * @param word the word to be matched
     * @param <T> the actual class of the object in the collection
     * @return the found object
     */
    private static <T extends CommandString> T findCommand(Collection<T> list, String word) {
        T found = null;
        word = word.toLowerCase();
        for (T data : list) {
            if (word.length() >= data.minChars && data.command.startsWith(word)) {
                found = data;
                break;
            }
        }
        return found;
    }

    /* Interface for command objects. */
    @FunctionalInterface
    private interface CommandProcessor {
        Command process(CommandParser parser, String command, String[] words)
                throws CommandException;
    }

    /**
     * CommandData is a CommandString extension that include a CommandProcessor
     * attribute representing the method that is to be used to process and
     * execute the command.
     */
    private static class CommandData extends CommandString {
        final CommandProcessor processor;

        CommandData(String command, int minChars, CommandProcessor processor) {
            super(command, minChars);
            this.processor = processor;
        }
    }

    /* CommandData object for all commands. */
    private static final Collection<CommandData> COMMANDS =
            Collections.unmodifiableCollection(Arrays.asList(
                    new CommandData("quit", 1, CommandParser::processQuit),
                    new CommandData("newgrid", 3, CommandParser::newGridCommand),
                    new CommandData("step", 2, CommandParser::stepCommand),
                    new CommandData("steprate", 6, CommandParser::stepRateCommand),
                    new CommandData("show", 2, CommandParser::showCommand),
                    new CommandData("zone", 2, CommandParser::zoneCommand),
                    new CommandData("bulldoze", 3, CommandParser::bulldozeCommand),
                    new CommandData("road", 3, CommandParser::roadCommand)
            ));

    /* Process a single command. */
    Command parseCommand(String[] words) throws CommandException {
        CommandData foundCommand = findCommand(COMMANDS, words[0]);
        if (foundCommand != null) {
            return foundCommand.processor.process(this, foundCommand.command, words);
        } else {
            throw new CommandException("Don't know command %s", words[0]);
        }
    }

    /* Process a quit command. */
    private Command processQuit(String command, String[] words) throws CommandException {
        if (words.length > 1) {
            throw new CommandException("Extra words in %s command", command);
        } else {
            return new QuitCommand(parent);
        }
    }

    /* Process a newgrid command. */
    private Command newGridCommand(String command, String[] words) throws CommandException {
        if (words.length > 1) {
            throw new CommandException("Extra words in %s command", command);
        } else {
            return new NewGridCommand(parent, false);
        }
    }

    /**
     * TimeInterval is a CommandString extension for time intervals. This
     * class has an additional attribute representing the number of periods
     * in the interval.
     */
    private static class TimeInterval extends CommandString {
        final int interval;

        TimeInterval(String word, int minChars, int interval) {
            super(word, minChars);
            this.interval = interval;
        }
    }

    /* TimeInterval objects for all time intervals. */
    private static final Collection<TimeInterval> TIME_INTERVALS =
            Collections.unmodifiableCollection(Arrays.asList(
                    new TimeInterval("week", 1, SimulatorTime.WEEK),
                    new TimeInterval("month", 1, SimulatorTime.MONTH),
                    new TimeInterval("year", 1, SimulatorTime.YEAR)
            ));

    /* Process a step command. */
    private Command stepCommand(String command, String[] words) throws CommandException {
        int n = 1;
        int interval = 1;
        if (words.length > 1) {
            // Convert n
            try {
                n = Integer.parseInt(words[1]);
            } catch (NumberFormatException ex) {
                throw new CommandException("Not a number: %s", words[1]);
            }
            if (n <= 0) {
                throw new CommandException("Number of steps must be >= 0");
            }

            // Convert period
            if (words.length > 2) {
                TimeInterval intervalData = findCommand(TIME_INTERVALS, words[2]);
                if (intervalData == null) {
                    throw new CommandException("Not a valid interval: %s", words[2]);
                }
                interval = intervalData.interval * Simulator.STEPS_PER_PERIOD;

                if (words.length > 3) {
                    throw new CommandException("Step command has too many words");
                }
            }
        }
        return new StepCommand(parent, n, interval);
    }

    /* Process a steprate command. */
    private Command stepRateCommand(String command, String[] words) throws CommandException {
        if (words.length < 2)
            throw new CommandException("steprate requires a rate");
        if (words.length > 2)
            throw new CommandException("steprate command has too many words");
        int rate;
        try {
            rate = Integer.parseInt(words[1]);
        } catch (NumberFormatException ex) {
            throw new CommandException("Not a number: %s", words[1]);
        }
        if (rate <= 0) {
            throw new CommandException("Rate must be > 0");
        }
        return new StepRateCommand(parent, rate);
    }

    /* Interface for SubcommandData. */
    @FunctionalInterface
    private interface SubcommandProcessor {
        Command process(CommandParser parser, String command, String subcommand, String[] words)
                throws CommandException;
    }

    /** SubcommandData is an extension of CommandString for subcommands of a
     * command. This class has an attribute which represents the function to
     * be executed when the subcommand is recognized.
     */
    private static class SubcommandData extends CommandString {
        final SubcommandProcessor processor;

        SubcommandData(String subcommand, int minChars, SubcommandProcessor processor) {
            super(subcommand, minChars);
            this.processor = processor;
        }
    }

    /* Collection of subcommands for the show command. */
    private static final Collection<SubcommandData> SHOW_COMMANDS =
            Collections.unmodifiableCollection(Arrays.asList(
                    new SubcommandData("grid", 2, CommandParser::showGridCommand),
                    new SubcommandData("time", 2, CommandParser::showTimeCommand),
                    new SubcommandData("population", 3, CommandParser::showPopCommand),
                    new SubcommandData("industrial", 3, CommandParser::showIndCommand)
            ));

    /* Process a show command. */
    private Command showCommand(String command, String[] words) throws CommandException {
        if (words.length < 2) {
            throw new CommandException("No option specified for %s", command);
        }
        SubcommandData foundCommand = findCommand(SHOW_COMMANDS, words[1]);
        if (foundCommand != null) {
            return foundCommand.processor.process(this, command, foundCommand.command, words);
        } else {
            throw new CommandException("Don't know how to show %s", words[1]);
        }
    }

    /* Process a show grid command. */
    private Command showGridCommand(String command, String subcommand, String[] words) throws CommandException {
        if (words.length > 2) {
            throw new CommandException("Too many arguments for %s %s", command, subcommand);
        }
        return new ShowGridCommand(parent);
    }


    /* Process a show time command. */
    private Command showTimeCommand(String command, String subcommand, String[] words) throws CommandException {
        if (words.length > 2) {
            throw new CommandException("Too many arguments for %s %s", command, subcommand);
        }
        return new ShowTimeCommand(parent);
    }

    /* Process a show population command. */
    private Command showPopCommand(String command, String subcommand, String[] words) throws CommandException {
        if (words.length > 2) {
            throw new CommandException("Too many arguments for %s %s", command, subcommand);
        }
        return new ShowPopulationCommand(parent);
    }
    
    /* Process a show industrial command. */
    private Command showIndCommand(String command, String subcommand, String[] words) throws CommandException {
        if (words.length > 2) {
            throw new CommandException("Too many arguments for %s %s", command, subcommand);
        }
        return new ShowIndustrialCommand(parent);
    }
    
    /* Collections of subcommands for the zone command. */
    private static final Collection<SubcommandData> ZONE_COMMANDS =
            Collections.unmodifiableCollection(Arrays.asList(
                    new SubcommandData("residential", 3, CommandParser::zone2Command),
                    new SubcommandData("industrial", 3, CommandParser::zone2Command)
            ));

    /* Process the zone command. */
    private Command zoneCommand(String command, String[] words) throws CommandException {
        if (words.length < 2) {
            throw new CommandException("No option specified for %s", command);
        }
        SubcommandData foundCommand = findCommand(ZONE_COMMANDS, words[1]);
        if (foundCommand != null) {
            return foundCommand.processor.process(this, command, foundCommand.command, words);
        } else {
            throw new CommandException("Unknown zone type %s", words[1]);
        }
    }

    /* Process a zone command with a zoneType. */
    private Command zone2Command(String command, String zoneType, String[] words)
            throws CommandException {
        GridLocation loc = getGridLoc(words, 2);

        if (words.length > 4) {
            throw new CommandException("Too many arguments for %s %s", command, zoneType);
        }
        return new ZoneCommand(parent, zoneType, loc);
    }

    /* Process a bulldoze command. */
    private Command bulldozeCommand(String command, String[] words) throws CommandException {
        GridRectangle rect = getGridRectangle(words, 1);
        if (words.length > 5) {
            throw new CommandException("Too many arguments for %s", command);
        }
        return new BulldozeCommand(parent, rect);
    }
     /* Process a road command. */
    private Command roadCommand(String command, String[] words) throws CommandException {
        GridRectangle rect = getGridRectangle(words, 1);
        if (words.length > 5) {
            throw new CommandException("Too many arguments for %s", command);
        }
        return new RoadCommand(parent, rect);
    }

    /* Get a grid location from words starting at start. */
    private GridLocation getGridLoc(String[] words, int start) throws CommandException {
        if (words.length < start + 2) {
            throw new CommandException("Missing grid location");
        }
        try {
            int x = Integer.parseInt(words[start]);
            int y = Integer.parseInt(words[start + 1]);
            return new GridLocation(x, y);
        } catch (NumberFormatException ex) {
            throw new CommandException("Invalid grid location %s, %s", words[start], words[start + 1]);
        }
    }

    /* Get a grid location from words starting at start. */
    private GridRectangle getGridRectangle(String[] words, int start) throws CommandException {
        if (words.length < start + 4) {
            throw new CommandException("Missing grid rectangle");
        }
        try {
            int x = Integer.parseInt(words[start]);
            int y = Integer.parseInt(words[start + 1]);
            int w = Integer.parseInt(words[start + 2]);
            int h = Integer.parseInt(words[start + 3]);
            return new GridRectangle(x, y, w, h);
        } catch (NumberFormatException ex) {
            throw new CommandException("Invalid grid rectable (%s, %s) %sx%s",
                    words[start], words[start + 1], words[start + 2], words[start + 3]);
        }
    }

}
