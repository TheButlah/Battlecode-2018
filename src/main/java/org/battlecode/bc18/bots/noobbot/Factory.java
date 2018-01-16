package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.api.AFactory;
import org.battlecode.bc18.api.MyUnit;
import org.battlecode.bc18.util.Utils;

import java.util.List;

public class Factory extends AFactory {
    //static int time1, time2, time3;
    //static long startTime;

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
        //startTime = System.currentTimeMillis();
        List<MyUnit> nearbyWorkers = senseNearbyFriendlies(UnitType.Worker);
        boolean hasNearbyWorker = (nearbyWorkers.size() >=1);
        //time1 += System.currentTimeMillis() - startTime;
        //System.out.println("time1: " + time1);

        //startTime = System.currentTimeMillis();
        if (!hasNearbyWorker && canProduceRobot(UnitType.Worker)) {
            produceRobot(UnitType.Worker);
        } else if (canProduceRobot(UnitType.Knight)) {
            produceRobot(UnitType.Knight);
        }
        //time2 += System.currentTimeMillis() - startTime;
        //System.out.println("time2: " + time2);
        //startTime = System.currentTimeMillis();
        // Unload units
        if (getGarrison().size() != 0) {
            for (Direction dir : Utils.dirs) {
                if (canUnload(dir)) {
                    unload(dir);
                    break;
                }
            }
        }
        //time3 += System.currentTimeMillis() - startTime;
        //System.out.println("time3: " + time3);
    }

    @Override
    protected void onDeath() {
        Worker.workersPerStructure.remove(getID());
    }
}
