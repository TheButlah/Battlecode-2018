package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.CentroidManager.cman;
import static org.battlecode.bc18.util.Utils.gc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bc.*;
import org.battlecode.bc18.ChokeManager;
import org.battlecode.bc18.pathfinder.Cell;
import org.battlecode.bc18.pathfinder.PathFinder;
import org.battlecode.bc18.api.AUnit;
import org.battlecode.bc18.util.Utils;

public class Main {
    public static int initializingWorkerId;

    public static void main(String[] args) {
        System.out.println("INITIALIZING MUSK THE DESTROYER - \"MEME MACHINE\" CLASS.\n" +
                           "YOUR INFERIOR PROGRAMMING WILL BE TERMINATED.");

        //Without this, MyUnit cannot form new units
        AUnit.init(new UnitBuilder());

        //gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Healer);
        //gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Rocket);
        //gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Knight);
        gc.queueResearch(UnitType.Healer);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Worker);

        // Initialize Pathfinder
        PathFinder.pf = new PathFinder();

        if (gc.planet() == Planet.Earth) {
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

            ArrayList<Unit> enemies = new ArrayList<>(3);
            ArrayList<Unit> friendlies = new ArrayList<>(3);
            VecUnit units = Utils.EARTH_START.getInitial_units();
            for (int i=0; i<units.size(); i++) {
                Unit u = units.get(i);
                if (u.team() == Utils.TEAM) friendlies.add(u);
                else enemies.add(u);
            }
            MapLocation enemyLoc = enemies.get(0).location().mapLocation();
            MapLocation myLoc = friendlies.get(0).location().mapLocation();
            List<MapLocation> chokes = ChokeManager.chman.computeChokesBetweenPoints(enemyLoc, myLoc);


        }

        while (true) {
            long round = gc.round();
            System.out.println("Current round: " + round);
            try{
                // Don't trigger timeout!!
                // We want to stay alive for as long as possible, in case the opponent times out
                // and we can steal the victory
                int timeLeft = gc.getTimeLeftMs();
                if (round < 995 && timeLeft < AUnit.getNumUnits() * 6) {
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
                    cman.updateCentroids(mapLoc.getX(), mapLoc.getY());
                }
                if (round % 10 == 0) System.out.println(Arrays.deepToString(cman.centroids));
                AUnit.doTurn();

            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) { // uh oh ¯\_(ツ)_/¯
                e.printStackTrace();
            }
            if (round % 100 == 0) {
                System.gc();
            }
            gc.nextTurn();
        }
    }


}