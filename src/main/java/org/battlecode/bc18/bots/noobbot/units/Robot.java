package org.battlecode.bc18.bots.noobbot.units;

import bc.*;

import static org.battlecode.bc18.Utils.gc;

public abstract class Robot extends MyUnit {

    /**
     * Constructor for MyUnit.
     * @exception RuntimeException Occurs when a unit with that id already exists.
     */
    protected Robot(Unit unit) {
        super(unit);
    }

    public boolean attack(int targetID) {
        if (gc.isAttackReady(this.id) &&
                gc.canAttack(this.id, targetID)) {
            println("Attacking");
            gc.attack(this.id, targetID);
            return true;
        }
        return false;
    }
    
    public boolean move(Direction dir) {
        if (gc.isMoveReady(this.id) &&
                gc.canMove(this.id, dir)) {
            println("Moving");
            gc.moveRobot(this.id, dir);
            return true;
        }
        return false;
    }
}
