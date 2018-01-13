package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

import static org.battlecode.bc18.bots.util.Utils.gc;

public class Healer extends Robot {

    public static final UnitType TYPE = UnitType.Healer;

    /**
     * Constructor for Healer.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
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
        if (gc.isHealReady(getID()) &&
                gc.canHeal(getID(), targetID)) {
            gc.heal(getID(), targetID);
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
        if (gc.isOverchargeReady(getID()) &&
                gc.canOvercharge(getID(), targetID)) {
            gc.overcharge(getID(), targetID);
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
