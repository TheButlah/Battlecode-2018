package org.battlecode.bc18.bots.noobbot;

import bc.Unit;
import bc.UnitType;

/**
 * Superclass for all the different types of bots.
 */
public abstract class Bot {

    public final int ID;
    public final UnitType TYPE;

    public Bot(Unit u) {
        this.ID = u.id();
        this.TYPE = u.unitType();
    }

    /**
     * Tells the bot to perform its action for this turn.
     */
    public abstract void act();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; //short circuit
        if (!(obj instanceof Bot)) return false;
        Bot other = (Bot) obj;
        return this.ID == other.ID;
    }

    @Override
    public int hashCode() {
        return ID;
    }

    @Override
    public String toString() {
        return "[" + TYPE + ":" + ID + "]";
    }

    /**
     * Prints to stdout the contents of `obj` prefixed by the bot info.
     * @param obj The data to print out.
     */
    protected void println(Object obj) {
        System.out.println(this + " " + obj);
    }

}
