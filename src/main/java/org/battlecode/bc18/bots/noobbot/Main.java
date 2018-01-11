package org.battlecode.bc18.bots.noobbot;

import bc.*;

import java.util.HashMap;

public class Main {

    public static final HashMap<Integer, Bot> bots = new HashMap<>();
    public static GameController gc;

    public static void main(String[] args) {
        /*
        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
        System.out.println("loc x: "+loc.getX());

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));
        */

        // Connect to the manager, starting the game
        gc = new GameController();

        while (true) {
            System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                try { //Avoid breaking the loop leading to instant loss
                    Unit unit = units.get(i);
                    UnitType type = unit.unitType();
                    int id = unit.id();
                    Bot bot = bots.get(id);
                    if (bot == null) {
                        bot = makeBot(unit);
                        bots.put(id, bot);
                    }
                    bot.act();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private static Bot makeBot(Unit unit) throws Exception {
        UnitType type = unit.unitType();
        switch(type) {
            case Worker:
                return new Worker(unit);
            case Knight:
                return new Knight(unit);
            case Ranger:
                return new Ranger(unit);
            case Mage:
                return new Mage(unit);
            case Healer:
                return new Healer(unit);
            case Factory:
                return new Factory(unit);
            case Rocket:
                return new Rocket(unit);
            default:
                throw new Exception("Undefined UnitType!"); //Should never happen
        }
    }
}