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
            ArrayList<Integer> deadUnits = new ArrayList<>();
            ArrayList<MyUnit> aliveUnits = new ArrayList<>();

            MyUnit.units.forEach((id, unit) -> {
                if (gc.canSenseUnit(id)) aliveUnits.add(unit);
                else deadUnits.add(id);
            });

            //Deal with dead units
            for (Integer deadUnitId : deadUnits) {
                try {
                    MyUnit.removeUnit(deadUnitId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Call act() for alive units
            for (MyUnit unit : aliveUnits) {
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