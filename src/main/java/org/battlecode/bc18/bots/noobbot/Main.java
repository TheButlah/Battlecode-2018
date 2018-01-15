package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.util.Utils.gc;

import org.battlecode.bc18.PathFinding;
import org.battlecode.bc18.api.AbstractUnit;
import org.battlecode.bc18.util.Utils;

import bc.GameController;
import bc.Planet;
import bc.PlanetMap;

public class Main {

    public static void main(String[] args) {
        // Connect to the manager, starting the game
        gc = new GameController();
        //Without this, AbstractUnit cannot form new units
        AbstractUnit.init(new UnitBuilder());

        if (gc.planet() == Planet.Earth) {
            PlanetMap earthMap = gc.startingMap(Planet.Earth);
            Utils.setEarthDims(earthMap);
            PathFinding.initializeEarthPathfinder(earthMap);
        }

        while (true) {
            System.out.println("Current round: " + gc.round());
            //Perform beginning of turn logic
            try{
                AbstractUnit.initTurn();
                AbstractUnit.doTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }
            gc.nextTurn();
        }
    }


}