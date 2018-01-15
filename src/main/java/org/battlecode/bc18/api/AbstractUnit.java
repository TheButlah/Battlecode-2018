package org.battlecode.bc18.api;

import java.util.*;

import bc.Location;
import bc.MapLocation;
import bc.Team;
import bc.Unit;
import bc.UnitType;
import bc.VecMapLocation;
import bc.VecUnit;
import bc.VecUnitID;
import bc.bc;

import org.battlecode.bc18.util.Pair;

import static org.battlecode.bc18.util.Utils.gc;

/** Superclass for the different types of units able to be controlled by our player. */
@SuppressWarnings("unused")
public abstract class AbstractUnit {

    /** Sets the UnitBuilder that will be used to make units. Should be run at very start. */
    public static void init(UnitBuilder builder) {
        AbstractUnit.builder = builder;
        VecUnit vec = gc.myUnits();
        int numUnits = (int) vec.size();
        for (int i=0; i<numUnits; i++) {
            makeUnit(vec.get(i));
        }
    }

    /** Prepares the AbstractUnit objects for their logic this turn. */
    public static void initTurn() {
        //Clean out all dead units from unitList. This is
        unitList.removeIf(AbstractUnit::isDead);
    }

    /** Goes through all our player's units and has them act. Requires `initTurn()` to run first. */
    public static void doTurn() {
        //Use ListIterator so that we can modify the list while we loop
        for (ListIterator<AbstractUnit> it = unitList.listIterator(); it.hasNext();) {
            AbstractUnit unit = it.next();
            try { //Avoid breaking the loop leading to instant loss
                if (unit.isDead()) continue; //Don't act on dead units
                unit.act();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called when it is time for the unit to perform its action.
     * NOTE: Only called for alive units.
     */
    public abstract void act();

    /** Gets the type of unit */
    public abstract UnitType getType(); //This would be static but you cant have static abstract class

    /** Kaboom. */
    public void selfDestruct() {
        gc.disintegrateUnit(id);
        informOfDeath();
    }

    /**
     * Senses all units (friendly and enemy) within the given radius (inclusive, distance squared) by type.
     * Both elements of the Pair are guaranteed to not be null.
     * The radius must be within the vision range.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A Pair of lists. First list is friendlies, second is enemies.
     */
    public Pair<List<AbstractUnit>, List<Unit>> senseNearbyUnits(int radius, UnitType type) {
        assert isOnMap();
        assert radius <= getVisionRange();

        VecUnit vec = (type == null) ?
            gc.senseNearbyUnits(getMapLocation(), radius) :
            gc.senseNearbyUnitsByType(getMapLocation(), radius, type);

        ArrayList<AbstractUnit> myUnits = new ArrayList<>((int) vec.size());
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
    public Pair<List<AbstractUnit>, List<Unit>> senseNearbyUnits(int radius) {
        return senseNearbyUnits(radius, null);
    }

    /**
     * Senses all units (friendly and enemy) within vision by type.
     * Both elements of the Pair are guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A Pair of lists. First list is friendlies, second is enemies.
     */
    public Pair<List<AbstractUnit>, List<Unit>> senseNearbyUnits(UnitType type) {
        return senseNearbyUnits(getVisionRange(), type);
    }

    /**
     * Senses all units (friendly and enemy) within vision.
     * Both elements of the Pair are guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A Pair of lists. First list is friendlies, second is enemies.
     */
    public Pair<List<AbstractUnit>, List<Unit>> senseNearbyUnits() {
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
    public List<AbstractUnit> senseNearbyFriendlies(int radius, UnitType type) {
        assert isOnMap();
        assert radius <= getVisionRange();

        VecUnit vec = (type == null) ?
            gc.senseNearbyUnits(getMapLocation(), radius) :
            gc.senseNearbyUnitsByType(getMapLocation(), radius, type);

        ArrayList<AbstractUnit> myUnits = new ArrayList<>((int) vec.size());
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
    public List<AbstractUnit> senseNearbyFriendlies(int radius) {
        return senseNearbyFriendlies(radius, null);
    }

    /**
     * Senses all friendly units within vision by type.
     * Returned list is guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @param type The type of unit to sense. If null, sense all units.
     * @return A list of friendly units.
     */
    public List<AbstractUnit> senseNearbyFriendlies(UnitType type) {
        return senseNearbyFriendlies(getVisionRange(), type);
    }

    /**
     * Senses all friendly units within vision.
     * Returned list is guaranteed to not be null.
     * NOTE: Does not check to ensure that this unit is on the map first.
     * @return A list of friendly units.
     */
    public List<AbstractUnit> senseNearbyFriendlies() {
        return senseNearbyFriendlies(getVisionRange(), null);
    }

    /**
     * Gets the AbstractUnit object that corresponds to a Unit object.
     * @param unit The Unit object. It should belong to our player and not be dead.
     * @return The associated AbstractUnit object.
     */
    public static AbstractUnit getUnit(Unit unit) {
        //`computeIfAbsent` is used to add any unidentified units to the list
        return units.computeIfAbsent(
            unit.id(),
            (k) -> makeUnit(unit)
        );
    }

    /**
     * Gets the AbstractUnit object that corresponds to a unit id.
     * @param id The unit id. It should belong to our player and not be dead.
     * @return The associated AbstractUnit object.
     */
    public static AbstractUnit getUnit(int id) {
        //`computeIfAbsent` is used to add any unidentified units to the list
        return units.computeIfAbsent(id, AbstractUnit::makeUnit);
    }

    /**
     * Gets the AbstractUnit objects that correspond to a VecUnit object.
     * @param units The VecUnit object. All contained units should belong to our player and not be dead.
     * @return The associated list of AbstractUnit objects.
     */
    public static List<? extends AbstractUnit> getUnits(VecUnit units) {
        List<AbstractUnit> result = new ArrayList<>((int) units.size());
        for (int i=0; i<units.size(); i++) {
            Unit unit = units.get(i);
            result.add(getUnit(unit));
        }
        return result;
    }

    /**
     * Gets the AbstractUnit objects that correspond to a VecUnitID object.
     * @param units The VecUnitID object. All contained units should belong to our player and not be dead.
     * @return The associated list of AbstractUnit objects.
     */
    public static List<? extends AbstractUnit> getUnits(VecUnitID units) {
        List<AbstractUnit> result = new ArrayList<>((int) units.size());
        for (int i=0; i<units.size(); i++) {
            int id = units.get(i);
            result.add(getUnit(id));
        }
        return result;
    }

    /**
     * Gets the AbstractUnit objects that correspond to a Collection of units.
     * @param units The Collection of units. All contained units should belong to our player and not be dead.
     * @return The associated list of AbstractUnit objects.
     */
    public static List<AbstractUnit> getUnits(Collection<Unit> units) {
        List<AbstractUnit> result = new ArrayList<>(units.size());
        for (Unit unit : units) result.add(getUnit(unit));
        return result;
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

    /** Whether the unit is in a garrison */
    public boolean isInGarrison() {
        return getLocation().isInGarrison();
    }

    /** Gets the health of the unit */
    public int getHealth() {
        return (int) getAsUnit().health();
    }


    /**
     * Gets the locations that contain karbonite within some radius.
     * NOTE: Requires that this unit is on the map first, and that radius <= getVisionRange().
     * @param radius The distance to look (in units squared).
     * @return ArrayList of Pairs of MapLocations and amounts of karbonite.
     */
    public ArrayList<Pair<MapLocation, Integer>> senseNearbyKarbonite(int radius) {
        assert radius <= getVisionRange();
        assert isOnMap();
        ArrayList<Pair<MapLocation, Integer>> nearbyKarbonite = new ArrayList<>();
        VecMapLocation locs = gc.allLocationsWithin(getMapLocation(), radius);

        for(int i = 0; i < locs.size(); i++) {
            MapLocation loc = locs.get(i);
            int amount = (int) gc.karboniteAt(loc);
            if (amount > 0) {
                nearbyKarbonite.add(new Pair<>(loc, amount));
            }
        }
        return nearbyKarbonite;
    }

    /**
     * Gets the locations that contain karbonite within a radius.
     * NOTE: Requires that this unit is on the map first.
     * @return ArrayList of Pairs of MapLocations and amounts of karbonite.
     */
    public ArrayList<Pair<MapLocation, Integer>> senseNearbyKarbonite() {
        return senseNearbyKarbonite(getVisionRange());
    }

    /** Whether the unit is dead or not. */
    public boolean isDead() {
        return isDead;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; //short circuit
        if (!(obj instanceof AbstractUnit)) return false;
        AbstractUnit other = (AbstractUnit) obj;
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

    /**
     * Prints to stdout the contents of `obj` prefixed by the unit info.
     * @param obj The data to print out.
     */
    protected void println(Object obj) {
        System.out.println(this + " " + obj);
    }

    /** Called whenever this unit dies. Use this to update any referenced you made to the unit. */
    protected abstract void onDeath();



    //////////END OF API//////////



    /**
     * Mapping from id to AbstractUnit objects (for internal use only).
     * Must only contain alive units belonging to our player, i.e. on our planet under our team.
     * NOTE: Do not attempt to iterate through this map unless you're sure you won't modify it.
     */
    private static final Map<Integer, AbstractUnit> units = new HashMap<>();
    private static final List<AbstractUnit> unitList = new LinkedList<>();

    private final int id;
    private final Team team;
    private final int maxHealth;

    private Location location;
    private boolean isDead;
    private static UnitBuilder builder;

    /**
     * Constructor for AbstractUnit.
     * @exception RuntimeException When unit already exists, has unknown type, doesn't belong to our player, or is dead.
     */
    AbstractUnit(Unit unit) throws RuntimeException{
        this.id = unit.id();
        this.team = unit.team();
        this.location = unit.location();
        this.maxHealth = (int) unit.maxHealth();

        if (this.team != gc.team() || (!this.location.isInGarrison() && !this.location.isOnPlanet(gc.planet()))) {
            throw new RuntimeException("The unit " + unit + " doesn't belong to us!");
        } else if (!gc.canSenseUnit(id)) {
            throw new RuntimeException("The unit " + unit + " is dead!");
        }

        //Put the unit into `units` HashMap
        AbstractUnit previousValue = units.put(id, this);
        if (previousValue != null) {
            units.put(id, previousValue); //restore the value
            throw new RuntimeException("The unit " + unit + " already exists!");
        }
        unitList.add(this);
    }

    /**
     * Constructs an AbstractUnit object based off of a Unit and adds it to the collections of units.
     * The unit must belong to our Player, i.e. on our Planet under our Team.
     * The unit must be alive.
     * The unit must not already have been created.
     * @param ourUnit The Unit object to make the AbstractUnit object from.
     * @exception RuntimeException When the unit has an unknown type.
     */
    static AbstractUnit makeUnit(Unit ourUnit) {
        return builder.newUnit(ourUnit);
    }

    /**
     * Constructs an AbstractUnit object based off of a unit's id and adds it to the collections of units.
     * The unit must belong to our Player, i.e. on our Planet under our Team.
     * The unit must be alive.
     * The unit must not already have been created.
     * @param ourUnit The id of the unit to make the AbstractUnit object from.
     * @exception RuntimeException When the unit has an unknown type.
     */
    static AbstractUnit makeUnit(int ourUnit) {
        assert gc.canSenseUnit(ourUnit);
        return makeUnit(gc.unit(ourUnit));
    }

    /**
     * Updates `isDead`, removes this unit from `units`, and calls `onDeath()`.
     * NOTE: Does not remove the unit from unitList.
     * NOTE: The unit must actually be dead.
     */
    void informOfDeath() {
        assert !gc.canSenseUnit(getID()); //Test to see if its dead
        isDead = true;
        units.remove(getID());
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
