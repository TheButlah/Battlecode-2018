package org.battlecode.bc18;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.battlecode.bc18.bots.noobbot.Bot;

import bc.Direction;
import bc.GameController;
import bc.MapLocation;
import bc.PlanetMap;

public final class Utils {
    private Utils() {} //Prevent instantiation

    public static final long SEED = 8675309;
    public static final Random rand = new Random(SEED);

    public static final Direction[] dirs = Direction.values();
    public static int earthWidth;
    public static int earthHeight;

    public static GameController gc;
    /** Mapping from id to Bot objects */
    public static final HashMap<Integer, Bot> bots = new HashMap<>();

    public static boolean toBool(short x) {
        return x > 0;
    }
    
    public static boolean toBool(int x) {
        return x > 0;
    }

    public static boolean toBool(byte x) {
        return x > 0;
    }

    public static boolean toBool(long x) {
        return x > 0;
    }

    public static void setEarthDims(PlanetMap earthMap) {
        earthWidth = (int)earthMap.getWidth();
        earthHeight = (int)earthMap.getHeight();
    }

    public static MapLocation closest(ArrayList<MapLocation> locations, MapLocation here) {
        MapLocation closest = null;
        long closestDist = Integer.MAX_VALUE;
        for (MapLocation loc : locations) {
            long dist = loc.distanceSquaredTo(here);
            if (dist < closestDist) {
                closestDist = dist;
                closest = loc;
            }
        }
        return closest;
    }

}
