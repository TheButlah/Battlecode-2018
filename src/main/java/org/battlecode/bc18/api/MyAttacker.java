package org.battlecode.bc18.api;

import bc.Unit;
import static org.battlecode.bc18.util.Utils.gc;

public interface MyAttacker extends MyRobot {

    /** Gets the attack range of this robot. */
    int getAttackRange();

    /** Whether the target is within attack range. */
    default boolean isWithinAttackRange(Unit target) {
        return gc.canAttack(getID(), target.id());
    }

    /** Whether the robot is ready to attack. */
    default boolean isAttackReady() {
        return gc.isAttackReady(getID());
    }

    /** Whether we can attack the target */
    default boolean canAttack(Unit target) {
        return isAttackReady() && isWithinAttackRange(target);
    }

    /**
     * Attack the target robot.
     * NOTE: Does not check to see if it can first.
     * @param target The robot to attack.
     */
    default void attack(Unit target) {
        assert canAttack(target);
        gc.attack(getID(), target.id());
    }

}
