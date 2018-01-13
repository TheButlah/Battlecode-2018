package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

import static org.battlecode.bc18.Utils.gc;

public class Healer extends Robot {

    public static final UnitType TYPE = UnitType.Healer;

    /**
     * Constructor for Healer.
     * @exception RuntimeException Occurs when a unit with that id already exists.
     */
    Healer(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Healer;
    }

    /**
     * Heal the target robot.
     * @param targetID target robot id
     * @return true if healing was successful, false otherwise
     */
    public boolean heal(int targetID) {
        if (gc.isHealReady(this.id) &&
                gc.canHeal(this.id, targetID)) {
            gc.heal(this.id, targetID);
            return true;
        }
        return false;
    }
    
    /**
     * Overcharges the robot, resetting the robot's cooldowns.
     * The robot must be on the same team as you.
     * @param targetID target robot id
     * @return true if overcharging was successful, false otherwise
     */
    public boolean overcharge(int targetID) {
        if (gc.isOverchargeReady(this.id) &&
                gc.canOvercharge(this.id, targetID)) {
            gc.overcharge(this.id, targetID);
            return true;
        }
        return false;
    }

    @Override
    public void act() {
        //TODO: Implement this
    }

    @Override
    public UnitType getType() {
        return Healer.TYPE;
    }
}
