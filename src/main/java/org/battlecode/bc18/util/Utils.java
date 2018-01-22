package org.battlecode.bc18.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
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
import bc.Veci32;

public final class Utils {
    //private Utils() {} //Prevent instantiation

    public static final GameController gc;

    public static final long SEED;
    public static final Random rand;

    public static final Direction[] dirs = Direction.values();
    public static final PlanetMap EARTH_START, MARS_START;
    public static final int EARTH_MAP_WIDTH, EARTH_MAP_HEIGHT;
    public static final int MARS_MAP_WIDTH, MARS_MAP_HEIGHT;
    public static final int MAP_WIDTH, MAP_HEIGHT;
    public static final Planet PLANET;
    public static final int[][] CONNECTED_COMPONENTS;
    public static final ArrayList<Integer> CONNECTED_COMPONENT_SIZES;
    public static final int IMPASSIBLE_TERRAIN = 9999;

    public static final Team TEAM;
    public static final Team OTHER_TEAM;

    public static final int COMMUNICATION_ARRAY_LENGTH = 100; // TODO: this doesn't seem to be available via the API?

    private static int landingLocationIndex = 0;

    static {
        gc = new GameController();
        TEAM = gc.team();
        OTHER_TEAM = TEAM == Team.Red ? Team.Blue : Team.Red;
        SEED = TEAM == Team.Red ? 8675309 : 8675310;
        rand = new Random(SEED);
        PLANET = gc.planet();
        EARTH_START = gc.startingMap(Planet.Earth);
        MARS_START = gc.startingMap(Planet.Mars);
        EARTH_MAP_WIDTH = (int) EARTH_START.getWidth();
        EARTH_MAP_HEIGHT = (int) EARTH_START.getHeight();
        MARS_MAP_WIDTH = (int) MARS_START.getWidth();
        MARS_MAP_HEIGHT = (int) MARS_START.getHeight();
        MAP_WIDTH = PLANET == Planet.Earth ? EARTH_MAP_WIDTH : MARS_MAP_WIDTH;
        MAP_HEIGHT = PLANET == Planet.Earth ? EARTH_MAP_HEIGHT : MARS_MAP_HEIGHT;

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
        if (Utils.PLANET == Planet.Mars) {
            broadcastLandingLocations();
            //System.out.println("Num landing locations: " + gc.getTeamArray(Planet.Mars).get(0));
            //for (int i = 0; i < COMMUNICATION_ARRAY_LENGTH; ++i) {
            //    System.out.println(getNextLandingLocation());
            //}
        }
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

    private static void broadcastLandingLocations() {
        // Compile mapping of CC to locations within that CC
        ArrayList<Pair<Integer, Integer>> ccBySize = new ArrayList<>();
        for (int cc = 1; cc < CONNECTED_COMPONENT_SIZES.size(); ++cc) {
            ccBySize.add(new Pair<Integer, Integer>(cc, CONNECTED_COMPONENT_SIZES.get(cc)));
        }
        // Sort connected components by size, decreasing
        ccBySize.sort(new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                return o2.getSecond() - o1.getSecond();
            }
        });
        int[][] locationsWithinCC = new int[CONNECTED_COMPONENT_SIZES.size()][];
        ArrayList<HashSet<Integer>> landingCoordinates = new ArrayList<>(CONNECTED_COMPONENT_SIZES.size());
        for (int cc = 0; cc < CONNECTED_COMPONENT_SIZES.size(); ++cc) {
            locationsWithinCC[cc] = new int[CONNECTED_COMPONENT_SIZES.get(cc)];
            landingCoordinates.add(new HashSet<>());
        }
        // Indices within locationsWithinCC arrays
        int[] locationsWithinCCIndices = new int[CONNECTED_COMPONENT_SIZES.size()];
        for (int r = 0; r < MAP_HEIGHT; ++r) {
            for (int c = 0; c < MAP_WIDTH; ++c) {
                int cc = CONNECTED_COMPONENTS[r][c];
                if (cc == IMPASSIBLE_TERRAIN) continue;
                int coord = (r << 16) | c;
                locationsWithinCC[cc][locationsWithinCCIndices[cc]++] = coord;
            }
        }
        //System.out.println(Arrays.deepToString(locationsWithinCC));
        int communicationArrayIndex = 1;
        int totalNumCoords = 0; // Keep track of the size of a set of landing coordinates that spans all connected components
        // Fill communication array until we can no longer fit a set of totalNumCoords into the array
        while (COMMUNICATION_ARRAY_LENGTH - communicationArrayIndex >= totalNumCoords) {
            totalNumCoords = 0;
            // Choose (cc size)^(1/4) locations in each cc
            for (int cc = 1; cc < CONNECTED_COMPONENT_SIZES.size(); ++cc) {
                HashSet<Integer> chosenCoords = landingCoordinates.get(cc);
                // Clear any previously generated landing locations for each connected component
                chosenCoords.clear();
                // Generate new unique landing locations (not necessarily distinct from previously generated locations)
                int numDesiredCoords = (int) Math.pow(CONNECTED_COMPONENT_SIZES.get(cc), 1d/4d);
                int[] locations = locationsWithinCC[cc];
                for (; chosenCoords.size() < numDesiredCoords;) {
                    int randCoord = locations[rand.nextInt(locations.length)];
                    chosenCoords.add(randCoord);
                }
                totalNumCoords += numDesiredCoords;
            }
            if (totalNumCoords > COMMUNICATION_ARRAY_LENGTH - communicationArrayIndex) {
                // Prevent overflow of communication array
                break;
            }
            //for (int cc = 1; cc < CONNECTED_COMPONENT_SIZES.size(); ++cc) {
            //    System.out.println(landingCoordinates.get(cc));
            //}
            // Write landing locations to communication array in decreasing order of connected component size
            for (Pair<Integer, Integer> ccSizePair : ccBySize) {
                int cc = ccSizePair.getFirst();
                for (Integer coord : landingCoordinates.get(cc)) {
                    gc.writeTeamArray(communicationArrayIndex, coord);
                    ++communicationArrayIndex;
                }
            }
        }
        gc.writeTeamArray(0, communicationArrayIndex - 1); // write number of generated landing locations
    }

    /**
     * Pre-condition: If the calling context is the Earth player, the round is at least COMMUNICATION_DELAY
     * @return the next landing location on Mars, in the format {@code (row << 16) | column}
     */
    public static int getNextLandingLocation() {
        Veci32 landingLocations = gc.getTeamArray(Planet.Mars);
        int nextLandingLocation = landingLocations.get(1 + landingLocationIndex);
        return nextLandingLocation;
    }

    public static void advanceLandingLocation() {
        Veci32 landingLocations = gc.getTeamArray(Planet.Mars);
        int numLandingLocations = landingLocations.get(0);
        landingLocationIndex = (landingLocationIndex + 1) % numLandingLocations;
    }

    public static Direction rotateDirClockwise(Direction dir) {
        switch (dir) {
            case Center:
                return Direction.Center;
            case East:
                return Direction.Southeast;
            case North:
                return Direction.Northeast;
            case Northeast:
                return Direction.East;
            case Northwest:
                return Direction.North;
            case South:
                return Direction.Southwest;
            case Southeast:
                return Direction.South;
            case Southwest:
                return Direction.West;
            case West:
                return Direction.Northwest;
            default:
                return dir;
        }
    }

    public static Direction rotateDirCounterClockwise(Direction dir) {
        switch(dir) {
            case Center:
                return Direction.Center;
            case East:
                return Direction.Northeast;
            case North:
                return Direction.Northwest;
            case Northeast:
                return Direction.North;
            case Northwest:
                return Direction.West;
            case South:
                return Direction.Southeast;
            case Southeast:
                return Direction.East;
            case Southwest:
                return Direction.South;
            case West:
                return Direction.Southwest;
            default:
                return dir;
        }
    }

    public static Direction angleToDirection(double angle) {
        if (-22.5 * Math.PI / 180 < angle && angle <= 22.5 * Math.PI / 180) {
            return Direction.East;
        }
        if (22.5 * Math.PI / 180 < angle && angle <= 67.5 * Math.PI / 180) {
            return Direction.Northeast;
        }
        if (67.5 * Math.PI / 180 < angle && angle <= 112.5 * Math.PI / 180) {
            return Direction.North;
        }
        if (112.5 * Math.PI / 180 < angle && angle <= 157.5 * Math.PI / 180) {
            return Direction.Northwest;
        }
        if (157.5 * Math.PI / 180 < angle || angle < -157.5 * Math.PI / 180) {
            return Direction.West;
        }
        if (-157.5 * Math.PI / 180 < angle && angle < -112.5 * Math.PI / 180) {
            return Direction.Southwest;
        }
        if (-112.5 * Math.PI / 180 < angle && angle < -67.5 * Math.PI / 180) {
            return Direction.South;
        }
        if (-67.5 * Math.PI / 180 < angle && angle < -22.5 * Math.PI / 180) {
            return Direction.Southeast;
        }
        return Direction.Center;
    }
}
