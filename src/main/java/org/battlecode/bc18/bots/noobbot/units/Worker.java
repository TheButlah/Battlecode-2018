package org.battlecode.bc18.bots.noobbot.units;

import bc.*;
import org.battlecode.bc18.Utils;

import static org.battlecode.bc18.Utils.gc;

public class Worker extends Robot {

    public static final UnitType TYPE = UnitType.Worker;
    
    private Factory factory;

    /**
     * Constructor for Worker.
     * @exception RuntimeException Occurs when a unit with that id already exists.
     */
    Worker(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Worker;
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
        return gc.canBlueprint(this.id, type, dir);
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
        gc.blueprint(this.id, type, dir);
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
        return gc.canBuild(this.id, blueprint.getID());
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
        gc.build(this.id, blueprint.getID());
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
        return gc.canRepair(this.id, structure.getID());
    }

    /**
     * Commands the worker to repair a structure, replenishing health to it.
     * This can only be done to structures which have been fully built.
     * NOTE: Does not check to see if it can repair first.
     * @param structure The structure to repair
     */
    public void repair(Structure structure) {
        assert canRepair(structure);
        println("Repairing: " + structure);
        gc.repair(this.id, structure.getID());
    }

    /**
     * Checks to see if this worker can harvest karbonite in a particular direction.
     * The worker cannot already have performed an action this round.
     * The direction must contain karbonite.
     * @param direction The direction in which to harvest.
     * @return Whether the worker can harvest the karbonite.
     */
    public boolean canHarvest(Direction direction) {
        return gc.canHarvest(this.id, direction);
    }
    
    /**
     * Harvests up to the worker's harvest amount of karbonite from the given location.
     * NOTE: Does not check if harvesting is permitted.
     * @param direction The direction in which to harvest.
     */
    public void harvest(Direction direction) {
        assert canHarvest(direction);
        println("Harvesting: towards " + direction);
        gc.harvest(this.id, direction);
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
        return gc.canReplicate(this.id, direction);
    }

    /**
     * Replicates a worker in the given direction.
     * NOTE: Does not check if replication is permitted.
     * @param direction The direction in which to replicate.
     * @return The new replicated worker.
     */
    public Worker replicate(Direction direction) {
        println("Replicating: towards " + direction);
        gc.replicate(this.id, direction);
        Unit unit = gc.senseUnitAtLocation(getMapLocation().add(direction));
        return new Worker(unit);
    }

    @Override
    public void act() {
        // first turn, build a factory [by one unit].
        // if the factory has not been built, replicate if can and help building a factory.
        // else, move randomly.
        // try mining if walked over the Karbonite.
        long turn = gc.round();
        Unit myUnit = getAsUnit();
        Location myLoc = myUnit.location();
        if (!myLoc.isOnMap()) {
            println("TODO: handle worker in space");
            return;
        }
        MapLocation myMapLoc = myLoc.mapLocation();

        if (turn == 1) {
            // for each direction, find the first availability spot for a factory.
            for (Direction dir : Utils.dirs) {
                if (!hasPlacedFactory()) {
                    if (blueprint(UnitType.Factory, dir))
                        return;
                }
            }
        }

        // building a factory based on the blueprint created.
        if (targetFactory != null) {
            if (build(factoryId))
                return;
        }
        else {
            VecUnit nearbyFactories = gc.senseNearbyUnitsByType(myMapLoc, myUnit.visionRange(), UnitType.Factory);
            Unit closestFactory = null;
            long closestFactoryDist = Long.MAX_VALUE;
            for (int i = 0; i < nearbyFactories.size(); ++i) {
                Unit factory = nearbyFactories.get(i);
                if (factory.team() == myUnit.team() && !Utils.toBool(factory.structureIsBuilt())) {
                    long distance = factory.location().mapLocation().distanceSquaredTo(myMapLoc);
                    if (distance < closestFactoryDist) {
                        closestFactory = factory;
                        closestFactoryDist = distance;
                        break;
                    }
                }
            }
            targetFactory = closestFactory;
        }

        if (hasPlacedFactory() && !builtFactory) { //factory placed but not built
            if (gc.canSenseUnit(factoryId) && Utils.toBool(gc.unit(factoryId).structureIsBuilt())) {
                builtFactory = true;
                println("Finished building factory");
            }
        }
        // replicate if factory not yet built
        if (targetFactory != null) {
            println("factory building");
            MapLocation factoryLoc = targetFactory.location().mapLocation();
            for (Direction dir : Utils.dirs) {
                //only replicate into spots adjacent to factory (since I don't feel like using pathfinding yet)
                if (!(myMapLoc.add(dir).isAdjacentTo(factoryLoc))) continue;
                println("found spot next to factory");
                if (replicate(dir))
                    return;
            }
        }

        // if can see Karbonite, mine it
        for (Direction dir : Direction.values()) {
            if (harvest(dir))
                return;
        }

        if (gc.isMoveReady(this.id)) {
            if (targetFactory != null) {
                Direction towardsFactory = myMapLoc.directionTo(targetFactory.location().mapLocation());
                if (move(towardsFactory))
                    return;
            }
            else {
                //Move randomly
                int rand = Utils.rand.nextInt(Utils.dirs.length);
                for (int i = 0; i < Utils.dirs.length; i++) {
                    Direction dir = Utils.dirs[(i + rand) % Utils.dirs.length]; //Cycle through based on random offset
                    if (move(dir))
                        return;
                }
            }
        }

    }

    @Override
    public UnitType getType() {
        return Worker.TYPE;
    }

    private boolean hasPlacedFactory() {
        return factory != null;
    }
}
