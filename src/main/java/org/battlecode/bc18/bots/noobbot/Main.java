package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.util.Utils.gc;

import java.util.ArrayList;

import bc.*;
import org.battlecode.bc18.PathFinder;
import org.battlecode.bc18.TargetManager;
import org.battlecode.bc18.api.AUnit;
import org.battlecode.bc18.util.Utils;

public class Main {
    public static ArrayList<MapLocation> enemySpawns;

    public static void main(String[] args) {
        System.out.println("INITIALIZING MUSK THE DESTROYER - CLASS \"MEME MACHINE\".");
        System.out.println("YOU DO NOT KNOW DA WAE. YOUR INFERIOR PROGRAMMING WILL BE TERMINATED.");

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
            PathFinder.myPlanetPathfinder = new PathFinder(Utils.EARTH_START);
        }

        boolean hitTimeLimit = false;
        while (true) {
            System.out.println("Current round: " + gc.round());
            try{
                // Don't trigger timeout!!
                // We want to stay alive for as long as possible, in case the opponent times out
                // and we can steal the victory
                int timeLeft = gc.getTimeLeftMs();
                if (timeLeft < 300 && !hitTimeLimit) {
                    System.out.println("Time Left: " + timeLeft + ", skipping turn...");
                    hitTimeLimit = true;
                    continue;
                }
                //Update centroids
                VecUnit units = gc.units();
                int numUnits = (int) units.size();
                //ArrayList<Unit> enemies = new ArrayList<>(numUnits/2);
                for (int i=0; i<numUnits; i++) {
                    Unit unit = units.get(i);
                    if (unit.team() == Utils.OTHER_TEAM) {
                        Location loc = unit.location();
                        if (!loc.isOnMap()) continue;
                        MapLocation mapLoc = loc.mapLocation();
                        Knight.tman.updateCentroids(mapLoc.getX(), mapLoc.getY());
                    }
                }
                AUnit.initTurn();
                AUnit.doTurn();

            } catch (Exception e) {
                e.printStackTrace();
            }
            gc.nextTurn();
        }
    }


}