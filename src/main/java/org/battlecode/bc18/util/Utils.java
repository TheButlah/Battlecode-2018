package org.battlecode.bc18.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.function.Predicate;

import org.battlecode.bc18.api.MyUnit;

import bc.Direction;
import bc.GameController;
import bc.MapLocation;
import bc.Planet;
import bc.PlanetMap;
import bc.Team;
import bc.Unit;
import bc.UnitType;
import bc.VecUnit;

public final class Utils {
    //private Utils() {} //Prevent instantiation

    public static final GameController gc;

    public static final long SEED = 8675309;
    public static final Random rand = new Random(SEED);

    public static final Direction[] dirs = Direction.values();
    public static final PlanetMap EARTH_START, MARS_START;
    public static final int MAP_WIDTH, MAP_HEIGHT;
    public static final Planet PLANET;
    public static final int[][] CONNECTED_COMPONENTS;
    public static final ArrayList<Integer> CONNECTED_COMPONENT_SIZES;
    public static final int IMPASSIBLE_TERRAIN = 9999;

    public static final Team TEAM;
    public static final Team OTHER_TEAM;

    static {
        gc = new GameController();
        PLANET = gc.planet();
        EARTH_START = gc.startingMap(Planet.Earth);
        MARS_START = gc.startingMap(Planet.Mars);
        MAP_WIDTH = (int) EARTH_START.getWidth();
        MAP_HEIGHT = (int) EARTH_START.getHeight();
        CONNECTED_COMPONENTS = new int[MAP_HEIGHT][MAP_WIDTH];
        CONNECTED_COMPONENT_SIZES = new ArrayList<>();
        CONNECTED_COMPONENT_SIZES.add(0); // Add dummy value so that array is 1-indexed so we can
                                          // perform size lookup for the nth connected component by
                                          // getting the nth element
        PlanetMap connectedComponentMap = PLANET == Planet.Earth ? EARTH_START : MARS_START;
        for (int r = 0; r < MAP_HEIGHT; ++r) {
            for (int c = 0; c < MAP_WIDTH; ++c) {
                MapLocation loc = new MapLocation(PLANET, c, r);
                if (!toBool(connectedComponentMap.isPassableTerrainAt(loc))) {
                    CONNECTED_COMPONENTS[r][c] = IMPASSIBLE_TERRAIN;
                }
            }
        }
        int count = 0;
        for (int r = 0; r < MAP_HEIGHT; ++r) {
            for (int c = 0; c < MAP_WIDTH; ++c) {
                if (CONNECTED_COMPONENTS[r][c] != 0) continue;
                CONNECTED_COMPONENTS[r][c] = ++count;
                int size = 1;
                Queue<Integer> q = new LinkedList<>();
                q.add((r << 16) | c);
                while (!q.isEmpty()) {
                    Integer coordinate = q.poll();
                    int row = coordinate >>> 16;
                    int col = coordinate & 0x0000FFFF;
                    int newRow = row;
                    int newCol = col - 1;
                    if (newCol >= 0) {
                        if (CONNECTED_COMPONENTS[newRow][newCol] == 0) {
                            CONNECTED_COMPONENTS[newRow][newCol] = count;
                            q.add((newRow << 16) | newCol);
                            ++size;
                        }
                    }
                    newCol = col + 1;
                    if (newCol < MAP_WIDTH) {
                        if (CONNECTED_COMPONENTS[newRow][newCol] == 0) {
                            CONNECTED_COMPONENTS[newRow][newCol] = count;
                            q.add((newRow << 16) | newCol);
                            ++size;
                        }
                    }
                    newRow = row - 1;
                    if (newRow >= 0) {
                        newCol = col;
                        if (CONNECTED_COMPONENTS[newRow][newCol] == 0) {
                            CONNECTED_COMPONENTS[newRow][newCol] = count;
                            q.add((newRow << 16) | newCol);
                            ++size;
                        }
                        newCol = col - 1;
                        if (newCol >= 0) {
                            if (CONNECTED_COMPONENTS[newRow][newCol] == 0) {
                                CONNECTED_COMPONENTS[newRow][newCol] = count;
                                q.add((newRow << 16) | newCol);
                                ++size;
                            }
                        }
                        newCol = col + 1;
                        if (newCol < MAP_WIDTH) {
                            if (CONNECTED_COMPONENTS[newRow][newCol] == 0) {
                                CONNECTED_COMPONENTS[newRow][newCol] = count;
                                q.add((newRow << 16) | newCol);
                                ++size;
                            }
                        }
                    }
                    newRow = row + 1;
                    if (newRow < MAP_HEIGHT) {
                        newCol = col;
                        if (CONNECTED_COMPONENTS[newRow][newCol] == 0) {
                            CONNECTED_COMPONENTS[newRow][newCol] = count;
                            q.add((newRow << 16) | newCol);
                            ++size;
                        }
                        newCol = col - 1;
                        if (newCol >= 0) {
                            if (CONNECTED_COMPONENTS[newRow][newCol] == 0) {
                                CONNECTED_COMPONENTS[newRow][newCol] = count;
                                q.add((newRow << 16) | newCol);
                                ++size;
                            }
                        }
                        newCol = col + 1;
                        if (newCol < MAP_WIDTH) {
                            if (CONNECTED_COMPONENTS[newRow][newCol] == 0) {
                                CONNECTED_COMPONENTS[newRow][newCol] = count;
                                q.add((newRow << 16) | newCol);
                                ++size;
                            }
                        }
                    }
                }
                CONNECTED_COMPONENT_SIZES.add(size);
            }
        }

        TEAM = gc.team();
        OTHER_TEAM = TEAM == Team.Red ? Team.Blue : Team.Red;
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

    public static boolean isAnyWithinDistance(List<MapLocation> locations, MapLocation target, int distance) {
        for (MapLocation loc : locations) {
            if (loc.distanceSquaredTo(target) <= distance) {
                return true;
            }
        }
        return false;
    }

    /** Clamps `val` to be within [min, max]. */
    public static double clamp(double min, double max, double val) {
        return (val < min) ? min : (val > max) ? max : val;
    }

    /** Clamps `val` to be within [min, max]. */
    public static float clamp(float min, float max, float val) {
        return (val < min) ? min : (val > max) ? max : val;
    }

    /** Clamps `val` to be within [min, max]. */
    public static int clamp(int min, int max, int val) {
        return (val < min) ? min : (val > max) ? max : val;
    }

    /** Gets all units that match the filter. */
    public static ArrayList<Unit> filterUnitsBy(VecUnit units, Predicate<Unit> filter) {
        int numUnits = (int) units.size();
        ArrayList<Unit> result = new ArrayList<>(numUnits);
        for (int i=0; i<numUnits; i++) {
            Unit unit = units.get(i);
            if (filter.test(unit)) result.add(unit);
        }
        return result;
    }

    /** Gets all units that match the filter. Does not modify `units` array.*/
    public static ArrayList<Unit> filterUnitsBy(ArrayList<Unit> units, Predicate<Unit> filter) {
        ArrayList<Unit> result = new ArrayList<>(units);
        result.removeIf(filter.negate());
        return result;
    }

    /**
     * Gets the unit with the lowest health.
     * NOTE: Assumes we can sense all the provided units.
     * @param units A list of units to check.
     * @param shouldPrioritize A function. When it returns true, these units are prioritized. If null, don't prioritize.
     * @return The lowest health unit found, or null if none were found.
     */
    public static Unit getLowestHealth(VecUnit units, Predicate<Unit> shouldPrioritize) {
        if (units == null) return null;
        int numUnits = (int) units.size();
        if (numUnits == 0) return null;
        Unit weakest = null;
        Unit weakestLowPrio = null;
        int weakestHealth = Integer.MAX_VALUE;
        int weakestHealthLowPrio = Integer.MAX_VALUE;
        for (int i = 0; i < numUnits; ++i) {
            Unit unit = units.get(i);
            assert gc.canSenseUnit(unit.id());
            int health = (int) unit.health();
            if (shouldPrioritize != null && shouldPrioritize.test(unit)) {
                if (health < weakestHealth) {
                    weakest = unit;
                    weakestHealth = health;
                }
            } else {
                if (health < weakestHealthLowPrio) {
                    weakestLowPrio = unit;
                    weakestHealthLowPrio = health;
                }
            }
        }
        return (weakest == null) ? weakestLowPrio : weakest;
    }

    /**
     * Gets the unit with the lowest health.
     * NOTE: Assumes we can sense all the provided units.
     * @param units A list of units to check.
     * @return The lowest health unit found.
     */
    public static Unit getLowestHealth(VecUnit units) {
        return getLowestHealth(units);
    }

    /**
     * Gets the closest unit.
     * NOTE: Assumes that we can sense all the units in `units`, and that they are on the map.
     * @param units A list of units to check.
     * @param loc The location to test proximity to.
     * @param shouldPrioritize A function. When it returns true, these units are prioritized. If null, don't prioritize.
     * @return The closest unit found, or null if none were found.
     */
    public static Unit getNearest(VecUnit units, MapLocation loc, Predicate<Unit> shouldPrioritize) {
        if (units == null) return null;
        int numUnits = (int) units.size();
        if (numUnits == 0) return null;
        Unit nearest = null;
        Unit nearestLowPrio = null;
        int nearistDist = Integer.MAX_VALUE;
        int nearestDistLowPrio = Integer.MAX_VALUE;
        for (int i=0; i<numUnits; i++) {
            Unit unit = units.get(i);
            assert gc.canSenseUnit(unit.id());
            int distance = (int) unit.location().mapLocation().distanceSquaredTo(loc);
            if (shouldPrioritize != null && shouldPrioritize.test(unit)) {
                if (distance < nearistDist) {
                    nearest = unit;
                    nearistDist = distance;
                }
            } else {
                if (distance < nearestDistLowPrio) {
                    nearestLowPrio = unit;
                    nearestDistLowPrio = distance;
                }
            }
        }
        return (nearest == null) ? nearestLowPrio : nearest;
    }

    /**
     * Gets the closest unit.
     * NOTE: Assumes that we can sense all the units in `units`, and that they are on the map.
     * @param units A list of units to check.
     * @param loc The location to test proximity to.
     * @return The closest unit found.
     */
    public static Unit getNearest(VecUnit units, MapLocation loc) {
        return getNearest(units, loc, null);
    }

    public static boolean isStructure(UnitType type) {
        return type == UnitType.Factory || type == UnitType.Rocket;
    }

    public static boolean isStructure(Unit unit) {
        return isStructure(unit.unitType());
    }

    public static boolean isStructure(MyUnit unit) {
        return isStructure(unit.getType());
    }

    public static boolean isAttacker(UnitType type) {
        return type == UnitType.Ranger ||
            type == UnitType.Knight ||
            type == UnitType.Mage;
    }

    public static boolean isAttacker(Unit unit) {
        return isAttacker(unit.unitType());
    }

    public static boolean isAttacker(MyUnit unit) {
        return isAttacker(unit.getType());
    }

}
