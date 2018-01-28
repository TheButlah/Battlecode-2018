package org.battlecode.bc18.bots.noobbot;

import bc.MapLocation;
import org.battlecode.bc18.ProductionManager;
import org.battlecode.bc18.api.AFactory;
import org.battlecode.bc18.api.AUnit;
import org.battlecode.bc18.api.MyRobot;
import org.battlecode.bc18.api.MyUnit;
import org.battlecode.bc18.util.Utils;

import bc.Direction;
import bc.Unit;
import bc.UnitType;

import java.util.HashMap;
import java.util.List;

public class Factory extends AFactory {
    //static int time1, time2, time3, time4, time5;
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
        MapLocation myLoc = getMapLocation();
        //System.out.println("Workers assigned to me: " + workersPerFactory.get(getID()));
        // Since we cant maintain the invariant for the units HashMap, manually add in units to ensure invariant.

        //startTime = System.currentTimeMillis();
        //time1 += System.currentTimeMillis() - startTime;
        //System.out.println("time1: " + time1);

        //startTime = System.currentTimeMillis();
        UnitType nextDesiredProduction = ProductionManager.getNextProductionType();
        if (getHealth() < getMaxHealth() && canProduceRobot(UnitType.Worker) && fastSenseNearbyFriendlies(UnitType.Worker).size() == 0) {
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
        List<MyRobot> garrison = getGarrison();
        //time3 += System.currentTimeMillis() - startTime;
        //System.out.println("time3: " + time3);
        if (garrison.size() != 0) {
            //startTime = System.currentTimeMillis();
            boolean hasUnloaded = false;
            for (Direction dir : Utils.dirs) {
                if (canUnload(dir)) {
                    unload(dir);
                    hasUnloaded = true;
                    break;
                }
            }
            //time4 += System.currentTimeMillis() - startTime;
            //System.out.println("time4: " + time4);
            // Self destruct adjacent unit if blocked
            if (!hasUnloaded) {
                //startTime = System.currentTimeMillis();
                int totalCount = 0;
                HashMap<UnitType, Integer> unitCounts = new HashMap<>();
                for (Direction dir : Utils.dirs) {
                    if (dir == Direction.Center) continue;
                    MapLocation loc = myLoc.add(dir);
                    //Skip dirs that dont have units
                    if (!Utils.gc.hasUnitAtLocation(loc)) continue;
                    Unit unit = Utils.gc.senseUnitAtLocation(loc);
                    //Only our units
                    if (unit.team() != Utils.TEAM) continue;
                    MyUnit myUnit = AUnit.getUnit(unit);
                    UnitType type = myUnit.getType();
                    int count = unitCounts.computeIfAbsent(type, (K) -> 0);
                    unitCounts.put(type, count++);
                    totalCount++;
                }
                //Decide which unit to destroy
                UnitType selectedType = null;
                int workers = unitCounts.getOrDefault(UnitType.Worker, 0);
                int healers = unitCounts.getOrDefault(UnitType.Healer, 0);
                int knights = unitCounts.getOrDefault(UnitType.Knight, 0);

                if (workers > 1) {
                    selectedType = UnitType.Worker;
                } else if (healers > 0) {
                    selectedType = UnitType.Healer;
                } else if (knights > 0) {
                    selectedType = UnitType.Knight;
                }
                // Destruct and unload
                MyUnit sacrificeOffering = fastSenseNearbyFriendlies(2, selectedType).get(0);
                if (sacrificeOffering != null) {
                    Direction dir = getMapLocation().directionTo(sacrificeOffering.getMapLocation());
                    sacrificeOffering.selfDestruct();
                    unload(dir);
                }
                //time5 += System.currentTimeMillis() - startTime;
                //System.out.println("time5: " + time5);
            }
        }
    }

    @Override
    protected void onDeath() {
        Worker.workersPerStructure.remove(getID());
    }

    private void initChokepoints() {

    }
}
