package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.api.AFactory;
import org.battlecode.bc18.api.MyUnit;
import org.battlecode.bc18.util.Utils;

import java.util.List;

public class Factory extends AFactory {

    /**
     * Constructor for Factory.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Factory(Unit unit) {
        super(unit);
    }

    @Override
    public void act() {
        //System.out.println("Workers assigned to me: " + workersPerFactory.get(getID()));
        // Since we cant maintain the invariant for the units HashMap, manually add in units to ensure invariant.

        // make and place knights until you can't :D
        List<MyUnit> nearbyWorkers = senseNearbyFriendlies(UnitType.Worker);
        boolean hasNearbyWorker = (nearbyWorkers.size() >=1);

        if (!hasNearbyWorker && canProduceRobot(UnitType.Worker)) {
            produceRobot(UnitType.Worker);
        } else if (canProduceRobot(UnitType.Knight)) {
            produceRobot(UnitType.Knight);
        }
        // Unload units
        for (Direction dir : Utils.dirs) {
            if (canUnload(dir)) unload(dir);
            //No break here because I think you can unload multiple units?
        }
    }

    @Override
    protected void onDeath() {
        //TODO: Do this
    }
}
