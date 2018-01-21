package org.battlecode.bc18.api;

import bc.Location;
import bc.MapLocation;
import bc.Team;
import bc.UnitType;
import org.battlecode.bc18.util.Pair;

import java.util.ArrayList;
import java.util.List;

public interface MyUnit {

    /**
     * Called when it is time for the unit to perform its action.
     * NOTE: Only called for alive units.
     */
    void act();

    /** Gets the type of unit */
    UnitType getType(); //This would be static but you cant have static abstract class

    /** Kaboom. */
    void selfDestruct();

    /**
     * Senses all units (friendly and enemy) within the given radius (inclusive, distance squared) by type.
     * Both elements of the Pair are guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A Pair of lists. First list is friendlies, second is enemies.
     */
    Pair<List<MyUnit>, List<bc.Unit>> senseNearbyUnits(int radius, UnitType type);

    /**
     * Senses all units (friendly and enemy) within the given radius (inclusive, distance squared).
     * Both elements of the Pair are guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A Pair of lists. First list is friendlies, second is enemies.
     */
    Pair<List<MyUnit>, List<bc.Unit>> senseNearbyUnits(int radius);

    /**
     * Senses all units (friendly and enemy) within vision by type.
     * Both elements of the Pair are guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A Pair of lists. First list is friendlies, second is enemies.
     */
    Pair<List<MyUnit>, List<bc.Unit>> senseNearbyUnits(UnitType type);

    /**
     * Senses all units (friendly and enemy) within vision.
     * Both elements of the Pair are guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A Pair of lists. First list is friendlies, second is enemies.
     */
    Pair<List<MyUnit>, List<bc.Unit>> senseNearbyUnits();

    /**
     * Senses all enemy units within the given radius (inclusive, distance squared) by type.
     * Returned list is guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A list of enemies.
     */
    List<bc.Unit> senseNearbyEnemies(int radius, UnitType type);

    /**
     * Senses all enemy units within the given radius (inclusive, distance squared).
     * Returned list is guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A list of enemy units.
     */
    List<bc.Unit> senseNearbyEnemies(int radius);

    /**
     * Senses all enemy units within vision by type.
     * Returned list is guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A list of enemy units.
     */
    List<bc.Unit> senseNearbyEnemies(UnitType type);

    /**
     * Senses all enemy units within vision.
     * Returned list is guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A list of enemy units.
     */
    List<bc.Unit> senseNearbyEnemies();

    /**
     * Senses all friendly units within the given radius (inclusive, distance squared) by type.
     * Returned list is guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A list of friendly units.
     */
    List<MyUnit> senseNearbyFriendlies(int radius, UnitType type);

    /**
     * Senses all friendly units within the given radius (inclusive, distance squared).
     * Returned list is guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A list of friendly units.
     */
    List<MyUnit> senseNearbyFriendlies(int radius);

    /**
     * Senses all friendly units within vision by type.
     * Returned list is guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A list of friendly units.
     */
    List<MyUnit> senseNearbyFriendlies(UnitType type);

    /**
     * Senses all friendly units within vision.
     * Returned list is guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A list of friendly units.
     */
    List<MyUnit> senseNearbyFriendlies();

    /** Gets the id of the unit */
    int getID();

    /** Gets the team of the unit. */
    Team getTeam();

    /** Gets the maximum health of the unit */
    int getMaxHealth();

    /** Gets the vision range of the unit */
    int getVisionRange();

    /**
     * Gets the unit's current location on the map.
     * NOTE: Does not check to ensure that the unit is on the map first.
     */
    MapLocation getMapLocation();

    /** Gets the unit's current location. */
    Location getLocation();

    /** Whether the unit is on the map. */
    boolean isOnMap();

    /** Whether the unit is in a garrison */
    boolean isInGarrison();

    /** Gets the health of the unit */
    int getHealth();

    /** Whether the structure's garrision is full */
    boolean isGarrisonFull();

    /**
     * Gets the locations that contain karbonite within some radius.
     * NOTE: Requires that this unit is on the map first, and that radius <= getVisionRange().
     * @param radius The distance to look (in units squared).
     * @return ArrayList of Pairs of MapLocations and amounts of karbonite.
     */
    ArrayList<Pair<MapLocation, Integer>> senseNearbyKarbonite(int radius);

    /**
     * Gets the locations that contain karbonite within a radius.
     * NOTE: Requires that this unit is on the map first.
     * @return ArrayList of Pairs of MapLocations and amounts of karbonite.
     */
    ArrayList<Pair<MapLocation, Integer>> senseNearbyKarbonite();

    /** Whether the unit is dead or not. */
    boolean isDead();
}
