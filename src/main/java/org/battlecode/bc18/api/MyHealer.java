package org.battlecode.bc18.api;

import bc.UnitType;

public interface MyHealer extends MyRobot {
    UnitType TYPE = UnitType.Healer;

    @Override
    default UnitType getType() {
        return TYPE;
    }

    /** Gets the healing range of this healer. */
    int getHealRange();

    /** Whether the target is within healing range. */
    boolean isWithinHealRange(MyRobot target);

    /** Whether the healer is ready to heal. */
    boolean isHealReady();

    /** Whether we can heal the target */
    boolean canHeal(MyRobot target);

    /**
     * Heal the target robot.
     * NOTE: Does not check if it can first.
     * @param target The robot to heal.
     */
    void heal(MyRobot target);

    /**
     * Whether overcharge ability is ready.
     * NOTE: Checks both heat and unlock status.
     */
    boolean isOverchargeReady();

    /** Whether the target unit is within overcharge range. */
    boolean isWithinOverchargeRange(MyRobot target);

    /**
     * Whether the unit can overcharge the target.
     * Checks heat, distance, and unlock status.
     */
    boolean canOvercharge(MyRobot target);

    /**
     * Overcharges the target, resetting its cooldowns.
     * Note: Does not check to see if it can first.
     */
    void overcharge(MyRobot target);
}
