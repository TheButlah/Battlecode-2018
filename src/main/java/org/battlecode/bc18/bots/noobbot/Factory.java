package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.Utils;

import static org.battlecode.bc18.Utils.gc;

public class Factory extends Bot {

    public static final UnitType TYPE = UnitType.Factory;

    public Factory(int id) {
        super(id);
    }

    @Override
    public void act() {
        // make and place knights until you can't :D
        Unit myUnit = this.getAsUnit();
        MapLocation myMapLoc = myUnit.location().mapLocation();
        VecUnit nearbyWorkers = gc.senseNearbyUnitsByType(myMapLoc, myUnit.visionRange(), UnitType.Worker);
        Team myTeam = myUnit.team();
        boolean hasNearbyWorker = false;
        for (int i = 0; i < nearbyWorkers.size(); ++i) {
            if (nearbyWorkers.get(i).team() == myTeam) {
                hasNearbyWorker = true;
                break;
            }
        }
        if (!hasNearbyWorker && gc.canProduceRobot(this.id, UnitType.Worker)) {
            gc.produceRobot(this.id, UnitType.Worker);
        }
        else if (gc.canProduceRobot(this.id, UnitType.Knight)) {
            gc.produceRobot(this.id, UnitType.Knight);
        }
        // Unload units
        for (Direction dir : Utils.dirs) {
            // if there are no more units to unload, break
            if (myUnit.structureGarrison().size() == 0) break;
            if (gc.canUnload(this.id, dir)) {
                gc.unload(this.id, dir);
                // no break here so it can unload multiple (I think that's allowed)
            }
        }
    }

    @Override
    public UnitType getType() {
        return Factory.TYPE;
    }
}
