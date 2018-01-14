package org.battlecode.bc18.bots.noobbot.units;

import static org.battlecode.bc18.bots.util.Utils.gc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.battlecode.bc18.bots.util.Utils;

import bc.Direction;
import bc.MapLocation;
import bc.Unit;
import bc.UnitType;
import bc.VecUnitID;

public class Factory extends Structure {

    public static final UnitType TYPE = UnitType.Factory;
    /**
     * A mapping of factories to numbers of workers assigned to each factory
     */
    public static final Map<Integer, Integer> workersPerFactory = new HashMap<>();
    /**
     * A mapping of workers to the factories they are assigned to
     */
    public static final Map<Integer, Integer> workerFactoryAssignment = new HashMap<>();

    /**
     * Whether the factory can produce a robot.
     * Must not currently be producing a robot, and must have sufficient resources.
     * @param type The type of robot to check.
     */
    public boolean canProduceRobot(UnitType type) {
        return gc.canProduceRobot(getID(), type);
    }

    /**
     * Starts producing the robot of the given type.
     * NOTE: Does not check to see if it can produce it first.
     * @param type The UnitType of the robot to produce. Must be a robot.
     * @return The robot produced.
     */
    public Robot produceRobot(UnitType type) {
        assert canProduceRobot(type);
        println("Producing: " + type);
        gc.produceRobot(getID(), type);
        /*Unit unit = gc.unit(getMapLocation());
        return (Structure) MyUnit.makeUnit(unit);*/
        return null; //TODO: Figure out how to get the Unit object of a robot being built.
    }


    //////////END OF API//////////

    /**
     * Constructor for Factory.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Factory(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Factory;
    }

    @Override
    public void act() {
        if (isDead()) return;
        System.out.println("Workers assigned to me: " + workersPerFactory.get(getID()));
        // Since we cant maintain the invariant for the units HashMap, manually add in units to ensure invariant.
        VecUnitID vec = getAsUnit().structureGarrison();
        for (int i=0; i<vec.size(); i++) {
            int id = vec.get(i);
            getUnit(gc.unit(id));
        }
        // make and place knights until you can't :D
        //Unit myUnit = this.getAsUnit();
        MapLocation myMapLoc = getMapLocation();
        List<MyUnit> nearbyWorkers = senseNearbyFriendlies(UnitType.Worker);
        boolean hasNearbyWorker = (nearbyWorkers.size() >=1);

        if (!hasNearbyWorker && canProduceRobot(UnitType.Worker)) {
            produceRobot(UnitType.Worker);
        }
        else if (canProduceRobot(UnitType.Knight)) {
            produceRobot(UnitType.Knight);
        }
        // Unload units
        for (Direction dir : Utils.dirs) {
            // if there are no more units to unload, break
            if (getAsUnit().structureGarrison().size() == 0) break;
            if (gc.canUnload(getID(), dir)) {
                gc.unload(getID(), dir);
                MapLocation unloadLoc = myMapLoc.add(dir);
                Unit unloadedUnit = gc.senseUnitAtLocation(unloadLoc);
                MyUnit.units.get(unloadedUnit.id()).setLocation(unloadLoc);
                // no break here so it can unload multiple (I think that's allowed)
            }
        }
    }

    @Override
    public UnitType getType() {
        return Factory.TYPE;
    }
}
