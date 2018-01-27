
package org.battlecode.bc18.util.pathfinder;

import bc.MapLocation;

import java.util.*;

/**
 * AStarSearch.java
 * A* update algorithm implementation.
 *
 * @author Jared Junyoung Lim
 */
public class AStarSearch {
    // Planet.getWidth()
    private int xLimit;
    // Planet.getHeight()
    private int yLimit;

    private Cell goalCell;

    // The set of nodes already evaluated
    private Set<Cell> closedSet = new HashSet<>();

    // The set of currently discovered nodes that are not evaluated yet.
    // Initially, only the start node is known.
    private PriorityQueue<Cell> openSet = new PriorityQueue<>(Comparator.comparingInt(this::getFScore));

    // For each node, which node it can most efficiently be reached from.
    // If a node can be reached from many nodes, cameFrom will eventually contain the
    // most efficient previous step.
    private Map<Cell, Cell> cameFrom = new HashMap<>();

    // For each node, the cost of getting from the start node to that node.
    // If !contains(), INFINITY
    private Map<Cell, Integer> gScore = new HashMap<>();

    // For each node, the total cost of getting from the start node to the goal
    // by passing by that node. That value is partly known, partly heuristic.
    private Map<Cell, Integer> fScore = new HashMap<>();
    
    // For each node, the cost of getting from its neighbor to itself
    // By default, it's one. Different weights can be specified.
    private Map<Cell, Integer> weights = new HashMap<>();

    /**
     * A* update module.
     * @param xLimit the upper limit for x axis.
     * @param yLimit the upper limit for y axis.
     * @param startCell starting point Cell.
     * @param goalCell end goal Cell.
     */
    public AStarSearch(int xLimit, int yLimit, Cell startCell, Cell goalCell) {
        this.xLimit = xLimit;
        this.yLimit = yLimit;

        this.goalCell = goalCell;
        openSet.add(startCell);

        // The cost of going from start to start is zero.
        gScore.put(startCell, 0);

        // For the first node, that value is completely heuristic.
        fScore.put(startCell, heuristicCostEstimate(startCell, goalCell));
    }

    /**
     * Add an obstacle to the update map.
     * @param loc map location of an obstacle
     */
    public void addObstacles(MapLocation loc) {
        closedSet.add(new Cell(loc.getY(), loc.getX()));
    }
    
    public void addObstacles(Cell cell) {
        closedSet.add(cell);
    }

    /**
     * Add weight to a location
     * @param loc a map location
     * @param weight added weight
     */
    public void addLocationWeight(MapLocation loc, int weight) {
        weights.put(new Cell(loc.getY(), loc.getX()), weight);
    }
    
    public void addLocationWeight(Cell cell, int weight) {
        weights.put(cell, weight);
    }
    
    private int heuristicCostEstimate(Cell fromCell, Cell endCell) {
        // double a = Math.pow(fromCell.r - endCell.r, 2);
        // double b = Math.pow(fromCell.c - endCell.c, 2);
        // return (int) Math.round(Math.sqrt(a + b));
        return Math.max(fromCell.r - endCell.r, fromCell.c - endCell.c);
    }
    
    private int getWeightOf(Cell cell) {
        return weights.getOrDefault(cell, 1);
    }

    private List<Cell> reconstructPath(Cell current) {
        List<Cell> totalPath = new ArrayList<>();
        totalPath.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(0, current);
        }
        return totalPath;
    }

    /**
     * Returns the most efficient path from the starting node to the goal node
     * The list starts with the starting node and ends with the goal node.
     * @return the list of nodes
     */
    public List<Cell> getShortestPath() {
        while (!openSet.isEmpty()) {
            Cell current = openSet.poll();
            if (current.equals(goalCell)) {
                return reconstructPath(current);
            }

            openSet.remove(current);
            closedSet.add(current);

            for (Cell neighbor : getNeighborsOf(current)) {

                if (closedSet.contains(neighbor))
                    continue;

                if (!openSet.contains(neighbor))
                    openSet.add(neighbor);

                int newG = gScore.get(current) + getWeightOf(neighbor);
                if (newG >= getGScore(neighbor))
                    continue;

                cameFrom.put(neighbor, current);

                gScore.put(neighbor, newG);

                int newF = gScore.get(neighbor) + heuristicCostEstimate(neighbor, goalCell);
                fScore.put(neighbor, newF);
            }
        }

        return null;
    }

    private int getFScore(Cell cell) {
        return fScore.getOrDefault(cell, Integer.MAX_VALUE);
    }

    private int getGScore(Cell cell) {
        return gScore.getOrDefault(cell, Integer.MAX_VALUE);
    }

    /**
     * Gets a list of all adjacent nodes.
     * Includes the four cardinal directions and the four diagonals.
     * @param current specified node
     * @return a list of neighbor nodes
     */
    private List<Cell> getNeighborsOf(Cell current) {
        int r = current.r;
        int c= current.c;
        List<Cell> neighbors = new ArrayList<>();

        if (c > 0) neighbors.add(new Cell(r, c-1));
        if (c < xLimit-1) neighbors.add(new Cell(r, c+1));

        if (r > 0) {
            neighbors.add(new Cell(r-1, c));
            if (c > 0) neighbors.add(new Cell(r-1, c-1));
            if (c < xLimit-1) neighbors.add(new Cell(r-1, c+1));
        }

        if (r < yLimit-1) {
            neighbors.add(new Cell(r+1, c));
            if (c > 0) neighbors.add(new Cell(r+1, c-1));
            if (c < xLimit-1) neighbors.add(new Cell(r+1, c+1));
        }

        return neighbors;
    }
}