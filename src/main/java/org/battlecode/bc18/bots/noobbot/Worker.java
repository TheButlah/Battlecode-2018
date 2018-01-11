package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.Utils;

import java.util.HashSet;

import static org.battlecode.bc18.Utils.bots;
import static org.battlecode.bc18.Utils.gc;

public class Worker extends Bot {

    public static final UnitType TYPE = UnitType.Worker;
    
    private static int factoryID = -1; //-1 indicates no factory has been placed
    private static boolean builtFactory = false;

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

        // for each direction, find the first availability spot for a factory.
        for (Direction dir : Main.dirs) {
            if (!hasPlacedFactory() && gc.canBlueprint(this.id, UnitType.Factory, dir)) {
                println("Blueprinting");
                gc.blueprint(this.id, UnitType.Factory, dir);
                Unit factory = gc.senseUnitAtLocation(getAsUnit().location().mapLocation().add(dir));
                factoryID = factory.id();
                bots.put(factoryID, new Factory(factoryID));
                return;
            }
        }

        // building a factory based on the blueprint created.
        if (gc.canBuild(this.id, factoryID)) {
            println("Building");
            gc.build(this.id, factoryID);
            return;
        }

        if (!builtFactory &&
            gc.canSenseUnit(factoryID) &&
            Utils.toBool(gc.unit(factoryID).structureIsBuilt())) builtFactory = true;

        // replicate if factory not yet built
        if (hasPlacedFactory() && !builtFactory) { //factory placed but not built
            println("factory building");
            MapLocation factoryLoc = bots.get(factoryID).getAsUnit().location().mapLocation();
            for (Direction dir : Main.dirs) {
                //only replicate into spots adjacent to factory (since I don't feel like using pathfinding yet)
                if (!(getAsUnit().location().mapLocation().add(dir).isAdjacentTo(factoryLoc))) continue;
                println("found spot next to factory");
                if (gc.canReplicate(this.id, dir)) {
                    println("Replicating");
                    gc.replicate(this.id, dir);
                    Unit newWorker = gc.senseUnitAtLocation(getAsUnit().location().mapLocation().add(dir));
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

        //Move randomly
        if (gc.isMoveReady(this.id)) {
            int rand = Utils.rand.nextInt(Main.dirs.length);
            for (int i=0; i<Main.dirs.length; i++) {
                Direction dir = Main.dirs[(i + rand) % Main.dirs.length]; //Cycle through based on random offset
                if (gc.canMove(this.id, dir)) {
                    //println("Moving");
                    gc.moveRobot(this.id, dir);
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
        return factoryID != -1;
    }
}
