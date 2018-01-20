
package org.battlecode.bc18.util.pathfinder;

import java.util.*;

/**
 * AStarSearch.java
 * A* search algorithm implementation.
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
    private List<Node> closedSet = new ArrayList<>();

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

    /**
     * A* search module.
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

    public void addObstacles(Node node) {
        closedSet.add(node);
    }

    private int heuristicCostEstimate(Node fromNode, Node endNode) {
        double a = Math.pow(fromNode.r - endNode.r, 2);
        double b = Math.pow(fromNode.c - endNode.c, 2);
        return (int) Math.round(Math.sqrt(a + b));
    }

    private List<Node> reconstructPath(Node current) {
        List<Node> totalPath = new ArrayList<>();
        totalPath.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }
        return totalPath;
    }

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

                int newG = gScore.get(current) + 1;
                if (newG >= getGScore(neighbor))
                    continue;

                if (cameFrom.replace(neighbor, current) == null) {
                    cameFrom.put(neighbor, current);
                }

                if (gScore.replace(neighbor, newG) == null) {
                    gScore.put(neighbor, newG);
                }

                int newF = gScore.get(neighbor) + heuristicCostEstimate(neighbor, goalNode);
                if (fScore.replace(neighbor, newF) == null) {
                    fScore.put(neighbor, newF);
                }
            }
        }

        return null;
    }

    private int getFScore(Node node) {
        return gScore.getOrDefault(node, Integer.MAX_VALUE);
    }

    private int getGScore(Node node) {
        return gScore.getOrDefault(node, Integer.MAX_VALUE);
    }

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