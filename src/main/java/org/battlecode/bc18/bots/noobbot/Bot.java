package org.battlecode.bc18.bots.noobbot;

import bc.*;
import static org.battlecode.bc18.Utils.gc;

/**
 * Superclass for all the different types of bots.
 */
public abstract class Bot {

    protected final int id;

    public Bot(int id) {
        this.id = id;
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
     * Prints to stdout the contents of `obj` prefixed by the bot info.
     * @param obj The data to print out.
     */
    protected void println(Object obj) {
        System.out.println(this + " " + obj);
    }

    public abstract UnitType getType();

    public Unit getAsUnit() {
        return gc.unit(id);
        //TODO: cache this somehow
    }

}
