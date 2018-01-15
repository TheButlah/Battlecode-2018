package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.PathFinding;
import org.battlecode.bc18.api.*;
import org.battlecode.bc18.api.MyUnit;
import org.battlecode.bc18.util.Pair;
import org.battlecode.bc18.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.battlecode.bc18.util.Utils.dirs;
import static org.battlecode.bc18.util.Utils.gc;

public class Worker extends AWorker {

    /**
     * Constructor for Worker.
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

        Factory targetFactory = getFactoryAssignment();
        if (targetFactory != null && targetFactory.isDead()) {
            println("Assigned to dead factory!");
            deassignFactory();
            targetFactory = null;
        }

        if (turn == 1 || gc.karbonite() >= 300) {
            List<MyUnit> nearbyFactories = senseNearbyFriendlies(3, UnitType.Factory);
            ArrayList<MapLocation> nearbyFactoriesLoc = new ArrayList<>();
            for (MyUnit factory : nearbyFactories) {
                nearbyFactoriesLoc.add(factory.getMapLocation());
            }
            // for each direction, find the first availabile spot for a factory.
            for (Direction dir : dirs) {
                if (canBlueprint(UnitType.Factory, dir)
                        && !Utils.isAnyAdjacent(nearbyFactoriesLoc, myMapLoc.add(dir))) {
                    //println("Blueprinting: " + UnitType.Factory + " towards " + dir);
                    targetFactory = (Factory) blueprint(UnitType.Factory, dir);
                    assignFactory(targetFactory);
                    break;
                }
            }
        }

        if (!hasActed() && turn >= 200 && gc.karbonite() >= 200) {
            List<MyUnit> nearbyFactories = senseNearbyFriendlies(2, UnitType.Factory);
            // TODO: blueprint rockets
        }

        if (targetFactory == null) {
            List<MyUnit> nearbyFactories = senseNearbyFriendlies(UnitType.Factory);
            Factory closestFactory = null;
            long closestFactoryDist = Long.MAX_VALUE;
            for (MyUnit unit : nearbyFactories) {
                Factory factory = (Factory) unit;
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
                assignFactory(targetFactory);
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
            } else {
                //No target factory, so look for nearby karbonite
                List<Pair<MapLocation, Integer>> deposits = senseNearbyKarbonite();
                if (deposits.size() != 0) {
                    Pair<MapLocation, Integer> targetDeposit = Utils.closestPair(deposits, myMapLoc);
                    MapLocation targetLoc = targetDeposit.getFirst();
                    int[][] distances = PathFinding.earthPathfinder.search(
                        targetLoc.getY(),
                        targetLoc.getX());
                    Direction towardsKarbonite = PathFinding.moveDirectionToDestination(
                        distances,
                        myMapLoc.getY(),
                        myMapLoc.getX(),
                        myMapLoc.getPlanet());
                    if (isAccessible(towardsKarbonite)) {
                        move(towardsKarbonite);
                    }
                } else {
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
                List<MyUnit> nearbyWorkers = senseNearbyFriendlies(UnitType.Worker);
                if (nearbyWorkers.size() < 7) {
                    for (Direction dir : Utils.dirs) {
                        if (canReplicate(dir)) {
                            //println("Replicating");
                            MyUnit newWorker = replicate(dir);
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
                if (Factory.workersPerFactory.get(targetFactory.getID()) > 3) {
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
        deassignFactory();
    }

    /**
     * Gets the {@link Factory} assigned to the {@link Worker} calling this method
     * Pre-condition: this method should only be called by instances of the {@link Worker} class
     * @return the factory
     */
    Factory getFactoryAssignment() {
        return Factory.workerFactoryAssignment.get(getID());
    }

    /**
     * Assigns the factory with the given ID to the {@link Worker} calling this method.
     * Pre-condition: this method should only be called by instances of the {@link Worker} class.
     * @param factory the factory.
     */
    void assignFactory(Factory factory) {
        Factory.workerFactoryAssignment.put(getID(), factory);
        if (!Factory.workersPerFactory.containsKey(factory.getID())) {
            Factory.workersPerFactory.put(factory.getID(), 1);
        }
        else {
            Factory.workersPerFactory.put(factory.getID(), Factory.workersPerFactory.get(factory.getID()) + 1);
        }
    }

    /**
     * De-assigns the factory assigned to the {@link Worker} calling this method.
     * If there is no assigned factory, no changes are made
     * Pre-condition: this method should only be called by instances of the {@link Worker} class
     * @return the the de-assigned factory, or null if none
     */
    Factory deassignFactory() {
        Factory factory = Factory.workerFactoryAssignment.remove(getID());
        if (factory != null) {
            Integer count = Factory.workersPerFactory.get(factory.getID());
            if (count != null) {
                Factory.workersPerFactory.put(factory.getID(), count - 1);
            }
        }
        return factory;
    }
}
