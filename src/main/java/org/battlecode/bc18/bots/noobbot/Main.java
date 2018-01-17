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
    public static ArrayList<MapLocation> enemySpawns;

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
            PathFinder.myPlanetPathfinder = new PathFinder(Utils.EARTH_START);
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
                //System.out.println(Arrays.deepToString(tman.centroids) + "\n");
                AUnit.initTurn();
                AUnit.doTurn();

            } catch (Exception e) {
                e.printStackTrace();
            }
            gc.nextTurn();
        }
    }


}