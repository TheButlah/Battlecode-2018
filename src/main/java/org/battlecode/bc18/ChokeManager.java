package org.battlecode.bc18;

import bc.MapLocation;
import bc.Planet;
import bc.PlanetMap;
import org.battlecode.bc18.api.MyFactory;
import org.battlecode.bc18.pathfinder.Cell;
import org.battlecode.bc18.pathfinder.PathFinder;
import org.battlecode.bc18.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChokeManager {

    public static final ChokeManager chman;
    static {
        chman = new ChokeManager();
    }

    private final int ROWS, COLS;
    private final boolean[] dangerous;
    private final HashMap<MyFactory, MapLocation> facToChoke = new HashMap<>(6);
    private final HashMap<MapLocation, MyFactory> chokeToFac = new HashMap<>(10);
    private final ArrayList<MapLocation> chokepoints = new ArrayList<>(10);

    public ChokeManager() {
        this.ROWS = Utils.MAP_HEIGHT;
        this.COLS = Utils.MAP_WIDTH;
        dangerous = new boolean[ROWS * COLS];
    }

    public List<Cell> getChokesInPath(PathFinder.Path path) {
        PathFinder.Path.Node start = path.getStart();
        return null; //TODO: Do this
    }

    public List<MapLocation> getChokepoints(MyFactory factory) {
        return null; //TODO: Do this
    }


    /** Whether the cell is dangerous (near the path the enemy will take) */
    public boolean isDangerous(Cell cell) {
        return dangerous[toIndex(cell)];
    }

    /** Whether the location is dangerous (near the path the enemy will take) */
    public boolean isDangerous(MapLocation loc) {
        return dangerous[toIndex(loc)];
    }

    public List<MapLocation> getChokepoints() {
        return chokepoints;
    }

    /** Marks the given cell as dangerous */
    private void setDangerous(boolean isDangerous, int r, int c) {
        dangerous[toIndex(r, c)] = isDangerous;
    }

    /** Marks the given cell as dangerous */
    private void setDangerous(boolean isDangerous, Cell cell) {
        setDangerous(isDangerous, cell.r, cell.c);
    }

    private int toIndex(int r, int c) {
        return c + r * COLS;
    }

    private int toIndex(Cell cell) {
        return cell.c + cell.r * COLS;
    }

    private int toIndex(MapLocation loc) {
        int r = loc.getY();
        int c = loc.getX();
        return toIndex(r,c);
    }

}
