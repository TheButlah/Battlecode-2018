package org.battlecode.bc18.api;

import bc.*;
import static org.battlecode.bc18.util.Utils.gc;

public abstract class ARanger extends ARobot implements MyRanger {

    @Override
    public int getCannotAttackRange() {
        return cannotAttackRange;
    }

    @Override
    public int getAttackRange() {
        return (int) getAsUnit().attackRange();
        //TODO: Use gc.researchInfo() to do this instead
    }

    @Override
    public int getAttackRingSize(){
        return this.getAttackRange() - this.getCannotAttackRange();
    }

    @Override
    public boolean canAct() {
        return this.getAsUnit().rangerIsSniping() == 0;
    }

    @Override
    public boolean isSnipeReady() {
        return gc.isBeginSnipeReady(this.getID());
    }


    /**
     * Determines if the ranger can snipe a unit, taking in to account
     * if the ability is unlocked, ability heat, and whatever their beginsnipe does :3
     * @param target The unit to attack
     * @return True if can snipe, else false
     */
    @Override
    public boolean canSnipe(Unit target) {
        return isAbilityUnlocked() && this.isSnipeReady() && gc.canBeginSnipe(this.getID(), target.location().mapLocation());
    }

    /**
     * checks if the ranger can snipe the target, if so, begins the countdown.
     * @param target The target unit to snipe
     * @return true if countdown started, else false
     */
    @Override
    public boolean snipe(Unit target) {
        if (canSnipe(target)) {
            gc.beginSnipe(this.getID(), target.location().mapLocation());
            return true;
        }
        return false;
    }

    @Override
    public boolean isTargetKindaClose(Unit target) {
        return target.location().mapLocation().distanceSquaredTo(this.getMapLocation()) < this.getCannotAttackRange() + (0.25*this.getAttackRingSize());
    }

    @Override
    public boolean isTargetKindaFar(Unit target) {
        return target.location().mapLocation().distanceSquaredTo(this.getMapLocation()) > this.getAttackRange() - (0.25*this.getAttackRingSize());

    }

    @Override
    public boolean isTargetInSweetSpot(Unit target) {
        return !isTargetKindaClose(target) && !isTargetKindaFar(target);
    }



    //////////END OF API//////////



    private final int cannotAttackRange;

    /**
     * Constructor for ARanger.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected ARanger(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Ranger;
        cannotAttackRange = (int) getAsUnit().rangerCannotAttackRange();
    }
}
