/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

import cs345.model.cell.CellType;
import static cs345.model.cell.CellType.*;
import cs345.model.cell.Dirt;
import cs345.model.cell.River;
import cs345.model.cell.Woods;

import java.util.Properties;
import java.util.Random;

/**
 * Generator for new Maps. The method newMap(Grid) is called to initialize the
 * grid with a new Map.
 *
 * This generator is a modified version of the map generator program from
 * micropolis, java version. (See http://github.com/jason17055/micropolis-java.)
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class MapGenerator {

    public static final String CREATE_ISLAND_PROP = "cs345opolis.mapgenerator.createIsland";
    public static final String TREE_LEVEL_PROP = "cs345opolis.mapgenerator.treeLevel";
    public static final String CURVE_LEVEL_PROP = "cs345opolis.mapgenerator.curveLevel";
    public static final String LAKE_LEVEL_PROP = "cs345opolis.mapgenerator.lakeLevel";
    public static final String RANDOM_SEED_PROP = "cs345opolis.mapgenerator.randomSeed";

    /**
     * Setting that determines whether to generate a new map as an island.
     */
    public enum CreateIsland
    {
        NEVER,
        ALWAYS,
        SELDOM    // seldom == 10% of the time
    }

    /**
     * Sets the frequency of island creation.
     */
    private CreateIsland createIsland;

    /**
     * Levels for map feature creation. Level meaning are:
     *   positive -- roughly the number of features to randomly place
     *   negative -- the number of features is randomly chosen.
     *   zero -- no occurrences of the feature are generated
     */
    private int treeLevel = -1; //level for tree creation
    private int curveLevel = -1; //level for river curviness
    private int lakeLevel = -1; //level for lake creation

    /* Remember the original seed for the random number generator. */
    private String prngSeed = null;

    private Random prng = null;

    /**
     * Create a new MapGenerator with default values.
     */
    public MapGenerator(Properties props) {
        getProperties(props);
        if (prngSeed != null) {
            prng = new Random(Long.parseLong(prngSeed));
        } else {
            prng = new Random();
        }
    }

    private void getProperties(Properties props) {
        this.createIsland = CreateIsland.valueOf(
                props.getProperty(CREATE_ISLAND_PROP, "SELDOM"));
        this.treeLevel = Integer.parseInt(
                props.getProperty(TREE_LEVEL_PROP, "-1"));
        this.curveLevel = Integer.parseInt(
                props.getProperty(CURVE_LEVEL_PROP, "-1"));
        this.lakeLevel = Integer.parseInt(
                props.getProperty(LAKE_LEVEL_PROP, "-1"));
        prngSeed = props.getProperty(RANDOM_SEED_PROP);
    }

    /**
     * Check if relevant properties have changed. Return true if all the
     * properties are the same, false otherwise.
     * @param props the properties to be checked
     * @return true if properties have not changed
     */
    boolean sameProperties(Properties props) {
        String prngSeedProp = props.getProperty(RANDOM_SEED_PROP);
        return createIsland.name().equals(props.getProperty(CREATE_ISLAND_PROP, "SELDOM")) &&
                treeLevel == Integer.parseInt(props.getProperty(TREE_LEVEL_PROP, "-1")) &&
                curveLevel == Integer.parseInt(props.getProperty(CURVE_LEVEL_PROP, "-1")) &&
                lakeLevel == Integer.parseInt(props.getProperty(LAKE_LEVEL_PROP, "-1")) &&
                ((prngSeed != null && prngSeed.equals(prngSeedProp)) ||
                 (prngSeed == null && prngSeedProp == null));
    }

    /**
     * Return current value of createIsland.
     * @return current setting for createIsland
     */
    public CreateIsland getCreateIsland() {
        return createIsland;
    }

    /**
     * Set current value of createIsland.
     * @param createIsland the value to be set
     */
    public void setCreateIsland(CreateIsland createIsland) {
        this.createIsland = createIsland;
    }

    /**
     * Get the current tree level. Negative levels are random, zero level is
     * none, and positive levels are the number of features to generate.
     * @return the current tree level
     */
    public int getTreeLevel() {
        return treeLevel;
    }

    /**
     * Set the tree level.
     * @param treeLevel thenew value for the lake level.
     */
    public void setTreeLevel(int treeLevel) {
        this.treeLevel = treeLevel;
    }

    /**
     * Get the current river curve level. Negative levels are random, zero level
     * is none, and positive levels are the number of features to generate.
     * @return the current curve level
     */
    public int getCurveLevel() {
        return curveLevel;
    }

    /**
     * Set the curve level.
     * @param curveLevel thenew value for the lake level.
     */
    public void setCurveLevel(int curveLevel) {
        this.curveLevel = curveLevel;
    }

    /**
     * Get the current lake level. Negative levels are random, zero level is
     * none, and positive levels are the number of features to generate.
     * @return the current lake level
     */
    public int getLakeLevel() {
        return lakeLevel;
    }

    /**
     * Set the lake level.
     * @param lakeLevel thenew value for the lake level.
     */
    public void setLakeLevel(int lakeLevel) {
        this.lakeLevel = lakeLevel;
    }

    /**
     * Set the value of the seed for the random number generator.
     * @param seed the value to set the seed of the random number generator
     */
    public void setRandomSeed(long seed) {
        prng.setSeed(seed);
    }

    /* The model for this map. */
    private Cs345Opolis model;

    /* Working storage for grid locations. */
    private int xStart;
    private int yStart;
    private int mapX;
    private int mapY;
    private int dir;
    private int lastDir;

    /**
     * Generate a map for the given Grid. Using the configuration parameters.
     * @param model the Cs345Opolis model for this map
     */
    public void generateMap(Cs345Opolis model) {
        this.model = model;
        Grid grid = model.getGrid();
        switch (createIsland) {
            case SELDOM:
                if (prng.nextInt(100) < 10) { //10% chance of generating an island
                    makeIsland(grid);
                    return;
                }
            case NEVER:
                grid.fill(Dirt::new);
                break;
            case ALWAYS:
                makeNakedIsland(grid);
                break;
        }

        getRandStart(grid);

        if (curveLevel != 0) {
            doRivers(grid);
        }

        if (lakeLevel != 0) {
            makeLakes(grid);
        }

        smoothRiver(grid);

        if (treeLevel != 0) {
            doTrees(grid);
        }
    }

    private void makeIsland(Grid grid) {
        makeNakedIsland(grid);
        smoothRiver(grid);
        doTrees(grid);
    }

    private int erand(int limit) {
        return Math.min(prng.nextInt(limit), prng.nextInt(limit));
    }

    private void makeNakedIsland(Grid grid) {
        final int ISLAND_RADIUS = 18;
        final int WORLD_X = grid.getWidth();
        final int WORLD_Y = grid.getHeight();

        grid.fill(River::new);

        for (int y = 5; y < WORLD_Y - 5; y++) {
            for (int x = 5; x < WORLD_X - 5; x++) {
                grid.setCellAt(x, y, new Dirt(model, new GridLocation(x, y)));
            }
        }

        for (int x = 0; x < WORLD_X - 5; x += 2)
        {
            mapX = x;
            mapY = erand(ISLAND_RADIUS+1);
            BRivPlop(grid);
            mapY = (WORLD_Y - 10) - erand(ISLAND_RADIUS+1);
            BRivPlop(grid);
            mapY = 0;
            SRivPlop(grid);
            mapY = WORLD_Y - 6;
            SRivPlop(grid);
        }

        for (int y = 0; y < WORLD_Y - 5; y += 2)
        {
            mapY = y;
            mapX = erand(ISLAND_RADIUS+1);
            BRivPlop(grid);
            mapX = (WORLD_X - 10) - erand(ISLAND_RADIUS+1);
            BRivPlop(grid);
            mapX = 0;
            SRivPlop(grid);
            mapX = (WORLD_X - 6);
            SRivPlop(grid);
        }
    }

    private void getRandStart(Grid grid) {
        int width3 = grid.getWidth() / 3;
        int height3 = grid.getHeight() / 3;
        xStart = width3 + prng.nextInt(grid.getWidth() - 2 * width3);
        yStart = height3 + prng.nextInt(grid.getHeight() - 2 * height3);

        mapX = xStart;
        mapY = yStart;
    }

    private void makeLakes(Grid grid) {
        int lim1;
        if (lakeLevel < 0) {
            // max lakes is 11 for standard 120 x 100 grid
            int maxLakes = (11 * grid.getWidth() * grid.getHeight()) / 12000;
            lim1 = prng.nextInt(maxLakes);
        } else
            lim1 = lakeLevel / 2;

        for (int t = 0; t < lim1; t++)
        {
            int x = prng.nextInt(grid.getWidth() - 20) + 10;
            int y = prng.nextInt(grid.getHeight() - 19) + 10;
            int lim2 = prng.nextInt(13) + 2;

            for (int z = 0; z < lim2; z++)
            {
                mapX = x - 6 + prng.nextInt(13);
                mapY = y - 6 + prng.nextInt(13);

                if (prng.nextInt(5) != 0)
                    SRivPlop(grid);
                else
                    BRivPlop(grid);
            }
        }
    }

    private void doRivers(Grid grid) {
        dir = lastDir = prng.nextInt(4);
        doBRiv(grid);

        mapX = xStart;
        mapY = yStart;
        dir = lastDir = lastDir ^ 4;
        doBRiv(grid);

        mapX = xStart;
        mapY = yStart;
        lastDir = prng.nextInt(4);
        doSRiv(grid);
    }

    private void doBRiv(Grid grid) {
        int r1, r2;
        if (curveLevel < 0) {
            r1 = 100;
            r2 = 200;
        } else {
            r1 = curveLevel + 10;
            r2 = curveLevel + 100;
        }

        while (grid.validCoords(mapX + 4, mapY + 4)) {
            BRivPlop(grid);
            if (prng.nextInt(r1+1) < 10) {
                dir = lastDir;
            } else {
                if (prng.nextInt(r2+1) > 90) {
                    dir++;
                }
                if (prng.nextInt(r2+1) > 90) {
                    dir--;
                }
            }
            moveMap(dir);
        }
    }

    private void doSRiv(Grid grid) {
        int r1, r2;
        if (curveLevel < 0) {
            r1 = 100;
            r2 = 200;
        } else {
            r1 = curveLevel + 10;
            r2 = curveLevel + 100;
        }

        while (grid.validCoords(mapX + 3, mapY + 3)) {
            SRivPlop(grid);
            if (prng.nextInt(r1+1) < 10) {
                dir = lastDir;
            } else {
                if (prng.nextInt(r2+1) > 90) {
                    dir++;
                }
                if (prng.nextInt(r2+1) > 90) {
                    dir--;
                }
            }
            moveMap(dir);
        }
    }

    private static final char [][] BRMatrix = new char[][] {
            { 0, 0, 0, 3, 3, 3, 0, 0, 0 },
            { 0, 0, 3, 2, 2, 2, 3, 0, 0 },
            { 0, 3, 2, 2, 2, 2, 2, 3, 0 },
            { 3, 2, 2, 2, 2, 2, 2, 2, 3 },
            { 3, 2, 2, 2, 4, 2, 2, 2, 3 },
            { 3, 2, 2, 2, 2, 2, 2, 2, 3 },
            { 0, 3, 2, 2, 2, 2, 2, 3, 0 },
            { 0, 0, 3, 2, 2, 2, 3, 0, 0 },
            { 0, 0, 0, 3, 3, 3, 0, 0, 0 }
    };

    private void BRivPlop(Grid grid) {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                putOnMap(grid, BRMatrix[y][x], x, y);
            }
        }
    }

    private static final char [][] SRMatrix = new char[][] {
            { 0, 0, 3, 3, 0, 0 },
            { 0, 3, 2, 2, 3, 0 },
            { 3, 2, 2, 2, 2, 3 },
            { 3, 2, 2, 2, 2, 3 },
            { 0, 3, 2, 2, 3, 0 },
            { 0, 0, 3, 3, 0, 0 }
    };

    private void SRivPlop(Grid grid)
    {
        for (int x = 0; x < 6; x++)
        {
            for (int y = 0; y < 6; y++)
            {
                putOnMap(grid, SRMatrix[y][x], x, y);
            }
        }
    }

    private void putOnMap(Grid grid, char mapChar, int xoff, int yoff) {
        if (mapChar == 0)
            return;

        int xloc = mapX + xoff;
        int yloc = mapY + yoff;

        if (!grid.validCoords(xloc, yloc))
            return;

        grid.setCellAt(xloc, yloc, new River(model, new GridLocation(xloc, yloc)));
    }

//    Ignore this for now
//    static final char [] REdTab = new char[] {
//            RIVEDGE + 8, RIVEDGE + 8, RIVEDGE + 12, RIVEDGE + 10,
//            RIVEDGE + 0, RIVER,       RIVEDGE + 14, RIVEDGE + 12,
//            RIVEDGE + 4, RIVEDGE + 6, RIVER,        RIVEDGE + 8,
//            RIVEDGE + 2, RIVEDGE + 4, RIVEDGE + 0,  RIVER
//    };

    private void smoothRiver(Grid grid) {
//        The purpose of this routine is to jigger river edge stuff.
//        We'll worry about that in the future.
//
//        for (int mapY = 0; mapY < map.length; mapY++)
//        {
//            for (int mapX = 0; mapX < map[mapY].length; mapX++)
//            {
//                if (map[mapY][mapX] == REDGE)
//                {
//                    int bitindex = 0;
//
//                    for (int z = 0; z < 4; z++)
//                    {
//                        bitindex <<= 1;
//                        int xtem = mapX + DX[z];
//                        int ytem = mapY + DY[z];
//                        if (engine.testBounds(xtem, ytem) &&
//                                ((map[ytem][xtem] & LOMASK) != DIRT) &&
//                                (((map[ytem][xtem] & LOMASK) < WOODS_LOW) ||
//                                        ((map[ytem][xtem] & LOMASK) > WOODS_HIGH)))
//                        {
//                            bitindex |= 1;
//                        }
//                    }
//
//                    char temp = REdTab[bitindex & 15];
//                    if ((temp != RIVER) && prng.nextInt(2) != 0)
//                        temp++;
//                    map[mapY][mapX] = temp;
//                }
//            }
//        }
    }

    private void doTrees(Grid grid) {
        int amount;
        int gridSize = grid.getHeight() * grid.getWidth();

        if (treeLevel < 0) {
            amount = ((prng.nextInt(101) + 50) * gridSize) / 12000;
        }
        else {
            amount = treeLevel + 3;
        }

        for (int x = 0; x < amount; x++) {
            int xloc = prng.nextInt(grid.getWidth());
            int yloc = prng.nextInt(grid.getHeight());
            treeSplash(grid, xloc, yloc);
        }

        smoothTrees(grid);
        smoothTrees(grid);
    }

    private void treeSplash(Grid grid, int xloc, int yloc) {
        int dis;
        if (treeLevel < 0) {
            dis = prng.nextInt(151) + 50;
        }
        else {
            dis = prng.nextInt(101 + (treeLevel*2)) + 50;
        }

        mapX = xloc;
        mapY = yloc;

        for (int z = 0; z < dis; z++) {
            int dir = prng.nextInt(8);
            moveMap(dir);

            if (!grid.validCoords(mapX, mapY))
                return;

            if (grid.cellAt(mapX, mapY).getCellType() == CellType.DIRT) {
                grid.setCellAt(mapX, mapY, new Woods(model, new GridLocation(mapX, mapY)));
            }
        }
    }

    private static final int [] DIRECTION_TABX = new int[] {  0,  1,  1,  1,  0, -1, -1, -1 };
    private static final int [] DIRECTION_TABY = new int[] { -1, -1,  0,  1,  1,  1,  0, -1 };
    private void moveMap(int dir) {
        dir = dir & 7;
        mapX += DIRECTION_TABX[dir];
        mapY += DIRECTION_TABY[dir];
    }

    private static final int [] DX = new int[] { -1, 0, 1, 0 };
    private static final int [] DY = new int[] { 0, 1, 0, -1 };
    private static final CellType[] TEdTab = new CellType[] {
            DIRT,  DIRT,  DIRT,  WOODS,
            DIRT,  DIRT,  WOODS, WOODS,
            DIRT,  WOODS, DIRT,  WOODS,
            WOODS, WOODS, WOODS, WOODS };

    private void smoothTrees(Grid grid) {
        for (int mapY = 0; mapY < grid.getHeight(); mapY++) {
            for (int mapX = 0; mapX < grid.getWidth(); mapX++) {
                if (grid.cellAt(mapX, mapY).isTree()) {
                    int bitindex = 0;
                    for (int z = 0; z < 4; z++) {
                        bitindex <<= 1;
                        int xtem = mapX + DX[z];
                        int ytem = mapY + DY[z];
                        if (grid.validCoords(xtem, ytem) &&
                                grid.cellAt(xtem, ytem).isTree()) {
                            bitindex |= 1;
                        }
                    }
                    CellType newType = TEdTab[bitindex & 15];
                    if (newType != grid.cellAt(mapX, mapY).getCellType())
                        if (newType == DIRT) {
                            grid.setCellAt(mapX, mapY, new Dirt(model, new GridLocation(mapX, mapY)));
                        } else if (newType == WOODS) {
                            grid.setCellAt(mapX, mapY, new Woods(model, new GridLocation(mapX, mapY)));
                        } else {
                            throw new IllegalStateException("Bad CellType: " + newType);
                        }

                }
            }
        }
    }
}
