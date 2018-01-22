package org.battlecode.bc18.bots.noobbot;

import org.battlecode.bc18.ProductionManager;
import org.battlecode.bc18.api.AFactory;
import org.battlecode.bc18.util.Utils;

import bc.Direction;
import bc.Unit;
import bc.UnitType;

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

        //startTime = System.currentTimeMillis();
        //time1 += System.currentTimeMillis() - startTime;
        //System.out.println("time1: " + time1);

        //startTime = System.currentTimeMillis();
        UnitType nextDesiredProduction = ProductionManager.getNextProductionType();
        if (getHealth() < getMaxHealth() && canProduceRobot(UnitType.Worker) && senseNearbyFriendlies(UnitType.Worker).size() == 0) {
            produceRobot(UnitType.Worker);
        }
        else if (nextDesiredProduction == UnitType.Worker && canProduceRobot(UnitType.Worker)) {
            produceRobot(UnitType.Worker);
        }
        else if (nextDesiredProduction == UnitType.Knight && canProduceRobot(UnitType.Knight)) {
            produceRobot(UnitType.Knight);
        }
        else if (nextDesiredProduction == UnitType.Ranger && canProduceRobot(UnitType.Ranger)) {
            produceRobot(UnitType.Ranger);
        }
        else if (nextDesiredProduction == UnitType.Mage && canProduceRobot(UnitType.Mage)) {
            produceRobot(UnitType.Mage);
        }
        else if (nextDesiredProduction == UnitType.Healer && canProduceRobot(UnitType.Healer)) {
            produceRobot(UnitType.Healer);
        }
        //time2 += System.currentTimeMillis() - startTime;
        //System.out.println("time2: " + time2);
        //startTime = System.currentTimeMillis();
        // Unload units
        if (getAsUnit().structureGarrison().size() != 0) {
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
