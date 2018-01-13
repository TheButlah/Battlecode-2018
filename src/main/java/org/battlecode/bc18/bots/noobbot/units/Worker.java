package org.battlecode.bc18.bots.noobbot.units;

import bc.*;
import org.battlecode.bc18.PathFinding;
import org.battlecode.bc18.bots.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.battlecode.bc18.bots.util.Utils.dirs;
import static org.battlecode.bc18.bots.util.Utils.gc;

public class Worker extends Robot {

    public static final UnitType TYPE = UnitType.Worker;

    @Override
    public UnitType getType() {
        return Worker.TYPE;
    }

    /**
     * Checks to see if this worker can blueprint a particular structure in a given direction.
     * The worker can only blueprint factories, and rockets if Rocketry has been researched.
     * The team must have sufficient karbonite in its resource pool.
     * The worker cannot already have performed an action this round.
     * @param type The type of structure to blueprint.
     * @param dir The direction to create the blueprint.
     * @return Whether the worker can perform the blueprint.
     */
    public boolean canBlueprint(UnitType type, Direction dir) {
        return gc.canBlueprint(getID(), type, dir);
    }

    /**
     * Blueprints a structure of the given type in the given direction.
     * NOTE: Does not check to see if it can first.
     * @param type The UnitType to blueprint. Must be a structure.
     * @param dir The direction to create the blueprint.
     * @return The structure blueprinted.
     */
    public Structure blueprint(UnitType type, Direction dir) {
        assert canBlueprint(type, dir);
        println("Blueprinting: " + type + " towards " + dir);
        gc.blueprint(getID(), type, dir);
        Unit unit = gc.senseUnitAtLocation(getMapLocation().add(dir));
        return (Structure) MyUnit.makeUnit(unit);
    }

    /**
     * Checks to see if this worker can build up a particular structure's blueprint.
     * The worker and the blueprint must be adjacent to each other.
     * The worker cannot already have performed an action this round.
     * @param blueprint The blueprint to build up.
     * @return Whether the worker can build up the blueprint.
     */
    public boolean canBuild(Structure blueprint) {
        return gc.canBuild(getID(), blueprint.getID());
    }

    /**
     * Builds a given structure's blueprint, increasing its health by the worker's build amount.
     * If raised to maximum health, the blueprint becomes a completed structure.
     * NOTE: Does not check to see if it can build first.
     * @param blueprint The structure to build.
     */
    public void build(Structure blueprint) {
        assert canBuild(blueprint);
        println("Building: " + blueprint);
        gc.build(getID(), blueprint.getID());
    }

    /**
     * Checks to see if this worker can repair a particular structure.
     * The structure must be built.
     * The worker must be within range of the structure.
     * The worker cannot already have performed an action this round.
     * @param structure The structure to repair.
     * @return Whether the worker can repair the structure.
     */
    public boolean canRepair(Structure structure) {
        return gc.canRepair(getID(), structure.getID());
    }

    /**
     * Commands the worker to repair a structure, replenishing health to it.
     * This can only be done to structures which have been fully built.
     * NOTE: Does not check to see if it can repair first.
     * @param structure The structure to repair
     */
    public void repair(Structure structure) {
        assert canRepair(structure);
        //println("Repairing: " + structure);
        gc.repair(getID(), structure.getID());
    }

    /**
     * Checks to see if this worker can harvest karbonite in a particular direction.
     * The worker cannot already have performed an action this round.
     * The direction must contain karbonite.
     * @param direction The direction in which to harvest.
     * @return Whether the worker can harvest the karbonite.
     */
    public boolean canHarvest(Direction direction) {
        return gc.canHarvest(getID(), direction);
    }
    
    /**
     * Harvests up to the worker's harvest amount of karbonite from the given location.
     * NOTE: Does not check if harvesting is permitted.
     * @param direction The direction in which to harvest.
     */
    public void harvest(Direction direction) {
        assert canHarvest(direction);
        //println("Harvesting: towards " + direction);
        gc.harvest(getID(), direction);
    }

    /**
     * Checks to see if this worker can replicate in a particular direction.
     * The the worker's ability heat must be sufficiently low.
     * The team must have sufficient karbonite in its resource pool.
     * The square in the given direction must be empty.
     * @param direction The direction in which to replicate.
     * @return Whether the worker can replicate.
     */
    public boolean canReplicate(Direction direction) {
        return gc.canReplicate(getID(), direction);
    }

    /**
     * Replicates a worker in the given direction.
     * NOTE: Does not check if replication is permitted.
     * @param direction The direction in which to replicate.
     * @return The new replicated worker.
     */
    public Worker replicate(Direction direction) {
        //println("Replicating: towards " + direction);
        gc.replicate(getID(), direction);
        Unit unit = gc.senseUnitAtLocation(getMapLocation().add(direction));
        if (unit.unitType() != UnitType.Worker) {
            return null;
        }
        return new Worker(unit);
    }



    //////////END OF API//////////


    /**
     * Constructor for Worker.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Worker(Unit unit) {
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
        Factory targetFactory = targetFactoryId != null ? (Factory)MyUnit.units.get(targetFactoryId) : null;
        if (targetFactory != null && targetFactory.getHealth() < 0) {
            println("Noticed factory that wasn't dead!");
            deassignFactory();
            targetFactory = null;
        }

        if (turn == 1 || gc.karbonite() >= 300) {
            List<MyUnit> nearbyFactories = senseNearbyFriendlies(2, UnitType.Factory);
            if (nearbyFactories.size() == 0) {
                // for each direction, find the first availabile spot for a factory.
                for (Direction dir : dirs) {
                    if (canBlueprint(UnitType.Factory, dir)) {
                        println("Blueprinting: " + UnitType.Factory + " towards " + dir);
                        targetFactory = (Factory) blueprint(UnitType.Factory, dir);
                        assignFactory(targetFactory.getID());
                    }
                }
            }
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
                if (MyUnit.workersPerFactory.get(targetFactory.getID()) > 3) {
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
}
