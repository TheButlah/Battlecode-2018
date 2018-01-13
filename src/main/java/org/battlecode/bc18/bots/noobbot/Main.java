package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.bots.util.Utils.gc;

import org.battlecode.bc18.PathFinding;

import bc.GameController;
import bc.Planet;
import bc.PlanetMap;
import org.battlecode.bc18.bots.noobbot.units.MyUnit;
import org.battlecode.bc18.bots.util.Utils;

public class Main {

    public static void main(String[] args) {
        // Connect to the manager, starting the game
        gc = new GameController();
        PlanetMap earthMap = gc.startingMap(Planet.Earth);
        Utils.setEarthDims(earthMap);
        PathFinding.initializeEarthPathfinder(earthMap);

        while (true) {
            System.out.println("Current round: " + gc.round());
            //Loop through all units. We must use `forEach()` because the underlying map is being modified.
            MyUnit.units.forEach((id, unit) -> {
                try { //Avoid breaking the loop leading to instant loss
                    unit.act();
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
            gc.nextTurn();
        }
    }


}