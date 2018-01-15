package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.api.AbstractFactory;
import org.battlecode.bc18.api.AbstractUnit;
import org.battlecode.bc18.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factory extends AbstractFactory {

    /**
     * A mapping of factories to numbers of workers assigned to each factory
     */
    public static final Map<Integer, Integer> workersPerFactory = new HashMap<>();
    /**
     * A mapping of workers to the factories they are assigned to
     */
    public static final Map<Integer, Integer> workerFactoryAssignment = new HashMap<>();

    /**
     * Constructor for Factory.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Factory(Unit unit) {
        super(unit);
    }

    @Override
    public void act() {
        System.out.println("Workers assigned to me: " + workersPerFactory.get(getID()));
        // Since we cant maintain the invariant for the units HashMap, manually add in units to ensure invariant.
        VecUnitID vec = getAsUnit().structureGarrison();
        getUnits(vec); //Add the

        // make and place knights until you can't :D
        List<AbstractUnit> nearbyWorkers = senseNearbyFriendlies(UnitType.Worker);
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
