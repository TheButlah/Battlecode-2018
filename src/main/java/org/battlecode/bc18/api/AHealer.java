package org.battlecode.bc18.api;

import bc.*;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class AHealer extends ARobot implements MyHealer {

    @Override
    public int getHealRange() {
        if (healRange == -1) healRange = (int) getAsUnit().attackRange();
        return healRange;
    }

    @Override
    public boolean isWithinHealRange(MyRobot target) {
        return gc.canHeal(getID(), target.getID());
    }

    @Override
    public boolean isHealReady() {
        return gc.isHealReady(getID());
    }

    @Override
    public boolean canHeal(MyRobot target) {
        return isHealReady() && isWithinHealRange(target);
    }

    @Override
    public void heal(MyRobot target) {
        assert canHeal(target);
        gc.heal(getID(), target.getID());
    }

    @Override
    public boolean isOverchargeReady() {
        return isAbilityUnlocked() && gc.isOverchargeReady(getID());
    }

    @Override
    public boolean isWithinOverchargeRange(MyRobot target) {
        return gc.canOvercharge(getID(), target.getID());
    }

    @Override
    public boolean canOvercharge(MyRobot target) {
        assert isAbilityUnlocked();
        return isOverchargeReady() && isWithinOverchargeRange(target);
    }
    
    @Override
    public void overcharge(MyRobot target) {
        assert canOvercharge(target);
        gc.overcharge(getID(), target.getID());
    }



    //////////END OF API//////////



    private static int healRange = -1;

    /**
     * Constructor for AHealer.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AHealer(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Healer;
    }
}
