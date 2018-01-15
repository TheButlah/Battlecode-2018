package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.PathFinding;
import org.battlecode.bc18.api.*;
import org.battlecode.bc18.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.battlecode.bc18.util.Utils.dirs;
import static org.battlecode.bc18.util.Utils.gc;

public class Worker extends AbstractWorker {

    /**
     * Constructor for AbstractWorker.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected Worker(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Worker;
    }

    @Override
    public void act() {
        // first turn, build a targetFactory [by one unit].
        // if the targetFactory has not been built, replicate if can and help building a targetFactory.
        // else, move randomly.
        // try mining if walked over the Karbonite.
        long turn = gc.round();
        if (!isOnMap()) {
            //TODO: Handle worker in space/garrison/dead?
            println("TODO: handle worker not on map");
            return;
        }
        //We already checked that we were on the map
        MapLocation myMapLoc = getMapLocation();

        Integer targetFactoryId = getFactoryAssignment();
        AbstractFactory targetFactory = targetFactoryId != null ? (AbstractFactory) getUnit(targetFactoryId) : null;
        if (targetFactory != null && targetFactory.isDead()) {
            println("Assigned to dead factory!");
            deassignFactory();
            targetFactory = null;
        }

        if (turn == 1 || gc.karbonite() >= 300) {
            List<AbstractUnit> nearbyFactories = senseNearbyFriendlies(2, UnitType.Factory);
            if (nearbyFactories.size() == 0) {
                // for each direction, find the first availabile spot for a factory.
                for (Direction dir : dirs) {
                    if (canBlueprint(UnitType.Factory, dir)) {
                        println("Blueprinting: " + UnitType.Factory + " towards " + dir);
                        targetFactory = (AbstractFactory) blueprint(UnitType.Factory, dir);
                        assignFactory(targetFactory.getID());
                    }
                }
            }
        }

        if (targetFactory == null) {
            List<AbstractUnit> nearbyFactories = senseNearbyFriendlies(UnitType.Factory);
            AbstractFactory closestFactory = null;
            long closestFactoryDist = Long.MAX_VALUE;
            for (AbstractUnit unit : nearbyFactories) {
                AbstractFactory factory = (AbstractFactory) unit;
                if (!factory.isBuilt() || factory.getHealth() < factory.getMaxHealth() * 3 / 4) {
                    long distance = factory.getMapLocation().distanceSquaredTo(myMapLoc);
                    if (distance < closestFactoryDist) {
                        closestFactory = factory;
                        closestFactoryDist = distance;
                    }
                }
            }
            targetFactory = closestFactory;
            if (targetFactory != null) {
                assignFactory(targetFactory.getID());
            }
        }
        //targetFactory should now be set. If its still null, we lost all out factories.

        if (isMoveReady()) {
            if (targetFactory != null) {
                // Move towards target factory
                MapLocation factoryLoc = targetFactory.getMapLocation();
                int[][] distances = PathFinding.earthPathfinder.search(factoryLoc.getY(),
                        factoryLoc.getX());
                Direction towardsFactory = PathFinding.moveDirectionToDestination(distances,
                        myMapLoc.getY(), myMapLoc.getX(), myMapLoc.getPlanet());
                if (isAccessible(towardsFactory)) {
                    move(towardsFactory);
                }
            }
            else {
                //No target factory, so look for nearby karbonite
                List<MapLocation> nearbyKarbonite = senseNearbyKarbonite(myMapLoc,
                        getVisionRange());
                if (nearbyKarbonite.size() != 0) {
                    MapLocation targetKarbonite = Utils.closest(nearbyKarbonite, myMapLoc);
                    int[][] distances = PathFinding.earthPathfinder.search(targetKarbonite.getY(),
                            targetKarbonite.getX());
                    Direction towardsKarbonite = PathFinding.moveDirectionToDestination(distances,
                            myMapLoc.getY(), myMapLoc.getX(), myMapLoc.getPlanet());
                    if (isAccessible(towardsKarbonite)) {
                        move(towardsKarbonite);
                    }
                }
                else {
                    //Move randomly
                    int offset = Utils.rand.nextInt(Utils.dirs.length);
                    for (int i = 0; i < Utils.dirs.length; i++) {
                        Direction dir = Utils.dirs[(i + offset) % Utils.dirs.length]; //Cycle through based on random offset
                        if (isAccessible(dir)) {
                            //println("Moving");
                            move(dir);
                            break;
                        }
                    }
                }
            }
        }

        if (Utils.toBool(getAsUnit().workerHasActed())) {
            return;
        }

        if (targetFactory != null) {
            boolean factoryBuilt = targetFactory.isBuilt();
            boolean needsRepair = targetFactory.getHealth() < 3 * targetFactory.getMaxHealth() / 4;
            if (!factoryBuilt || needsRepair) {
                // replicate if factory not yet built or factory damaged
                List<AbstractUnit> nearbyWorkers = senseNearbyFriendlies(UnitType.Worker);
                if (nearbyWorkers.size() < 7) {
                    for (Direction dir : Utils.dirs) {
                        if (canReplicate(dir)) {
                            //println("Replicating");
                            AbstractUnit newWorker = replicate(dir);
                            if (newWorker != null) {
                                return;
                            }
                        }
                    }
                }
                // building a factory based on the blueprint created.
                if (!factoryBuilt) {
                    if (canBuild(targetFactory)) {
                        //println("Building");
                        build(targetFactory);
                        return;
                    }
                }
                else if (canRepair(targetFactory)) {
                    //println("Repairing");
                    repair(targetFactory);
                    return;
                }
            }
            else {
                // De-assign worker from factory so he can explore the map
                if (AbstractFactory.workersPerFactory.get(targetFactory.getID()) > 3) {
                    deassignFactory();
                    targetFactory = null;
                }
            }
        }

        // if can see Karbonite, mine it
        for (Direction dir : Utils.dirs) {
            if (canHarvest(dir)) {
                //println("Harvesting");
                harvest(dir);
                return;
            }
        }
    }

    @Override
    protected void onDeath() {

    }

    public ArrayList<MapLocation> senseNearbyKarbonite(MapLocation here, int senseRange) {
        int x = here.getX();
        int y = here.getY();
        Planet planet = here.getPlanet();
        ArrayList<MapLocation> nearbyKarbonite = new ArrayList<>();
        senseRange = (int)Math.sqrt(senseRange);
        for (int r = y - senseRange; r <= y + senseRange; ++r) {
            if (r < 0 || r > Utils.earthHeight) {
                continue;
            }
            for (int c = x - senseRange; c <= x + senseRange; ++c) {
                if (c < 0 || c > Utils.earthWidth) {
                    continue;
                }
                try {
                    MapLocation loc = new MapLocation(planet, c, r);
                    if (gc.karboniteAt(loc) > 0) {
                        nearbyKarbonite.add(loc);
                    }
                }
                catch (Exception e) { }
            }
        }
        return nearbyKarbonite;
    }

    /**
     * Gets ID of the factory assigned to the {@link Worker} calling this method
     * Pre-condition: this method should only be called by instances of the {@link Worker} class
     * @return the factory ID
     */
    Integer getFactoryAssignment() {
        return AbstractFactory.workerFactoryAssignment.get(getID());
    }

    /**
     * Assigns the factory with the given ID to the {@link Worker} calling this method
     * Pre-condition: this method should only be called by instances of the {@link Worker} class
     * @param factoryId the factory ID
     */
    void assignFactory(int factoryId) {
        AbstractFactory.workerFactoryAssignment.put(getID(), factoryId);
        if (!AbstractFactory.workersPerFactory.containsKey(factoryId)) {
            AbstractFactory.workersPerFactory.put(factoryId, 1);
        }
        else {
            AbstractFactory.workersPerFactory.put(factoryId, AbstractFactory.workersPerFactory.get(factoryId) + 1);
        }
    }

    /**
     * De-assigns the factory assigned to the {@link Worker} calling this method.
     * If there is no assigned factory, no changes are made
     * Pre-condition: this method should only be called by instances of the {@link Worker} class
     * @return the ID of the de-assigned factory, or null if none
     */
    Integer deassignFactory() {
        Integer factoryId = AbstractFactory.workerFactoryAssignment.remove(getID());
        if (factoryId != null) {
            Integer count = AbstractFactory.workersPerFactory.get(factoryId);
            if (count != null) {
                AbstractFactory.workersPerFactory.put(factoryId, count - 1);
            }
        }
        return factoryId;
    }
}
