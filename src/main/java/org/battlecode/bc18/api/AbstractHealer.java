package org.battlecode.bc18.api;

import bc.*;

import static org.battlecode.bc18.bots.util.Utils.gc;

public class AbstractHealer extends AbstractRobot {

    public static final UnitType TYPE = UnitType.Healer;

    /**
     * Constructor for AbstractHealer.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    AbstractHealer(Unit unit) {
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
        if (isDead()) return;
        //TODO: Implement this
    }

    @Override
    public UnitType getType() {
        return AbstractHealer.TYPE;
    }
}
