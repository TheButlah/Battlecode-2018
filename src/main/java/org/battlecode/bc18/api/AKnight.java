package org.battlecode.bc18.api;

import static org.battlecode.bc18.util.Utils.gc;

import org.battlecode.bc18.TargetManager;
import org.battlecode.bc18.util.Utils;

import bc.Planet;
import bc.PlanetMap;
import bc.Unit;
import bc.UnitType;

public abstract class AKnight extends ARobot implements MyKnight {

    @Override
    public boolean isJavelinReady() {
        return isAbilityUnlocked() && gc.isJavelinReady(getID());
    }

    @Override
    public boolean isWithinJavelinRange(Unit target) {
        return gc.canJavelin(getID(), target.id());
    }

    @Override
    public boolean canJavelin(Unit target) {
        return isJavelinReady() && isWithinJavelinRange(target);
    }

    @Override
    public void javelin(Unit target) {
        assert canJavelin(target);
        gc.javelin(getID(), target.id());
    }

    @Override
    public int getAttackRange() {
        return attackRange;
    }



    //////////END OF API//////////



    private final int attackRange;

    /**
     * Constructor for AKnight.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AKnight(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Knight;
        this.attackRange = (int) getAsUnit().attackRange();
    }
}
