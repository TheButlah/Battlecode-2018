package org.battlecode.bc18.bots.noobbot;

import bc.Direction;
import bc.Unit;
import org.battlecode.bc18.api.AbstractHealer;
import org.battlecode.bc18.api.AbstractUnit;
import org.battlecode.bc18.util.Utils;

public class Healer extends AbstractHealer {

    /**
     * Constructor for Healer.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Healer(Unit unit) {
        super(unit);
    }

    @Override
    public void act() {
        // heals weakest ally in heal range, if possible. Else, moves randomly.

        // gets the weakest ally within heal range
        AbstractUnit target = null;
        int healthLeft = Integer.MAX_VALUE;
        for (AbstractUnit u : senseNearbyFriendlies(this.getAttackRange())) {
            if (u.getHealth() < u.getMaxHealth() && u.getHealth() < healthLeft) {
                target = u;
                healthLeft = u.getHealth();
            }
        }

        if (target != null) {
            heal(target.getID());
        }
        else {
            if (isMoveReady()) {
                //Move randomly
                int offset = Utils.rand.nextInt(Utils.dirs.length);
                for (int i = 0; i < Utils.dirs.length; i++) {
                    Direction dir = Utils.dirs[(i + offset) % Utils.dirs.length]; //Cycle through based on random offset
                    //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                    if (isAccessible(dir)) {
                        move(dir);
                        return;
                    }
                }
            }
            // can't move and no target so return
            return;
        }


    }

    @Override
    protected void onDeath() {

    }
}
