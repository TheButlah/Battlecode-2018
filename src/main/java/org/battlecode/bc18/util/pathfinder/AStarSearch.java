
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

    private Node goalNode;

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
    private Map<Node, Integer> gScore = new HashMap<>();

    // For each node, the total cost of getting from the start node to the goal
    // by passing by that node. That value is partly known, partly heuristic.
    private Map<Node, Integer> fScore = new HashMap<>();
    
    // For each node, the cost of getting from its neighbor to itself
    // By default, it's one. Different weights can be specified.
    private Map<Node, Integer> weights = new HashMap<>();

    /**
     * A* update module.
     * @param xLimit the upper limit for x axis.
     * @param yLimit the upper limit for y axis.
     * @param startNode starting point Node.
     * @param goalNode end goal Node.
     */
    public AStarSearch(int xLimit, int yLimit, Node startNode, Node goalNode) {
        this.xLimit = xLimit;
        this.yLimit = yLimit;

        this.goalNode = goalNode;
        openSet.add(startNode);

        // The cost of going from start to start is zero.
        gScore.put(startNode, 0);

        // For the first node, that value is completely heuristic.
        fScore.put(startNode, heuristicCostEstimate(startNode, goalNode));
    }

    /**
     * Add an obstacle to the update map.
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
     */
    public List<Node> getShortestPath() {
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.equals(goalNode)) {
                return reconstructPath(current);
            }

            openSet.remove(current);
            closedSet.add(current);

            for (Node neighbor : getNeighborsOf(current)) {

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