package org.battlecode.bc18.api;

import bc.*;
import org.battlecode.bc18.util.Utils;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class ARobot extends AUnit implements MyRobot {

    @Override
    public boolean isAccessible(Direction dir) {
        return gc.canMove(getID(), dir);
    }

    @Override
    public boolean isMoveReady() {
        return gc.isMoveReady(getID());
    }

    @Override
    public boolean canMove(Direction dir) {
        return isMoveReady() && isAccessible(dir);
    }

    @Override
    public MapLocation move(Direction dir) {
        assert canMove(dir);
        if (dir == Direction.Center) return getMapLocation();
        //if (getMapLocation().getX() == 9 && getMapLocation().getY() == 12) {
        //    println("BROKEN: moving to " + dir);
        //}
        gc.moveRobot(getID(), dir);
        MapLocation newLoc = getMapLocation().add(dir);
        setLocation(newLoc);
        return newLoc;
    }

    @Override
    public MapLocation fuzzyMove(Direction dir) {
        assert isMoveReady();
        Direction moveDir = dir;
        boolean canMove = false;
        //if (getMapLocation().getX() == 9 && getMapLocation().getY() == 12) {
        //    println("BROKEN: moving to " + dir);
        //}
        if (isAccessible(moveDir)) {
            canMove = true;
        }
        if (!canMove && isAccessible(Utils.rotateDirClockwise(moveDir))) {
            moveDir = Utils.rotateDirClockwise(moveDir);
            canMove = true;
        }
        if (!canMove && isAccessible(Utils.rotateDirCounterClockwise(moveDir))) {
            moveDir = Utils.rotateDirCounterClockwise(moveDir);
            canMove = true;
        }
        if (canMove) {
            return move(moveDir);
        }
        return getMapLocation();
    }

    @Override
    public MapLocation movePerpendicular(Direction dir) {
        assert isMoveReady();
        Direction perpendicular1 = Utils.rotateDirClockwise(Utils.rotateDirClockwise(dir));
        Direction perpendicular2 = Utils.rotateDirCounterClockwise(Utils.rotateDirCounterClockwise(dir));
        Direction moveDir = dir;
        //if (getMapLocation().getX() == 9 && getMapLocation().getY() == 12) {
        //    println("BROKEN: moving to " + dir);
        //}
        boolean canMove = false;
        if (isAccessible(perpendicular1)) {
            moveDir = perpendicular1;
            canMove = true;
        }
        if (!canMove && isAccessible(perpendicular2)) {
            moveDir = perpendicular2;
            canMove = true;
        }
        if (canMove) {
            return move(moveDir);
        }
        return getMapLocation();
    }

    @Override
    public int getAbilityHeat() {
        return (int) getAsUnit().abilityHeat();
    }

    @Override
    public int getAbilityCooldown() {
        if (abilityCooldown == -1) abilityCooldown = (int) getAsUnit().abilityCooldown();
        return abilityCooldown;
    }

    @Override
    public boolean isAbilityUnlocked() {
        return Utils.toBool(getAsUnit().isAbilityUnlocked());
    }

    @Override
    public int getAbililtyRange() {
        if (abilityRange == -1) {
            long tmp = getAsUnit().abilityRange();
            if (tmp > Integer.MAX_VALUE) tmp = Integer.MAX_VALUE; //prevent overflow from snipe ability
            abilityRange = (int) tmp;
        }
        return abilityRange;
    }



    //////////END OF API//////////



    //We don't know these values yet
    private int abilityCooldown = -1;
    private int abilityRange = -1;
    
    /**
     * Constructor for ARobot.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    ARobot(Unit unit) {
        super(unit);
    }
}
