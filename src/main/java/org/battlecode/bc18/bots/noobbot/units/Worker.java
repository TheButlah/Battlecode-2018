package org.battlecode.bc18.bots.noobbot.units;

import bc.*;
import org.battlecode.bc18.Utils;
import org.battlecode.bc18.bots.noobbot.Main;

import static org.battlecode.bc18.Utils.gc;

public class Worker extends Robot {

    public static final UnitType TYPE = UnitType.Worker;
    
    private int factoryId = -1; //-1 indicates no factory has been placed
    private boolean builtFactory = false;
    private Unit targetFactory = null;

    Worker(int id) {
        super(id);
    }

    /**
     * Blueprints a unit of the given type in the given direction.
     * Subtract cost of that unit from the team's resource pool.
     * @param ut UnitType to blueprint
     * @param dir the given direction
     * @return true if blueprinting was successful, false otherwise
     */
    public boolean blueprint(UnitType ut, Direction dir) {
        if (gc.canBlueprint(this.id, ut, dir)) {
            println("Blueprinting: " + ut + " toward " + dir);
            gc.blueprint(this.id, UnitType.Factory, dir);
            factoryId = gc.senseUnitAtLocation(getMyMapLocation().add(dir)).id();
            bots.put(factoryId, new Factory(factoryId));
            return true;
        }
        return false;
    }

    /**
     * Builds a given blueprint, increasing its health by the worker's build amount.
     * If raised to maximum health, the blueprint becomes a completed structure.
     * @param buildUnitID UnitID to build
     * @return true if building was successful, false otherwise
     */
    public boolean build(int buildUnitID) {
        if (gc.canBuild(this.id, buildUnitID)) {
            println("Building " + buildUnitID);
            gc.build(this.id, factoryId);
            return true;
        }
        return false;
    }

    /**
     * Commands the worker to repair a structure, replenishing health to it.
     * This can only be done to structures which have been fully built.
     * @param structureID structure id to repair
     * @return true if repairing was successful, false otherwise
     */
    public boolean repair(int structureID) {
        if (gc.canRepair(this.id, structureID)) {
            println("Repairing " + structureID);
            gc.repair(this.id, structureID);
            return true;
        }
        return false;
    }
    
    /**
     * Harvests up to the worker's harvest amount of karbonite from the given location,
     * adding it to the team's resource pool.
     * @param dir the given direction to harvest
     * @return true if harvesting was successful, false otherwise
     */
    public boolean harvest(Direction dir) {
        if (gc.canHarvest(this.id, dir)) {
            println("Harvesting");
            gc.harvest(this.id, dir);
            return true;
        }
        return false;
    }

    /**
     * Replicates a worker in the given direction.
     * Subtracts the cost of the worker from the team's resource pool.
     * @param dir the specified direction
     * @return true if replication to the direction was successful, false otherwise
     */
    public boolean replicate(Direction dir) {
        if (gc.canReplicate(this.id, dir)) {
            println("Replicating");
            gc.replicate(this.id, dir);
            int newUnitID = gc.senseUnitAtLocation(getMyMapLocation().add(dir)).id();
            bots.put(newUnitID, new Worker(newUnitID));
            return true;
        }
        return false;
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
        return factoryId != -1;
    }
}
