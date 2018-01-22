
package org.battlecode.bc18.util.pathfinder;

import static org.battlecode.bc18.util.Utils.gc;

import bc.Direction;
import bc.MapLocation;
import bc.Planet;
import bc.PlanetMap;
import org.battlecode.bc18.util.Utils;

import java.util.*;

/**
 * AStarSearch.java
 * A* search algorithm implementation.
 * 
 * [Manual]
 * Create the search module. You need to specify the planet.
 * The created module will have a grid of dimension (planet.x, planet.y)
 * You may optionally add obstacles into the map by calling addObstacles(),
 * or a specified weight to a specific map location by calling addLocationWeight().
 * 
 * 
 *
 * @author Jared Junyoung Lim
 */
public class AStarSearch {
    // Upper bound for x of the map
    private int xLimit;
    // Upper bound for y of the map
    private int yLimit;
    // current planet
    private Planet planet;
    // current planet map
    private PlanetMap planetMap;

    // The set of nodes already evaluated
    private Set<Node> closedSet = new HashSet<>();

    // The set of currently discovered nodes that are not evaluated yet.
    // Initially, only the start node is known.
    private PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(this::getFScore));

    // For each node, which node it can most efficiently be reached from.
    // If a node can be reached from many nodes, cameFrom will eventually contain the
    // most efficient previous step.
    private Map<Node, Node> cameFrom = new HashMap<>();

    // For each node, the cost of getting from the start node to that node.
    // If !contains(), INFINITY
    private Map<Node, Integer> gScore = new HashMap<>();;

    // For each node, the total cost of getting from the start node to the goal
    // by passing by that node. That value is partly known, partly heuristic.
    private Map<Node, Integer> fScore = new HashMap<>();;

    // For each node, the cost of getting from its neighbor to itself
    // By default, it's one. Different weights can be specified.
    private Map<Node, Integer> weights = new HashMap<>();;

    /**
     * A* search module.
     * @param planet current planet
     */
    public AStarSearch(Planet planet) {
        updatePlanet(planet);
    }

    public void refreshPlanetMap() {
        updatePlanet(gc.planet());
    }

    public void updatePlanet(Planet planet) {
        this.planet = planet;
        this.planetMap = gc.startingMap(planet);
        this.xLimit = (int) planetMap.getWidth();
        this.yLimit = (int) planetMap.getHeight();
        resetAll();
    }

    public void resetAll() {
        closedSet.clear();
        openSet.clear();
        cameFrom.clear();
        gScore.clear();
        fScore.clear();
        weights.clear();
    }

    /**
     * Add an obstacle to the search map.
     * @param loc map location of an obstacle
     */
    public void addObstacles(MapLocation loc) {
        closedSet.add(new Node(loc.getY(), loc.getX()));
    }

    public void addObstacles(Node node) {
        closedSet.add(node);
    }

    /**
     * Add weight to a location
     * @param loc a map location
     * @param weight added weight
     */
    public void addLocationWeight(MapLocation loc, int weight) {
        weights.put(new Node(loc.getY(), loc.getX()), weight);
    }

    public void addLocationWeight(Node node, int weight) {
        weights.put(node, weight);
    }

    private int heuristicCostEstimate(Node fromNode, Node endNode) {
        // double a = Math.pow(fromNode.r - endNode.r, 2);
        // double b = Math.pow(fromNode.c - endNode.c, 2);
        // return (int) Math.round(Math.sqrt(a + b));
        return Math.max(fromNode.r - endNode.r, fromNode.c - endNode.c);
    }

    private int getWeightOf(Node node) {
        return weights.getOrDefault(node, 1);
    }

    public Direction getNextDirectionFrom(MapLocation startLoc, MapLocation endLoc, boolean ignoreUnits) {
        List<Node> path = getShortestPath(new Node(startLoc), new Node(endLoc), ignoreUnits);
        if (path.size() < 2)
            return Direction.Center;
        
        Node cur = path.get(0);
        Node next = path.get(1);
        return Utils.dirGrid[next.r - cur.r + 1][next.c - cur.c + 1];
    }

    private List<Node> reconstructPath(Node current) {
        List<Node> totalPath = new ArrayList<>();
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
     * @param startNode starting node
     * @param goalNode destination node
     * @param ignoreUnits true if we want to ignore units while finding a path,
     *                    false if we want to treat them as obstacles
     */
    public List<Node> getShortestPath(Node startNode, Node goalNode, boolean ignoreUnits) {

        openSet.add(startNode);
        gScore.put(startNode, 0);
        fScore.put(startNode, heuristicCostEstimate(startNode, goalNode));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.equals(goalNode)) {
                return reconstructPath(current);
            }

            openSet.remove(current);
            closedSet.add(current);

            for (Node neighbor : getNeighborsOf(current)) {

                if (!Utils.toBool(planetMap.isPassableTerrainAt(neighbor.toMapLocation(planet))))
                    continue;

                if (!ignoreUnits) {
                    if (gc.senseUnitAtLocation(neighbor.toMapLocation(planet)) != null)
                        continue;
                }

                if (closedSet.contains(neighbor))
                    continue;

                if (!openSet.contains(neighbor))
                    openSet.add(neighbor);

                int newG = gScore.get(current) + getWeightOf(neighbor);
                if (newG >= getGScore(neighbor))
                    continue;

                cameFrom.put(neighbor, current);

                gScore.put(neighbor, newG);

                int newF = gScore.get(neighbor) + heuristicCostEstimate(neighbor, goalNode);
                fScore.put(neighbor, newF);
            }
        }

        return null;
    }

    private int getFScore(Node node) {
        return fScore.getOrDefault(node, Integer.MAX_VALUE);
    }

    private int getGScore(Node node) {
        return gScore.getOrDefault(node, Integer.MAX_VALUE);
    }

    /**
     * Gets a list of all adjacent nodes.
     * Includes the four cardinal directions and the four diagonals.
     * @param current specified node
     * @return a list of neighbor nodes
     */
    private List<Node> getNeighborsOf(Node current) {
        int r = current.r;
        int c= current.c;
        List<Node> neighbors = new ArrayList<>();

        if (c > 0) neighbors.add(new Node(r, c-1));
        if (c < xLimit-1) neighbors.add(new Node(r, c+1));

        if (r > 0) {
            neighbors.add(new Node(r-1, c));
            if (c > 0) neighbors.add(new Node(r-1, c-1));
            if (c < xLimit-1) neighbors.add(new Node(r-1, c+1));
        }

        if (r < yLimit-1) {
            neighbors.add(new Node(r+1, c));
            if (c > 0) neighbors.add(new Node(r+1, c-1));
            if (c < xLimit-1) neighbors.add(new Node(r+1, c+1));
        }

        return neighbors;
    }
}