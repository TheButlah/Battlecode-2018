package org.battlecode.bc18.bots.noobbot.units;

import bc.*;
import org.battlecode.bc18.bots.util.Pair;

import java.util.*;

import static org.battlecode.bc18.bots.util.Utils.gc;

/** Superclass for the different types of units able to be controlled by our player. */
@SuppressWarnings("unused")
public abstract class MyUnit {

    /**
     * Unmodifiable mapping from id to MyUnit objects (safe for external use).
     * Has fixed order where newest units are last.
     * Must only contain units belonging to our player, i.e. on our planet under our team.
     * NOTE: Do not attempt to iterate through this map unless if using `Map.forEach()`.
     */
    public static final Map<Integer, MyUnit> units;

    /** Tells the unit to perform its action for this turn */
    public abstract void act();

    /** Gets the type of unit */
    public abstract UnitType getType(); //This would be static but you cant have static abstract class

    /** Kaboom. */
    public void selfDestruct() {
        gc.disintegrateUnit(id);
    }

    /**
     * Senses all units (friendly and enemy) within the given radius (inclusive, distance squared) by type.
     * Both elements of the Pair are guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A Pair of lists. First list is friendlies, second is enemies.
     */
    public Pair<List<MyUnit>, List<Unit>> senseNearbyUnits(int radius, UnitType type) {
        assert isOnMap();
        assert radius <= getVisionRange();

        VecUnit vec = (type == null) ?
            gc.senseNearbyUnits(getMapLocation(), radius) :
            gc.senseNearbyUnitsByType(getMapLocation(), radius, type);

        ArrayList<MyUnit> myUnits = new ArrayList<>((int) vec.size());
        ArrayList<Unit> enemyUnits = new ArrayList<>((int) vec.size());
        for (int i=0; i<vec.size(); i++) {
            Unit unit = vec.get(i);
            if (unit.team() == gc.team()) myUnits.add(getUnit(unit));
            else enemyUnits.add(unit);
        }
        return new Pair<>(myUnits, enemyUnits);
    }

    /**
     * Senses all units (friendly and enemy) within the given radius (inclusive, distance squared).
     * Both elements of the Pair are guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A Pair of lists. First list is friendlies, second is enemies.
     */
    public Pair<List<MyUnit>, List<Unit>> senseNearbyUnits(int radius) {
        return senseNearbyUnits(radius, null);
    }

    /**
     * Senses all units (friendly and enemy) within vision by type.
     * Both elements of the Pair are guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A Pair of lists. First list is friendlies, second is enemies.
     */
    public Pair<List<MyUnit>, List<Unit>> senseNearbyUnits(UnitType type) {
        return senseNearbyUnits(getVisionRange(), type);
    }

    /**
     * Senses all units (friendly and enemy) within vision.
     * Both elements of the Pair are guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A Pair of lists. First list is friendlies, second is enemies.
     */
    public Pair<List<MyUnit>, List<Unit>> senseNearbyUnits() {
        return senseNearbyUnits(getVisionRange(), null);
    }

    /**
     * Senses all enemy units within the given radius (inclusive, distance squared) by type.
     * Returned list is guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A list of enemies.
     */
    public List<Unit> senseNearbyEnemies(int radius, UnitType type) {
        assert isOnMap();
        assert radius <= getVisionRange();

        VecUnit vec = (type == null) ?
            gc.senseNearbyUnits(getMapLocation(), radius) :
            gc.senseNearbyUnitsByType(getMapLocation(), radius, type);

        ArrayList<Unit> enemyUnits = new ArrayList<>((int) vec.size());
        for (int i=0; i<vec.size(); i++) {
            Unit unit = vec.get(i);
            if (unit.team() != gc.team()) enemyUnits.add(unit);
        }
        return enemyUnits;
    }

    /**
     * Senses all enemy units within the given radius (inclusive, distance squared).
     * Returned list is guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A list of enemy units.
     */
    public List<Unit> senseNearbyEnemies(int radius) {
        return senseNearbyEnemies(radius, null);
    }

    /**
     * Senses all enemy units within vision by type.
     * Returned list is guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A list of enemy units.
     */
    public List<Unit> senseNearbyEnemies(UnitType type) {
        return senseNearbyEnemies(getVisionRange(), type);
    }

    /**
     * Senses all enemy units within vision.
     * Returned list is guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A list of enemy units.
     */
    public List<Unit> senseNearbyEnemies() {
        return senseNearbyEnemies(getVisionRange(), null);
    }

    /**
     * Senses all friendly units within the given radius (inclusive, distance squared) by type.
     * Returned list is guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A list of friendly units.
     */
    public List<MyUnit> senseNearbyFriendlies(int radius, UnitType type) {
        assert isOnMap();
        assert radius <= getVisionRange();

        VecUnit vec = (type == null) ?
            gc.senseNearbyUnits(getMapLocation(), radius) :
            gc.senseNearbyUnitsByType(getMapLocation(), radius, type);

        ArrayList<MyUnit> myUnits = new ArrayList<>((int) vec.size());
        for (int i=0; i<vec.size(); i++) {
            Unit unit = vec.get(i);
            if (unit.team() == gc.team()) myUnits.add(getUnit(unit));
        }
        return myUnits;
    }

    /**
     * Senses all friendly units within the given radius (inclusive, distance squared).
     * Returned list is guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A list of friendly units.
     */
    public List<MyUnit> senseNearbyFriendlies(int radius) {
        return senseNearbyFriendlies(radius, null);
    }

    /**
     * Senses all friendly units within vision by type.
     * Returned list is guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A list of friendly units.
     */
    public List<MyUnit> senseNearbyFriendlies(UnitType type) {
        return senseNearbyFriendlies(getVisionRange(), type);
    }

    /**
     * Senses all friendly units within vision.
     * Returned list is guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A list of friendly units.
     */
    public List<MyUnit> senseNearbyFriendlies() {
        return senseNearbyFriendlies(getVisionRange(), null);
    }

    /** Gets the unit as a Unit object */
    public Unit getAsUnit() {
        return gc.unit(id);
        //TODO: cache this somehow
    }

    /** Gets the id of the unit */
    public int getID() {
        return id;
    }

    /** Gets the team of the unit. */
    public Team getTeam() {
        return team;
    }

    /** Gets the maximum health of the unit */
    public int getMaxHealth() {
        return maxHealth;
    }

    /** Gets the vision range of the unit */
    public int getVisionRange() {
        return (int) getAsUnit().visionRange();
    }

    /**
     * Gets the unit's current location on the map.
     * NOTE: Does not check to ensure that the unit is on the map first.
     */
    public MapLocation getMapLocation() {
        assert isOnMap();
        return getLocation().mapLocation();
        //TODO: Cache this?
    }

    /** Gets the unit's current location. */
    public Location getLocation() {
        return location;
    }

    /** Whether the unit is on the map. */
    public boolean isOnMap() {
        return getLocation().isOnMap();
    }

    /** Gets the health of the unit */
    public int getHealth() {
        return (int) getAsUnit().health();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; //short circuit
        if (!(obj instanceof MyUnit)) return false;
        MyUnit other = (MyUnit) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "[" + getType() + ":" + this.id + "]";
    }



    //////////END OF API//////////



    /**
     * Mapping from id to MyUnit objects (for internal use only).
     * Ordered by insertion order.
     * Must only contain units belonging to our player, i.e. on our planet under our team.
     * NOTE: Do not attempt to iterate through this map unless if using `Map.forEach()`.
     */
    private static final Map<Integer, MyUnit> unitsModifiable;

    private final int id;
    private final Team team;
    private final int maxHealth;
    private Location location;

    //Static initializer to ensure that right from the start, MyUnit knows all of our units.
    static {
        VecUnit vec = gc.myUnits();
        unitsModifiable = new LinkedHashMap<>((int) vec.size());
        units = Collections.unmodifiableMap(unitsModifiable);
        for (int i=0; i<vec.size(); i++) {
            makeUnit(vec.get(i));
        }
    }

    /**
     * Constructor for MyUnit.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    MyUnit(Unit unit) throws RuntimeException{
        this.id = unit.id();
        this.team = unit.team();
        this.location = unit.location();
        this.maxHealth = (int) unit.maxHealth();

        if (this.team != gc.team() || !this.location.isOnPlanet(gc.planet())) {
            throw new RuntimeException("The unit " + unit + " doesn't belong to us!");
        }

        if (unitsModifiable.put(id, this) != null) throw new RuntimeException("The unit " + unit + " already exists!");
    }

    /**
     * Gets the MyUnit object that corresponds to a Unit object.
     * @param unit The Unit object. It should belong to our player.
     * @return The associated MyUnit object.
     */
    static MyUnit getUnit(Unit unit) {
        //`computeIfAbsent` is used to add any unidentified units to the list
        return unitsModifiable.computeIfAbsent(
            unit.id(),
            (k) -> makeUnit(unit)
        );
    }

    /**
     * Gets the MyUnit objects that correspond to a VecUnit object.
     * @param units The VecUnit object. All contained units should belong to our player.
     * @return The associated list of MyUnit objects.
     */
    static List<MyUnit> getUnits(VecUnit units) {
        ArrayList<MyUnit> result = new ArrayList<>((int) units.size());
        for (int i=0; i<units.size(); i++) {
            Unit unit = units.get(i);
            result.add(getUnit(unit));
        }
        return result;
    }

    /**
     * Gets the MyUnit objects that correspond to a Collection.
     * @param units The Collection of units. All contained units should belong to our player.
     * @return The associated list of MyUnit objects.
     */
    static List<MyUnit> getUnits(Collection<Unit> units) {
        ArrayList<MyUnit> result = new ArrayList<>(units.size());
        for (Unit unit : units) result.add(getUnit(unit));
        return result;
    }

    /**
     * Constructs a MyUnit object based off of a Unit and adds it to the HashMap of units.
     * NOTE: The unit must belong to our Player, i.e. on our Planet under our Team.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, or unit doesn't belong to Player.
     */
    static MyUnit makeUnit(Unit ourUnit) {
        int id = ourUnit.id();
        UnitType type = ourUnit.unitType();
        switch(type) {
            case Worker:
                return new Worker(ourUnit);
            case Knight:
                return new Knight(ourUnit);
            case Ranger:
                return new Ranger(ourUnit);
            case Mage:
                return new Mage(ourUnit);
            case Healer:
                return new Healer(ourUnit);
            case Factory:
                return new Factory(ourUnit);
            case Rocket:
                return new Rocket(ourUnit);
            default:
                throw new RuntimeException("Unrecognized UnitType: " + type); //Should never happen
        }
    }

    /**
     * Prints to stdout the contents of `obj` prefixed by the unit info.
     * @param obj The data to print out.
     */
    void println(Object obj) {
        System.out.println(this + " " + obj);
    }

    /**
     * Sets the location of the unit.
     * @return The previous location.
     */
    Location setLocation(Location newLoc) {
        Location oldLoc = this.location;
        this.location = newLoc;
        return oldLoc;
    }

    /**
     * Sets the location of the unit.
     * @return The previous location.
     */
    Location setLocation(MapLocation newLoc) {
        Location oldLoc = this.location;
        this.location = bc.bcLocationNewOnMap(newLoc);
        return oldLoc;
    }


}
