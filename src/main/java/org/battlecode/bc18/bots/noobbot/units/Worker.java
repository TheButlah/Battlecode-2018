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
     * Replicate this worker to the first available direction.
     * @return true if replication was successful, false otherwise
     */
    public boolean replicate() {
        for (Direction dir : Direction.values()) {
            if (replicate(dir)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Replicate this worker to the specified direction.
     * @param dir the specified direction
     * @return true if replication to the direction was successful, false otherwise
     */
    public boolean replicate(Direction dir) {
        if (gc.canReplicate(this.id, dir)) {
            gc.replicate(this.id, dir);
            MyUnit.bots.put(this.id, this);
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
                if (!hasPlacedFactory() && gc.canBlueprint(this.id, UnitType.Factory, dir)) {
                    println("Blueprinting");
                    gc.blueprint(this.id, UnitType.Factory, dir);
                    targetFactory = gc.senseUnitAtLocation(myMapLoc.add(dir));
                    factoryId = targetFactory.id();
                    bots.put(factoryId, new Factory(factoryId));
                    return;
                }
            }
        }

        // building a factory based on the blueprint created.
        if (targetFactory != null && gc.canBuild(this.id, factoryId)) {
            println("Building");
            gc.build(this.id, factoryId);
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
                if (gc.canReplicate(this.id, dir)) {
                    println("Replicating");
                    gc.replicate(this.id, dir);
                    Unit newWorker = gc.senseUnitAtLocation(myMapLoc.add(dir));
                    bots.put(newWorker.id(), new Worker(newWorker.id()));
                    return;
                }
            }
        }

        // if can see Karbonite, mine it
        for (Direction dir : Direction.values()) {
            if (gc.canHarvest(this.id, dir)) {
                println("Harvesting'");
                gc.harvest(this.id, dir);
                return;
            }
        }

        if (gc.isMoveReady(this.id)) {
            if (targetFactory != null) {
                Direction towardsFactory = myMapLoc.directionTo(targetFactory.location().mapLocation());
                if (gc.canMove(this.id, towardsFactory)) {
                    gc.moveRobot(this.id, towardsFactory);
                    return;
                }
            }
            else {
                //Move randomly
                int rand = Utils.rand.nextInt(Utils.dirs.length);
                for (int i = 0; i < Utils.dirs.length; i++) {
                    Direction dir = Utils.dirs[(i + rand) % Utils.dirs.length]; //Cycle through based on random offset
                    if (gc.canMove(this.id, dir)) {
                        //println("Moving");
                        gc.moveRobot(this.id, dir);
                        return;
                    }
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
