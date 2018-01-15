package org.battlecode.bc18.api;

import bc.*;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class AbstractHealer extends ARobot {

    public static final UnitType TYPE = UnitType.Healer;

    @Override
    public UnitType getType() {
        return AbstractHealer.TYPE;
    }

    /** Gets the healing range of this healer. */
    public int getHealRange() {
        if (healRange == -1) healRange = (int) getAsUnit().attackRange();
        return healRange;
    }

    /** Whether the target is within healing range. */
    public boolean isWithinHealRange(MyRobot target) {
        return gc.canHeal(getID(), target.getID());
    }

    /** Whether the healer is ready to heal. */
    public boolean isHealReady() {
        return gc.isHealReady(getID());
    }

    /** Whether we can heal the target */
    public boolean canHeal(MyRobot target) {
        return isHealReady() && isWithinHealRange(target);
    }

    /**
     * Heal the target robot.
     * NOTE: Does not check if it can first.
     * @param target The robot to heal.
     */
    public void heal(MyRobot target) {
        assert canHeal(target);
        gc.heal(getID(), target.getID());
    }

    /**
     * Whether overcharge ability is ready.
     * NOTE: Checks both heat and unlock status.
     */
    public boolean isOverchargeReady() {
        return isAbilityUnlocked() && gc.isOverchargeReady(getID());
    }

    /** Whether the target unit is within overcharge range. */
    public boolean isWithinOverchargeRange(MyRobot target) {
        return gc.canOvercharge(getID(), target.getID());
    }

    /**
     * Whether the unit can overcharge the target.
     * Checks heat, distance, and unlock status.
     */
    public boolean canOvercharge(MyRobot target) {
        assert isAbilityUnlocked();
        return isOverchargeReady() && isWithinOverchargeRange(target);
    }
    
    /**
     * Overcharges the target, resetting its cooldowns.
     * Note: Does not check to see if it can first.
     */
    public void overcharge(MyRobot target) {
        assert canOvercharge(target);
        gc.overcharge(getID(), target.getID());
    }



    //////////END OF API//////////



    private static int healRange = -1;

    /**
     * Constructor for AbstractHealer.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AbstractHealer(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Healer;
    }
}
