package org.battlecode.bc18.pathfinder;

import bc.Direction;
import bc.MapLocation;
import org.battlecode.bc18.api.MyFactory;

import org.battlecode.bc18.util.ListNode;
import org.battlecode.bc18.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.battlecode.bc18.pathfinder.PathFinder.pf;

public class ChokeManager {

    public static final double MAX_PCT_TO_ENEMY = 0.4;
    public static final double MIN_PCT_TO_ENEMY = 0.15;

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

    /** `path` is the node at the start of our path (node where we are standing) */
    public List<Cell> getChokesInPath(ListNode path) {

        int limit = pathLength(path) / 2;
        int current = Integer.MAX_VALUE;
        List<Cell> chokepoints = new ArrayList<>();
        boolean found = false;

        for (int count = 0; count < limit; count++) {
            int nextWidth = PathFinder.pf.cost[toIndex(path.cell)];
            if (nextWidth <= current) {
                current = nextWidth;
                if (count == limit - 1) {
                    chokepoints.add(path.cell);
                }
            }
            else {
                if (!found) {
                    chokepoints.add(path.cell);
                }
                found = !found;
            }

            path = path.next;
        }

        return chokepoints;
    }

    private int pathLength(ListNode path) {
        int count = 0;
        while (path != null) {
            path = path.next;
            count++;
        }
        return count;
    }

    public List<MapLocation> getChokepoints(MyFactory factory) {
        return null; //TODO: Do this
    }

    /*public List<MapLocation> computeChokesBetweenPoints(MapLocation enemy, MapLocation us) {
        pf.setTarget(enemy);
        System.out.println("Set the target");
        System.out.println("Target: " + pf.getTarget() + ", Us: " + us);
        PathFinder.Path path = pf.pathToTargetFrom(us);
        System.out.println("Computed Path");
        PathFinder.Path.Node start = path.getStart();
        System.out.println(start + ", " + us);
        System.out.println(path.describePath());
        System.out.println("\n\n\n\n\n\n\n\n");
        return null;
    }*/

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


    public List<ListNode> getPaths(MapLocation startLoc) {
        List<ListNode> paths = new ArrayList<ListNode>();
        HashSet<Cell> visited = new HashSet<>();
        Cell startCell = new Cell(startLoc);
        ListNode start = new ListNode(startCell, null, null);
        MapLocation fromLoc = startLoc;
        ArrayList<Cell> optimalCells = new ArrayList<>(8);
        int optimalDist = PathFinder.UNREACHABLE;
        for (Direction dir : Utils.dirs) {
            if (dir == Direction.Center) continue;
            MapLocation dirLoc = fromLoc.add(dir);
            Cell dirCell = new Cell(dirLoc);
            if (!pf.onMap(dirLoc) || !pf.isPassable(dirLoc) || visited.contains(dirCell)) continue;
            int index = toIndex(dirCell);
            int theCost = pf.cost[index];
            if (theCost == optimalDist) {
                optimalCells.add(dirCell);
            } else if (theCost < optimalDist) {
                optimalDist = pf.cost[index];
                optimalCells.clear();
                optimalCells.add(dirCell);
            }

        }
        if (optimalCells.size() == 0) return null; //TODO fix this
        /*for (Cell cell : optimalCells) {
            paths.
        }*/
        return null;
    }

}
