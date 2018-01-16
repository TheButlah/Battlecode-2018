package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.util.Utils.gc;

import java.util.ArrayList;

import org.battlecode.bc18.PathFinder;
import org.battlecode.bc18.TargetManager;
import org.battlecode.bc18.api.AUnit;
import org.battlecode.bc18.util.Utils;

import bc.MapLocation;
import bc.Planet;
import bc.PlanetMap;
import bc.Unit;
import bc.UnitType;
import bc.VecUnit;

public class Main {
    public static ArrayList<MapLocation> enemySpawns;

    public static void main(String[] args) {
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
            // Get enemy spawns
            enemySpawns = new ArrayList<>(3);
            PlanetMap initialMap = Utils.EARTH_START;
            VecUnit initialUnits = initialMap.getInitial_units();
            TargetManager.tman = new TargetManager(initialUnits);
            for (int i = 0; i < initialUnits.size(); ++i) {
                Unit unit = initialUnits.get(i);
                if (unit.team() != Utils.TEAM) {
                    enemySpawns.add(unit.location().mapLocation());
                }
            }

            // Initialize Pathfinder
            PathFinder.earthPathfinder = new PathFinder(Utils.EARTH_START);
        }

        while (true) {
            System.out.println("Current round: " + gc.round());
            try{
                //System.out.println(gc.getTimeLeftMs());
                if (gc.getTimeLeftMs() > 300) {
                    // Don't trigger timeout!!
                    // We want to stay alive for as long as possible, in case the opponent times out
                    // and we can steal the victory
                    AUnit.initTurn();
                    AUnit.doTurn();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gc.nextTurn();
        }
    }


}