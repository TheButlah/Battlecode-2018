package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.Utils.bots;
import static org.battlecode.bc18.Utils.gc;

import java.util.ArrayList;

import org.battlecode.bc18.PathFinding;
import org.battlecode.bc18.Utils;

import bc.Direction;
import bc.Location;
import bc.MapLocation;
import bc.Planet;
import bc.Unit;
import bc.UnitType;
import bc.VecUnit;

public class Worker extends Bot {

    public static final UnitType TYPE = UnitType.Worker;
    
    private int factoryId = -1; //-1 indicates no factory has been placed
    private boolean builtFactory = false;
    private Unit targetFactory = null;

    public Worker(int id) {
        super(id);
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

        if (turn == 1 || gc.karbonite() >= 300) {
            VecUnit nearbyFactories = gc.senseNearbyUnitsByType(myMapLoc, 2, UnitType.Factory);
            if (nearbyFactories.size() == 0) {
                // for each direction, find the first availability spot for a factory.
                for (Direction dir : Utils.dirs) {
                    if (!hasPlacedFactory() && gc.canBlueprint(this.id, UnitType.Factory, dir)) {
                        println("Blueprinting");
                        gc.blueprint(this.id, UnitType.Factory, dir);
                        targetFactory = gc.senseUnitAtLocation(myMapLoc.add(dir));
                        factoryId = targetFactory.id();
                        bots.put(factoryId, new Factory(factoryId));
                    }
                }
            }
        }

        if (targetFactory == null) {
            VecUnit nearbyFactories = gc.senseNearbyUnitsByType(myMapLoc, myUnit.visionRange(), UnitType.Factory);
            Unit closestFactory = null;
            long closestFactoryDist = Long.MAX_VALUE;
            for (int i = 0; i < nearbyFactories.size(); ++i) {
                Unit factory = nearbyFactories.get(i);
                if (factory.team() == myUnit.team() &&
                  (!Utils.toBool(factory.structureIsBuilt()) || factory.health() < factory.maxHealth() * 3 / 4)) {
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

        if (gc.isMoveReady(this.id)) {
            if (targetFactory != null) {
                // Move towards target factory
                MapLocation factoryLoc = targetFactory.location().mapLocation();
                int[][] distances = PathFinding.earthPathfinder.search(factoryLoc.getY(), factoryLoc.getX());
                Direction towardsFactory = PathFinding.moveDirectionToDestination(distances, myMapLoc.getY(), myMapLoc.getX(), myMapLoc.getPlanet());
                if (gc.canMove(this.id, towardsFactory)) {
                    gc.moveRobot(this.id, towardsFactory);
                }
            }
            else {
                // Look for nearby karbonite
                ArrayList<MapLocation> nearbyKarbonite = senseNearbyKarbonite(myMapLoc, (int)myUnit.visionRange());
                if (nearbyKarbonite.size() != 0) {
                    MapLocation targetKarbonite = Utils.closest(nearbyKarbonite, myMapLoc);
                    int[][] distances = PathFinding.earthPathfinder.search(targetKarbonite.getY(), targetKarbonite.getX());
                    Direction towardsKarbonite = PathFinding.moveDirectionToDestination(distances, myMapLoc.getY(), myMapLoc.getX(), myMapLoc.getPlanet());
                    if (gc.canMove(this.id, towardsKarbonite)) {
                        gc.moveRobot(this.id, towardsKarbonite);
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
                        }
                    }
                }
            }
        }

        if (hasPlacedFactory() && !builtFactory) { //factory placed but not built
            if (gc.canSenseUnit(factoryId) && Utils.toBool(gc.unit(factoryId).structureIsBuilt())) {
                builtFactory = true;
                println("Finished building factory");
            }
        }
        if (Utils.toBool(myUnit.workerHasActed())) {
            return;
        }
        // replicate if factory not yet built or factory damaged
        if (targetFactory != null) {
            VecUnit nearbyWorkers = gc.senseNearbyUnitsByType(myMapLoc, myUnit.visionRange(), UnitType.Worker);
            int numNearbyFriendlyWorkers = 0;
            for (int i = 0; i < nearbyWorkers.size(); ++i) {
                if (nearbyWorkers.get(i).team() == myUnit.team()) {
                    ++numNearbyFriendlyWorkers;
                }
            }
            if (numNearbyFriendlyWorkers < 7) {
                for (Direction dir : Utils.dirs) {
                    if (gc.canReplicate(this.id, dir)) {
                        println("Replicating");
                        try {
                            gc.replicate(this.id, dir);
                            // TODO FIXME: replicate doesn't throw errors!
                            Unit newWorker = gc.senseUnitAtLocation(myMapLoc.add(dir));
                            if (newWorker.unitType() != UnitType.Worker) {
                                continue;
                            }
                            bots.put(newWorker.id(), new Worker(newWorker.id()));
                        }
                        catch (Exception e) { } // replicate failed
                    }
                }
            }
        }

        // if can see Karbonite, mine it
        for (Direction dir : Utils.dirs) {
            if (gc.canHarvest(this.id, dir)) {
                println("Harvesting'");
                gc.harvest(this.id, dir);
                return;
            }
        }

        if (targetFactory != null) {
            // building a factory based on the blueprint created.
            if (gc.canBuild(this.id, factoryId)) {
                println("Building");
                gc.build(this.id, factoryId);
                return;
            }
            if (gc.canRepair(this.id, factoryId)) {
                println("Repairing");
                gc.repair(this.id, factoryId);
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

    @Override
    public UnitType getType() {
        return Worker.TYPE;
    }

    private boolean hasPlacedFactory() {
        return factoryId != -1;
    }
}
