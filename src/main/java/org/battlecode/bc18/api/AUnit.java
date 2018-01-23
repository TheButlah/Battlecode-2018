package org.battlecode.bc18.api;

import static org.battlecode.bc18.util.Utils.gc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.battlecode.bc18.util.Pair;
import org.battlecode.bc18.util.Utils;

import bc.Location;
import bc.MapLocation;
import bc.Planet;
import bc.RocketLanding;
import bc.RocketLandingInfo;
import bc.Team;
import bc.Unit;
import bc.UnitType;
import bc.VecMapLocation;
import bc.VecRocketLanding;
import bc.VecUnit;
import bc.VecUnitID;
import bc.bc;

/** Superclass for the different types of units able to be controlled by our player. */
@SuppressWarnings("unused")
public abstract class AUnit implements MyUnit {

    /** Sets the UnitBuilder that will be used to make units. Should be run at very start. */
    public static void init(UnitBuilder builder) {
        AUnit.builder = builder;
        VecUnit vec = gc.myUnits();
        int numUnits = (int) vec.size();
        for (int i=0; i<numUnits; i++) {
            makeUnit(vec.get(i));
        }
    }

    /** Prepares the MyUnit objects for their logic this turn. */
    private static void filterDeadUnits() {
        //Clean out all dead units from unitList. This is
        unitList.forEach((unit) -> {
            if (!gc.canSenseUnit(unit.id)) {
                unit.informOfDeath();
            }
        });
        unitList.removeIf(MyUnit::isDead);
    }

    /** Goes through all our player's units and has them act. Requires `initTurn()` to run first. */
    public static void doTurn() {
        //TODO: before calling act() for structure, call makeUnit() on its newly produced units by
        //TODO: keeping a Queue of the turn numbers at which units should be ready.
        //TODO: Peek the queue to see if the turn matches this one, and if it does call makeUnit()
        //TODO: on last member of garrison
        if (Utils.PLANET == Planet.Mars) {
            // Create MyUnit wrappers for imminently landing units
            RocketLandingInfo landingInfo = gc.rocketLandings();
            VecRocketLanding landingsThisRound = landingInfo.landingsOn(gc.round());
            for (int i = 0; i < landingsThisRound.size(); ++i) {
                RocketLanding rocketLanding = landingsThisRound.get(i);
                int rocketId = rocketLanding.getRocket_id();
                if (!gc.canSenseUnit(rocketId)) {
                    continue;
                }
                Unit rocketUnit = gc.unit(rocketId);
                if (rocketUnit.team() == Utils.OTHER_TEAM) {
                    continue;
                }
                AUnit rocket = (AUnit) getUnit(rocketUnit);
                rocket.setLocation(rocketLanding.getDestination());
                VecUnitID garrisonedUnits = rocket.getAsUnit().structureGarrison();
                for (int j = 0; j < garrisonedUnits.size(); ++j) {
                    getUnit(garrisonedUnits.get(j));
                }
            }
        }
        else {
            // Update factory production queue counts
            updateFactoryProductionQueue();
        }
        filterDeadUnits();
        // Perform all structure actions
        for (int i = 0; i < unitList.size(); ++i) {
            MyUnit unit = unitList.get(i);
            UnitType unitType = unit.getType();
            if (unitType != UnitType.Factory && unitType != UnitType.Rocket) continue;
            try { //Avoid breaking the loop leading to instant loss
                if (unit.isDead()) continue; //Don't act on dead units
                //long startTime = System.currentTimeMillis();
                unit.act();
                //System.out.println("Took: " + (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        filterDeadUnits();
        // Perform all robot actions
        for (int i = 0; i < unitList.size(); ++i) {
            MyUnit unit = unitList.get(i);
            UnitType unitType = unit.getType();
            if (unitType == UnitType.Factory || unitType == UnitType.Rocket) continue;
            try { //Avoid breaking the loop leading to instant loss
                if (unit.isDead()) continue; //Don't act on dead units
                //long startTime = System.currentTimeMillis();
                unit.act();
                //System.out.println("Took: " + (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the MyUnit object that corresponds to a MyUnit object.
     * @param unit The MyUnit object. It should belong to our player and not be dead.
     * @return The associated MyUnit object.
     */
    public static MyUnit getUnit(Unit unit) {
        if (!units.containsKey(unit.id())) {
            return makeUnit(unit);
        }
        else {
            return units.get(unit.id());
        }
    }

    /**
     * Gets the MyUnit object that corresponds to a unit id.
     * @param id The unit id. It should belong to our player and not be dead.
     * @return The associated MyUnit object.
     */
    public static MyUnit getUnit(int id) {
        MyUnit unit = units.get(id);
        if (unit != null) return unit;
        //MyUnit doesn't yet exist
        return makeUnit(id);
    }

    /**
     * Gets the MyUnit objects that correspond to a VecUnit object.
     * @param units The VecUnit object. All contained units should belong to our player and not be dead.
     * @return The associated list of MyUnit objects.
     */
    public static List<? extends MyUnit> getUnits(VecUnit units) {
        List<MyUnit> result = new ArrayList<>((int) units.size());
        for (int i=0; i<units.size(); i++) {
            Unit unit = units.get(i);
            result.add(getUnit(unit));
        }
        return result;
    }

    /**
     * Gets the MyUnit objects that correspond to a VecUnitID object.
     * @param units The VecUnitID object. All contained units should belong to our player and not be dead.
     * @return The associated list of MyUnit objects.
     */
    public static List<? extends MyUnit> getUnits(VecUnitID units) {
        int numUnits = (int) units.size();
        List<MyUnit> result = new ArrayList<>(numUnits);
        for (int i=0; i<numUnits; i++) {
            int id = units.get(i);
            result.add(getUnit(id));
        }
        return result;
    }

    /**
     * Gets the MyUnit objects that correspond to a Collection of units.
     * NOTE: `units` cannot be modified!
     * @param units The Collection of units. All contained units should belong to our player and not be dead.
     * @return The associated list of MyUnit objects.
     */
    public static List<MyUnit> getUnits(Collection<Unit> units) {
        List<MyUnit> result = new ArrayList<>(units.size());
        for (Unit unit : units) result.add(getUnit(unit));
        return result;
    }

    /** Gets the total number of friendly units */
    public static int getNumUnits() {
        return totalUnitCount;
    }

    /** Gets the number of friendly units of the given type */
    public static int getNumUnits(UnitType type) {
        return unitCounts[type.swigValue()];
    }

    public static void updateFactoryProductionQueue() {
        int round = (int) gc.round();
        while (!factoryProductionQueue.isEmpty()) {
            if (factoryProductionQueue.peek().getFirst() <= round) {
                UnitType type = factoryProductionQueue.poll().getSecond();
                factoryProductionQueueCounts.put(type, factoryProductionQueueCounts.get(type) - 1);
            }
            else {
                return;
            }
        }
    }

    public static int getNumQueuedUnits() {
        return factoryProductionQueue.size();
    }

    public static int getNumQueuedUnits(UnitType type) {
        Integer count = factoryProductionQueueCounts.get(type);
        return count != null ? count : 0;
    }

    public static void factoryProductionQueueAdd(UnitType type) {
        factoryProductionQueue.add(new Pair<Integer, UnitType>((int)gc.round() + FACTORY_PRODUCTION_ROUNDS, type));
        if (!factoryProductionQueueCounts.containsKey(type)) {
            factoryProductionQueueCounts.put(type, 1);
        }
        else {
            factoryProductionQueueCounts.put(type, factoryProductionQueueCounts.get(type) + 1);
        }
    }

    @Override
    public void selfDestruct() {
        gc.disintegrateUnit(id);
        informOfDeath();
    }

    @Override
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

    @Override
    public Pair<List<MyUnit>, List<Unit>> senseNearbyUnits(int radius) {
        return senseNearbyUnits(radius, null);
    }

    @Override
    public Pair<List<MyUnit>, List<Unit>> senseNearbyUnits(UnitType type) {
        return senseNearbyUnits(getVisionRange(), type);
    }

    @Override
    public Pair<List<MyUnit>, List<Unit>> senseNearbyUnits() {
        return senseNearbyUnits(getVisionRange(), null);
    }

    @Override
    public List<Unit> senseNearbyEnemies(int radius, UnitType type) {
        assert isOnMap();

        VecUnit vec = (type == null) ?
            gc.senseNearbyUnitsByTeam(getMapLocation(), radius, Utils.OTHER_TEAM) :
            gc.senseNearbyUnitsByType(getMapLocation(), radius, type);

        ArrayList<Unit> enemyUnits = new ArrayList<>((int) vec.size());
        for (int i=0; i<vec.size(); i++) {
            Unit unit = vec.get(i);
            if (unit.team() == Utils.OTHER_TEAM) enemyUnits.add(unit);
        }
        return enemyUnits;
    }

    @Override
    public List<Unit> senseNearbyEnemies(int radius) {
        return senseNearbyEnemies(radius, null);
    }

    @Override
    public List<Unit> senseNearbyEnemies(UnitType type) {
        return senseNearbyEnemies(getVisionRange(), type);
    }

    @Override
    public List<Unit> senseNearbyEnemies() {
        return senseNearbyEnemies(getVisionRange(), null);
    }

    @Override
    public List<MyUnit> senseNearbyFriendlies(int radius, UnitType type) {
        assert isOnMap();

        VecUnit vec = (type == null) ?
            gc.senseNearbyUnitsByTeam(getMapLocation(), radius, Utils.TEAM) :
            gc.senseNearbyUnitsByType(getMapLocation(), radius, type);

        ArrayList<MyUnit> myUnits = new ArrayList<>((int) vec.size());
        for (int i=0; i<vec.size(); i++) {
            Unit unit = vec.get(i);
            //Don't return ourselves
            if (unit.id() == this.id) continue;
            if (unit.team() == Utils.TEAM) myUnits.add(getUnit(unit));
        }
        return myUnits;
    }

    @Override
    public List<MyUnit> senseNearbyFriendlies(int radius) {
        return senseNearbyFriendlies(radius, null);
    }

    @Override
    public List<MyUnit> senseNearbyFriendlies(UnitType type) {
        return senseNearbyFriendlies(getVisionRange(), type);
    }

    @Override
    public List<MyUnit> senseNearbyFriendlies() {
        return senseNearbyFriendlies(getVisionRange(), null);
    }

    /** Gets the unit as a Unit object */
    public Unit getAsUnit() {
        return gc.unit(id);
        //TODO: cache this somehow
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public int getVisionRange() {
        return (int) getAsUnit().visionRange();
    }


    @Override
    public MapLocation getMapLocation() {
        assert isOnMap();
        return getLocation().mapLocation();
        //TODO: Cache this?
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isOnMap() {
        return getLocation().isOnMap();
    }

    @Override
    public boolean isInGarrison() {
        return getLocation().isInGarrison();
    }

    @Override
    public int getHealth() {
        return (int) getAsUnit().health();
    }

    @Override
    public boolean isGarrisonFull() {
        Unit unit = getAsUnit();
        return unit.structureGarrison().size() == unit.structureMaxCapacity();
    }

    @Override
    public ArrayList<Pair<MapLocation, Integer>> senseNearbyKarbonite(int radius) {
        assert radius <= getVisionRange();
        assert isOnMap();
        ArrayList<Pair<MapLocation, Integer>> nearbyKarbonite = new ArrayList<>();
        VecMapLocation locs = gc.allLocationsWithin(getMapLocation(), radius);

        for(int i = 0; i < locs.size(); i++) {
            MapLocation loc = locs.get(i);
            //if (!gc.canSenseLocation(loc)) {
            //    System.out.println("My location: " + getAsUnit().location());
            //    System.out.println("My location: " + getAsUnit().location().mapLocation());
            //    System.out.println("Vision range: " + getAsUnit().visionRange());
            //    System.out.println("Got: " + loc + " but it is distance " + getMapLocation().distanceSquaredTo(loc));
            //    System.out.println("Real distance: " + getAsUnit().location().mapLocation().distanceSquaredTo(loc));
            //    println("My team: " + gc.team());
            //    System.exit(0);
            //}
            // TODO: temporary(?) fix: it's possible for allLocationsWithin(loc, visionRange) to return
            // a location that we cannot sense. One potential case when this occurs is when we move
            // during a round. The visible locations are only updated by the game engine at the
            // beginning of a round, so we need this additional check. However, as of the moment,
            // this function is only called before the unit moves, so this cannot be the case.
            if (!gc.canSenseLocation(loc)) {
                continue;
            }
            int amount = (int) gc.karboniteAt(loc);
            if (amount > 0) {
                nearbyKarbonite.add(new Pair<>(loc, amount));
            }
        }
        return nearbyKarbonite;
    }

    @Override
    public ArrayList<Pair<MapLocation, Integer>> senseNearbyKarbonite() {
        return senseNearbyKarbonite(getVisionRange());
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; //short circuit
        if (!(obj instanceof AUnit)) return false;
        AUnit other = (AUnit) obj;
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

    /** Called whenever this unit dies. Use this to update any references you made to the unit. */
    protected abstract void onDeath();



    //////////END OF API//////////



    /**
     * Mapping from id to AUnit objects (for internal use only).
     * Must only contain alive units belonging to our player, i.e. on our planet under our team.
     * NOTE: Do not attempt to iterate through this map unless you're sure you won't modify it.
     */
    public static final int FACTORY_PRODUCTION_ROUNDS = 5; // How many rounds it takes to produce a robot
    private static final Map<Integer, AUnit> units = new HashMap<>();
    private static final List<AUnit> unitList = new ArrayList<>();

    private final int id;
    private final Team team;
    private final int maxHealth;

    private Location location;
    private boolean isDead;
    private static UnitBuilder builder;

    private static final int[] unitCounts = new int[UnitType.values().length];
    private static int totalUnitCount;
    private static final Queue<Pair<Integer, UnitType>> factoryProductionQueue = new LinkedList<>();
    private static final HashMap<UnitType, Integer> factoryProductionQueueCounts = new HashMap<>();

    /**
     * Constructor for AUnit.
     * @exception RuntimeException When unit already exists, has unknown type, doesn't belong to our player, or is dead.
     */
    AUnit(Unit unit) throws RuntimeException{
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
        AUnit previousValue = units.put(id, this);
        if (previousValue != null) {
            units.put(id, previousValue); //restore the value
            throw new RuntimeException("The unit " + unit + " already exists!");
        }
        unitList.add(this);
        unitCounts[unit.unitType().swigValue()]++;
        totalUnitCount++;
    }

    /**
     * Constructs an AUnit object based off of a Unit and adds it to the collections of units.
     * The unit must belong to our Player, i.e. on our Planet under our Team.
     * The unit must be alive.
     * The unit must not already have been created.
     * @param ourUnit The Unit object to make the AUnit object from.
     * @exception RuntimeException When the unit has an unknown type.
     */
    static AUnit makeUnit(Unit ourUnit) {
        return builder.newUnit(ourUnit);
    }

    /**
     * Constructs an AUnit object based off of a unit's id and adds it to the collections of units.
     * The unit must belong to our Player, i.e. on our Planet under our Team.
     * The unit must be alive.
     * The unit must not already have been created.
     * @param ourUnit The id of the unit to make the AUnit object from.
     * @exception RuntimeException When the unit has an unknown type.
     */
    static AUnit makeUnit(int ourUnit) {
        assert gc.canSenseUnit(ourUnit);
        return makeUnit(gc.unit(ourUnit));
    }

    /**
     * Updates `isDead`, removes this unit from `units`, and calls `onDeath()`.
     * NOTE: Does not remove the unit from unitList.
     * NOTE: The unit must actually be dead.
     */
    void informOfDeath() {
        UnitType type = getType();
        isDead = true;
        units.remove(getID());
        unitCounts[type.swigValue()]--;
        totalUnitCount--;
        onDeath();
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
