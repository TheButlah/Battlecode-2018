package org.battlecode.bc18.pathfinder;

import bc.Direction;
import bc.MapLocation;
import bc.UnitType;
import bc.VecMapLocation;
import org.battlecode.bc18.api.MyFactory;

import org.battlecode.bc18.util.ListNode;
import org.battlecode.bc18.util.Pair;
import org.battlecode.bc18.util.Utils;

import java.util.*;

import static org.battlecode.bc18.pathfinder.PathFinder.pf;

public class ChokeManager {

    public static final double MAX_PCT_TO_ENEMY = 0.4;
    public static final double MIN_PCT_TO_ENEMY = 0.15;
    public static final int DANGEROUS_RADIUS = 70; //vision range of ranger

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
    public Cell getChokeInPath(Pair<ListNode, ListNode> path) {
        return null; //TODO: Do this Jared
    }

    /** Follows a path and marks the dangerous spots */
    public void markDangerousPath(ListNode start) {
        ListNode current = start;
        while (current != null) {
            MapLocation currentLoc = current.cell.getLoc();
            VecMapLocation locs = Utils.gc.allLocationsWithin(currentLoc, DANGEROUS_RADIUS);
            for (int i=0; i<locs.size(); i++) {
                MapLocation loc = locs.get(i);
                setDangerous(true, loc);
            }
            current = current.getNext();
        }
    }



    public MapLocation getChokepoint(MyFactory factory) {
        MapLocation facLoc = factory.getMapLocation();
        Pair<ListNode, ListNode> path = getPath(facLoc);
        Cell choke = getChokeInPath(path);
        return choke.getLoc();
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

    /** Marks the given location as dangerous */
    private void setDangerous(boolean isDangerous, MapLocation loc) {
        setDangerous(isDangerous, loc.getY(), loc.getX());
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

    /** Gets the pair of start and end nodes on a path from `from` to PathFinder.getTarget() */
    public Pair<ListNode, ListNode> getPath(MapLocation from) {
        HashSet<Cell> visited = new HashSet<>();
        Cell startCell = new Cell(from);
        visited.add(startCell);
        ListNode start = new ListNode(startCell, null, null);
        ListNode end = getPath(start, visited);
        return new Pair<>(start, end);
    }

    public ListNode getPath(ListNode from, Set<Cell> visited) {
        MapLocation fromLoc = from.cell.getLoc();
        Cell optimalCell = null;
        int optimalDist = PathFinder.UNREACHABLE;
        for (Direction dir : Utils.dirs) {
            if (dir == Direction.Center) continue;
            MapLocation dirLoc = fromLoc.add(dir);
            Cell dirCell = new Cell(dirLoc);
            if (!pf.onMap(dirLoc) || !pf.isPassable(dirLoc) || visited.contains(dirCell)) continue;
            int index = toIndex(dirCell);
            int theCost = pf.cost[index];
            if (theCost < optimalDist) {
                optimalDist = pf.cost[index];
                optimalCell = dirCell;
            }

        }
        if (optimalCell == null) return from;
        visited.add(optimalCell);
        ListNode newNode = new ListNode(optimalCell, from, null);
        return getPath(newNode, visited);
    }


    public List<ListNode> getPaths(MapLocation startLoc) {
        //TODO this is not done
        List<ListNode> paths = new ArrayList<ListNode>();
        HashSet<Cell> visited = new HashSet<>();
        Cell startCell = new Cell(startLoc);
        ListNode prevNode = new ListNode(startCell, null, null);
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
        for (int i=0; i<optimalCells.size(); i++) {
            Cell cell = optimalCells.get(i);
            ListNode nextNode = new ListNode(cell, prevNode, null);
        }
        return null;
    }

}
