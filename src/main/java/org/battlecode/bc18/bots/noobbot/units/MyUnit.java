package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.battlecode.bc18.Utils.gc;

/** Superclass for the different types of units. */
public abstract class MyUnit {

    /** Tells the unit to perform its action for this turn */
    public abstract void act();

    /** Gets the type of unit */
    public abstract UnitType getType(); //This would be static but you cant have static abstract class

    /** Kaboom. */
    public void selfDestruct() {
        gc.disintegrateUnit(id);
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

    /** Gets the unit's current map location. Null if not on map. */
    public MapLocation getMapLocation() {
        return getLocation().mapLocation(); //This returns null if its not on map (I think).
        //TODO: Cache this?
    }

    /** Gets the unit's current location. */
    public Location getLocation() {
        return getAsUnit().location();
        //TODO: Cache this?
    }

    /** Whether the unit is on the map. */
    public boolean isOnMap() {
        return getLocation().isOnMap();
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



    /** Mapping from id to MyUnit objects (for internal use only) */
    protected static final Map<Integer, MyUnit> unitsModifiable = new HashMap<>();
    /** Unmodifiable mapping from id to MyUnit objects (safe for external use) */
    public static final Map<Integer, MyUnit> units = Collections.unmodifiableMap(unitsModifiable);

    protected final int id;
    protected final Team team;
    protected final int maxHealth;

    //Static initializer to ensure that right from the start, MyUnit knows all the units.
    static {
        VecUnit vec = gc.myUnits();
        for (int i=0; i<vec.size(); i++) {
            makeUnit(vec.get(i));
        }
    }

    /**
     * Constructor for MyUnit.
     * @exception RuntimeException Occurs when a unit with that id already exists.
     */
    protected MyUnit(Unit unit) throws RuntimeException{
        this.id = unit.id();
        this.team = unit.team();
        this.maxHealth = (int) unit.maxHealth();
        if (unitsModifiable.put(id, this) != null) throw new RuntimeException("The unit already exists!");
    }

    /**
     * Constructs a MyUnit object based off of a Unit.
     * @exception RuntimeException Occurs when `unit.unitType()` is not found or when the unit already exists.
     */
    protected static MyUnit makeUnit(Unit unit) {
        int id = unit.id();
        if (units.containsKey(id)) throw new RuntimeException("The unit already exists!");
        UnitType type = unit.unitType();
        switch(type) {
            case Worker:
                return new Worker(unit);
            case Knight:
                return new Knight(unit);
            case Ranger:
                return new Ranger(unit);
            case Mage:
                return new Mage(unit);
            case Healer:
                return new Healer(unit);
            case Factory:
                return new Factory(unit);
            case Rocket:
                return new Rocket(unit);
            default:
                throw new RuntimeException("Unrecognized UnitType!"); //Should never happen
        }
    }

    /**
     * Prints to stdout the contents of `obj` prefixed by the unit info.
     * @param obj The data to print out.
     */
    protected void println(Object obj) {
        System.out.println(this + " " + obj);
    }


}
