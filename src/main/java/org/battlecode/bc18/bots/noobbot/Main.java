package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.bots.noobbot.Knight.tman;
import static org.battlecode.bc18.util.Utils.gc;

import java.util.ArrayList;
import java.util.Arrays;

import bc.*;
import org.battlecode.bc18.PathFinder;
import org.battlecode.bc18.TargetManager;
import org.battlecode.bc18.api.AUnit;
import org.battlecode.bc18.util.Utils;

public class Main {
    public static int initializingWorkerId;

    public static void main(String[] args) {
        System.out.println("INITIALIZING MUSK THE DESTROYER - \"MEME MACHINE\" CLASS.");
        System.out.println("YOUR INFERIOR PROGRAMMING WILL BE TERMINATED.");

        //Without this, MyUnit cannot form new units
        AUnit.init(new UnitBuilder());

        gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Worker);

        if (gc.planet() == Planet.Earth) {
            // Initialize Pathfinder
            PathFinder.myPlanetPathfinder = new PathFinder();
            // Determine initializer worker
            VecUnit startingWorkers = gc.myUnits();
            Unit optimalInitializingWorker = null;
            int maxConnectedComponentSize = Integer.MIN_VALUE;
            for (int i = 0; i < startingWorkers.size(); ++i) {
                Unit worker = startingWorkers.get(i);
                MapLocation workerLoc = worker.location().mapLocation();
                int connectedComponentSize = Utils.CONNECTED_COMPONENT_SIZES.get(
                        Utils.CONNECTED_COMPONENTS[workerLoc.getY()][workerLoc.getX()]);
                if (connectedComponentSize > maxConnectedComponentSize) {
                    optimalInitializingWorker = worker;
                    maxConnectedComponentSize = connectedComponentSize;
                }
            }
            initializingWorkerId = optimalInitializingWorker.id();
        }

        while (true) {
            System.out.println("Current round: " + gc.round());
            try{
                // Don't trigger timeout!!
                // We want to stay alive for as long as possible, in case the opponent times out
                // and we can steal the victory
                int timeLeft = gc.getTimeLeftMs();
                if (timeLeft < 300) {
                    System.out.println("Time Left: " + timeLeft + ", skipping turn...");
                    gc.nextTurn();
                    continue;
                }
                //Update centroids all at once to avoid units updating one enemy multiple times
                VecUnit units = gc.units();
                int numUnits = (int) units.size();
                for (int i=0; i<numUnits; i++) {
                    Unit unit = units.get(i);
                    if (unit.team() == Utils.TEAM) continue;
                    Location loc = unit.location();
                    if (!loc.isOnMap()) continue;
                    MapLocation mapLoc = loc.mapLocation();
                    tman.updateCentroids(mapLoc.getX(), mapLoc.getY());
                }
                if (Utils.gc.round() % 10 == 0) System.out.println(Arrays.toString(Knight.tman.centroids));
                AUnit.initTurn();
                AUnit.doTurn();

            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) { // uh oh ¯\_(ツ)_/¯
                e.printStackTrace();
            }
            gc.nextTurn();
        }
    }


}