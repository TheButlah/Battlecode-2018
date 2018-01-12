package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

import java.util.HashMap;

import static org.battlecode.bc18.Utils.gc;

/**
 * Superclass for all the different types of units.
 */
public abstract class MyUnit {

    /** Tells the unit to perform its action for this turn */
    public abstract void act();

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

    /**
     * Prints to stdout the contents of `obj` prefixed by the unit info.
     * @param obj The data to print out.
     */
    protected void println(Object obj) {
        System.out.println(this + " " + obj);
    }

    /** Gets the type of unit */
    public abstract UnitType getType();

    /** Gets the unit as a Unit object */
    public Unit getAsUnit() {
        return gc.unit(id);
        //TODO: cache this somehow
    }

    /**
     * Access the robot's current map location.
     * @return current map location
     */
    public MapLocation getMyMapLocation() {
        Unit myUnit = getAsUnit();
        Location myLoc = myUnit.location();
        // TODO: handle the case in which !myLoc.isOnMap()
        return myLoc.mapLocation();
    }

    /////END OF API/////
    /** Mapping from id to MyUnit objects */
    static final HashMap<Integer, MyUnit> bots = new HashMap<>();

    protected final int id;

    MyUnit(int id) {
        this.id = id;
    }


}
