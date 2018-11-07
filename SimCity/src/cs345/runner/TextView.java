/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.runner;

import cs345.model.*;
import cs345.model.cell.CellType;
import cs345.model.cell.Industrial;
import cs345.model.cell.Residential;
import cs345.model.cell.Road;

import java.io.PrintStream;
import java.util.*;

/**
 * Run a CS345Opolis game from the command line.
 *
 * This class handles text input and output for a game. A Scanner is used as
 * input and a PrintStream as output. Command parsing is handled by the
 * CommandParser class.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
class TextView {

    private final TextRunner parent;
    private final Cs345Opolis game;

    private final Scanner input;
    private final PrintStream output;

    private final CommandParser parser;

    /**
     * Create a TextView object on the given input and output.
     * @param game the Cs345Opolis game being viewed
     * @param input the Scanner used for input
     * @param output the PrintStream used for output
     */
    TextView(TextRunner parent, Cs345Opolis game, Scanner input, PrintStream output) {
        this.parent = parent;
        this.game = game;
        this.input = input;
        this.output = output;
        this.parser = new CommandParser(parent);
    }

    /**
     * Get a command from the input.
     *
     * @return A Command object if a valid command is input. null if there is
     *         no input available.
     * @throws CommandException if there is a problem processing an input
     */
    Command getCommand() throws CommandException {
        output.print("> ");
        while (input.hasNextLine()) {
            String line = input.nextLine().trim();
            if (line.length() != 0) {
                String[] words = line.split("\\s+");
                return parser.parseCommand(words);
            }
            output.print("> ");
        }
        return null;
    }

    /* Output a welcome message. */
    void welcomeMessage() {
        message("Welcome to CS345Opolis!");
    }

    /**
     * Output a message. format and args are printf-like parameters. A message
     * takes a single line on the output
     * @param format the format of the message
     * @param args the arguments for the format
     */
    public void message(String format, Object... args) {
        output.println(String.format(format, args));
    }

    /* This Map gives the Strings that are used for each kind of cell. */
    private static final Map<CellType, String> CELL_TYPE_STRING_MAP;

    /* This code initializes the CELL_TYPE_STRING_MAP object. */
    static {
        CELL_TYPE_STRING_MAP = new HashMap<>();
        CELL_TYPE_STRING_MAP.put(CellType.DIRT, "..");
        CELL_TYPE_STRING_MAP.put(CellType.RIVER, "~~");
        CELL_TYPE_STRING_MAP.put(CellType.WOODS, "TT");
        CELL_TYPE_STRING_MAP.put(CellType.RESIDENTIAL, "R?");
        CELL_TYPE_STRING_MAP.put(CellType.INDUSTRIAL, "I?");
        CELL_TYPE_STRING_MAP.put(CellType.ROAD, "++");
    }

    /** Display the grid for the game.
     */
    void displayGrid() {
        // Show the grid
        Grid grid = game.getGrid();
        int nRows = grid.getHeight();
        int nCols = grid.getWidth();

        // Each line of output is a separate message.
        StringBuilder temp = new StringBuilder();

        //Header line
        temp.append("  ");
        for (int col = 0; col < nCols; col++) {
            if (col > 0 && (col % 10) == 0 ) {
                temp.append(col % 100);
            } else {
                temp.append(String.format("%2d", col % 10));
            }
        }
        message(temp.toString());

        // Lines showing the grid
        for (int row = 0; row < nRows; row++) {
            temp.setLength(0);
            if (row > 0 && (row % 10) == 0 ) {
                temp.append(row % 100);
            } else {
                temp.append(String.format("%2d", row % 10));
            }
            for (int col = 0; col < nCols; col++) {
                Cell cell = grid.cellAt(col, row);
                CellType cellType = cell.getCellType();
                String cellString = CELL_TYPE_STRING_MAP.get(cellType);
                // For RESIDENTIAL zones, the population / 8 is added to the
                // output.
                if (cellType == CellType.RESIDENTIAL) {
                    // Add population to output
                    Residential resCell = (Residential)cell;
                    GridRectangle rect = resCell.getRectangle();
                    if (col == rect.x + rect.w - 1 && row == rect.y + rect.h - 1) {
                        cellString = String.format("%2d", resCell.getPopulation());
                    } else {
                        int resPop = (resCell.getPopulation() + 7) / 8;
                        char popChar;
                        if (resPop == 0)
                            popChar = ' ';
                        else if (resPop <= 9)
                            popChar = Integer.toString(resPop).charAt(0);
                        else
                            popChar = '+';
                        cellString = cellString.replace('?', popChar);
                    }
                 }
                 if (cellType == CellType.INDUSTRIAL) {
                    //Add industries count to output
                    Industrial indCell = (Industrial) cell;
                    GridRectangle rect = indCell.getRectangle();
                    int indCount = indCell.getPopulation();
                    char countChar = Integer.toString(indCount).charAt(0);
                    if (indCount != 0) {
                        cellString = cellString.replace('?', countChar);
                    } else {
                        cellString = cellString.replace('?', ' ');
                    }
                 }
                 if (cellType == CellType.ROAD){
                    Road roadCell = (Road) cell;
                    GridLocation loc = roadCell.getLocation();
                    
                 }

                temp.append(cellString);
            }
            message(temp.toString());
        }

        // Bottom line
        message("");
    }

//    /* Print a command. For testing purposes. */
//    private void printCommand(String command, String[] words) {
//        message(String.format("Command %s: %s%n",
//                command, Arrays.toString(words)));
//    }
}
