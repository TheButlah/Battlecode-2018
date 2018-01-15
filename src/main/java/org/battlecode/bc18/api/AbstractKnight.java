package org.battlecode.bc18.api;

import bc.*;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class AbstractKnight extends ARobot {

    public static final UnitType TYPE = UnitType.Knight;

    @Override
    public UnitType getType() {
        return AbstractKnight.TYPE;
    }

    /**
     * Whether the javelin is ready to use.
     * NOTE: Checks both heat and unlock status.
     */
    public boolean isJavelinReady() {
        return isAbilityUnlocked() && gc.isJavelinReady(getID());
    }

    /** Whether the target is within javelin range */
    public boolean isWithinJavelinRange(Unit target) {
        return gc.canJavelin(getID(), target.id());
    }

    /**
     * Whether we can javelin the target.
     * Checks heat, distance, and unlock status.
     */
    public boolean canJavelin(Unit target) {
        return isJavelinReady() && isWithinJavelinRange(target);
    }

    /**
     * Javelins the target, dealing the knight's standard damage.
     * NOTE: Does not check to see if we can first.
     * @param target The target unit.
     */
    public void javelin(Unit target) {
        assert canJavelin(target);
        gc.javelin(getID(), target.id());
    }



    //////////END OF API//////////



    /**
     * Constructor for AbstractKnight.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AbstractKnight(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Knight;
    }
}
