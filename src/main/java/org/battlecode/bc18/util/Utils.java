package org.battlecode.bc18.util;

import bc.*;

import java.util.List;
import java.util.Random;

public final class Utils {
    //private Utils() {} //Prevent instantiation

    public static final GameController gc;

    public static final long SEED = 8675309;
    public static final Random rand = new Random(SEED);

    public static final Direction[] dirs = Direction.values();
    public static final PlanetMap EARTH_START, MARS_START;
    public static final int MAP_WIDTH, MAP_HEIGHT;
    public static final Planet PLANET;

    public static final Team TEAM;

    static {
        gc = new GameController();
        PLANET = gc.planet();
        EARTH_START = gc.startingMap(Planet.Earth);
        MARS_START = gc.startingMap(Planet.Mars);
        MAP_WIDTH = (int) EARTH_START.getWidth();
        MAP_HEIGHT = (int) EARTH_START.getHeight();
        TEAM = gc.team();
    }

    public static boolean toBool(short x) {
        return x != 0;
    }
    
    public static boolean toBool(int x) {
        return x != 0;
    }

    public static boolean toBool(byte x) {
        return x != 0;
    }

    public static boolean toBool(long x) {
        return x != 0;
    }

    public static MapLocation closest(List<MapLocation> locations, MapLocation here) {
        MapLocation closest = null;
        int closestDist = Integer.MAX_VALUE;
        for (MapLocation loc : locations) {
            int dist = (int) loc.distanceSquaredTo(here);
            if (dist < closestDist) {
                closestDist = dist;
                closest = loc;
            }
        }
        return closest;
    }

    public static Pair<MapLocation, Integer> closestPair(List<Pair<MapLocation, Integer>> locations, MapLocation here) {
        Pair<MapLocation, Integer> closest = null;
        int closestDist = Integer.MAX_VALUE;
        for (Pair<MapLocation, Integer> pair : locations) {
            MapLocation loc = pair.getFirst();
            int dist = (int) loc.distanceSquaredTo(here);
            if (dist < closestDist) {
                closestDist = dist;
                closest = pair;
            }
        }
        return closest;
    }

    public static boolean isAnyAdjacent(List<MapLocation> locations, MapLocation target) {
        for (MapLocation loc : locations) {
            if (loc.isAdjacentTo(target)) {
                return true;
            }
        }
        return false;
    }

}
