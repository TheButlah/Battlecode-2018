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
            //Perform beginning of turn logic
            try{
                MyUnit.initTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Deal with dead units
            for (Integer deadUnitId : MyUnit.deadUnits) {
                try {
                    MyUnit.removeUnit(deadUnitId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Call act() for alive units
            for (MyUnit unit : MyUnit.aliveUnits) {
                try { //Avoid breaking the loop leading to instant loss
                    unit.act();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            gc.nextTurn();
        }
    }


}