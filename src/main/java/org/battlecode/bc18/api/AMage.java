package org.battlecode.bc18.api;

import bc.*;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class AMage extends ARobot implements MyMage {

    @Override
    public boolean isAcessibleBlink(MapLocation loc) {
        return gc.canBlink(getID(), loc);
    }

    @Override
    public boolean isBlinkReady() {
        return isAbilityUnlocked() && gc.isBlinkReady(getID());
    }

    @Override
    public boolean canBlink(MapLocation loc) {
        return isAcessibleBlink(loc) && isBlinkReady();
    }

    @Override
    public void blink(MapLocation loc) {
        assert canBlink(loc);
        gc.blink(getID(), loc);
    }

    @Override
    public int getAttackRange() {
        return attackRange;
    }



    //////////END OF API//////////



    private final int attackRange;

    /**
     * Constructor for AMage.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AMage(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Mage;
        this.attackRange = (int) getAsUnit().attackRange();
    }
}
