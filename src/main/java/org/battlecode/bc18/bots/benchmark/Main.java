package org.battlecode.bc18.bots.benchmark;

import bc.*;
import org.battlecode.bc18.api.AUnit;
import org.battlecode.bc18.util.Utils;
import org.battlecode.bc18.pathfinder.PathFinder;

import java.util.ArrayList;

import static org.battlecode.bc18.util.Utils.gc;
import static org.battlecode.bc18.pathfinder.PathFinder.pf;

public class Main {

    public static void main(String[] args) {
        System.out.println("INITIALIZING SERVER BENCHMARK");

        // Initialize Pathfinder
        PathFinder.pf = new PathFinder();
        VecUnit startingUnits = Utils.EARTH_START.getInitial_units();
        int numUnits = (int) startingUnits.size();
        ArrayList<MapLocation> enemies = new ArrayList<>(numUnits);
        ArrayList<MapLocation> friends = new ArrayList<>(numUnits);
        for (int i=0; i<numUnits; i++) {
            Unit unit = startingUnits.get(i);
            MapLocation loc = unit.location().mapLocation();
            if (unit.team() == Utils.TEAM) friends.add(loc);
            else enemies.add(loc);
        }

        MapLocation target = enemies.get(0);
        MapLocation start = friends.get(0);

        //setTarget warm up
        System.out.println("setTarget warmup");
        boolean alwaysComputed = true;
        pf.resetCache();
        for (int i=0; i<5_000; i++) {
            alwaysComputed &= pf.setTarget(target);
            pf.resetCache();
        }

        //setTarget test
        System.out.println("setTarget test");
        int cycles = 10_000;
        alwaysComputed = true;
        long startTime = System.nanoTime();
        for (int i=0; i<cycles; i++) {
            alwaysComputed &= pf.setTarget(target);
            pf.resetCache();
        }
        long finishTime = System.nanoTime();
        double frequency = cycles * 1.0E9 /(finishTime - startTime);
        double period = (finishTime - startTime) * 1.0E-6 / cycles;
        System.out.println("Always computed: " + alwaysComputed);
        System.out.printf("setTarget frequency: %f, period(ms): %f\n", frequency, period);

        //search warm up
        System.out.println("search warmup");
        pf.resetCache();
        pf.setTarget(target);
        for (int i=0; i<5_000; i++) {
            pf.directionToTargetFrom(start);
        }

        //setTarget test
        System.out.println("search test");
        cycles = 10_000;
        pf.resetCache();
        pf.setTarget(target);
        startTime = System.nanoTime();
        for (int i=0; i<cycles; i++) {
            pf.directionToTargetFrom(start);
        }
        finishTime = System.nanoTime();
        frequency = cycles * 1.0E9 /(finishTime - startTime);
        period = (finishTime - startTime) * 1.0E-6 / cycles;
        System.out.printf("search frequency: %f, period(ms): %f\n", frequency, period);
        if (true) return;




        while (true) {
            long round = gc.round();
            System.out.println("Current round: " + round);
            try{

                // Don't trigger timeout!!
                // We want to stay alive for as long as possible, in case the opponent times out
                // and we can steal the victory
                int timeLeft = gc.getTimeLeftMs();
                if (round < 995 && timeLeft < AUnit.getNumUnits() * 6) {
                    System.out.println("Time Left: " + timeLeft + ", skipping turn...");
                    gc.nextTurn();
                    continue;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            /*
            if (round % 100 == 0) {
                System.gc();
            }*/ //missing for benchmark purposes
            gc.nextTurn();
        }
    }


}