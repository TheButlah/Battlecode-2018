package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.bots.util.Utils.gc;

import java.util.ArrayList;

import org.battlecode.bc18.PathFinding;
import org.battlecode.bc18.bots.noobbot.units.MyUnit;
import org.battlecode.bc18.bots.util.Utils;

import bc.GameController;
import bc.Planet;
import bc.PlanetMap;

public class Main {

    public static void main(String[] args) {
        // Connect to the manager, starting the game
        gc = new GameController();
        if (gc.planet() == Planet.Earth) {
            PlanetMap earthMap = gc.startingMap(Planet.Earth);
            Utils.setEarthDims(earthMap);
            PathFinding.initializeEarthPathfinder(earthMap);
        }

        while (true) {
            System.out.println("Current round: " + gc.round());
            MyUnit.initTurn();

            //Perform act on all the units and catch any exception to prevent instant loss
            MyUnit.units.forEach((id, unit) -> {
                try {
                    unit.act();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });

            gc.nextTurn();
        }
    }


}