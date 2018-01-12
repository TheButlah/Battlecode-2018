package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.Utils.gc;

import org.battlecode.bc18.PathFinding;
import org.battlecode.bc18.Utils;

import bc.GameController;
import bc.Planet;
import bc.PlanetMap;
import bc.Unit;
import bc.UnitType;
import bc.VecUnit;

public class Main {

    public static void main(String[] args) {
        // Connect to the manager, starting the game
        gc = new GameController();
        PlanetMap earthMap = gc.startingMap(Planet.Earth);
        Utils.setEarthDims(earthMap);
        PathFinding.initializeEarthPathfinder(earthMap);

        while (true) {
            System.out.println("Current round: " + gc.round());
            //System.out.println(earthPathfinder.search(0, 0, (int)earthMap.getHeight() - 1, (int)earthMap.getWidth() - 1));
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                try { //Avoid breaking the loop leading to instant loss
                    Unit unit = units.get(i);  //Dont try to hold a reference to this, it
                    UnitType type = unit.unitType();
                    int id = unit.id();
                    Bot bot = Utils.bots.get(id);
                    if (bot == null) {
                        bot = makeBot(unit);
                        Utils.bots.put(id, bot);
                    }
                    bot.act();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            gc.nextTurn();
        }
    }

    /**
     * Constructs a Bot object based off of a Unit.
     * @exception RuntimeException Occurs when `unit.unitType()` is not recognized.
     */
    private static Bot makeBot(Unit unit) throws RuntimeException {
        UnitType type = unit.unitType();
        int id = unit.id();
        switch(type) {
            case Worker:
                return new Worker(id);
            case Knight:
                return new Knight(id);
            case Ranger:
                return new Ranger(id);
            case Mage:
                return new Mage(id);
            case Healer:
                return new Healer(id);
            case Factory:
                return new Factory(id);
            case Rocket:
                return new Rocket(id);
            default:
                throw new RuntimeException("Unrecognized UnitType!"); //Should never happen
        }
    }
}