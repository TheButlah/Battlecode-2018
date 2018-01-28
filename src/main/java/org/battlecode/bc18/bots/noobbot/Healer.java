package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.CentroidManager.cman;

import java.util.List;

import org.battlecode.bc18.api.AHealer;
import org.battlecode.bc18.api.AUnit;
import org.battlecode.bc18.api.MyRobot;
import org.battlecode.bc18.util.Utils;
import org.battlecode.bc18.pathfinder.PathFinder;

import bc.Direction;
import bc.MapLocation;
import bc.Unit;
import bc.UnitType;

public class Healer extends AHealer {

    //private static long startTime;
    //private static long time1, time2, time3;
    private float[] macroTarget = null;
    private int macroTargetSeed;
    /**
     * Constructor for Healer.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Healer(Unit unit) {
        super(unit);
    }

    @Override
    public void act() {
        if (!isOnMap()) {
            return;
        }
        //startTime = System.currentTimeMillis();
        MapLocation myMapLoc = getMapLocation();

        // heals weakest ally in heal range, if possible. Else, moves randomly.
        MyRobot target = null;
        int healRange = getHealRange();
        int healthLeft = Integer.MAX_VALUE;
        if (getHealth() < getMaxHealth()) {
            target = this;
            healthLeft = getHealth();
        }
        for (AUnit u : fastSenseNearbyFriendlies(healRange)) {
            if (!(u instanceof MyRobot)) continue;
            int health = u.getHealth();
            if (health < u.getMaxHealth() && health < healthLeft) {
                target = (MyRobot) u;
                healthLeft = health;
            }
        }
        //time1 += (System.currentTimeMillis() - startTime);
        //println("time1: " + time1);

        if (target != null && canHeal(target)) {
            heal(target);
        }

        if (isMoveReady()) {
            //startTime = System.currentTimeMillis();
            // Update macro target
            boolean moved = false;
            macroTarget = cman.getCentroid(macroTargetSeed % cman.numCentroids());
            MapLocation macroLoc = null;
            List<Unit> nearbyEnemies = fastSenseNearbyEnemies();
            int numCloseEnemies = 0;
            double closeEnemyAvgX = 0;
            double closeEnemyAvgY = 0;
            for (int i = 0; i < nearbyEnemies.size(); ++i) {
                Unit enemy = nearbyEnemies.get(i);
                UnitType enemyType = enemy.unitType();
                if (enemyType == UnitType.Knight || enemyType == UnitType.Ranger || enemyType == UnitType.Mage) {
                    MapLocation enemyLoc = enemy.location().mapLocation();
                    ++numCloseEnemies;
                    closeEnemyAvgX += (enemyLoc.getX() - closeEnemyAvgX) / numCloseEnemies;
                    closeEnemyAvgY += (enemyLoc.getY() - closeEnemyAvgY) / numCloseEnemies;
                }
            }
            //time2 += (System.currentTimeMillis() - startTime);
            //println("time2: " + time2);
            if (numCloseEnemies != 0) {
                double deltaX = myMapLoc.getX() - closeEnemyAvgX;
                double deltaY = myMapLoc.getY() - closeEnemyAvgY;
                double angleAwayFromEnemy = Math.atan2(deltaY, deltaX);
                Direction directionAwayFromEnemy = Utils.angleToDirection(angleAwayFromEnemy);
                if (fuzzyMove(directionAwayFromEnemy) != null) {
                    moved = true;
                }
            }
            if (!moved && target != null) {
                if (fuzzyMove(myMapLoc.directionTo(target.getMapLocation())) != null) {
                    moved = true;
                }
            }
            if (!moved && hasMacroTarget()) {
                //startTime = System.currentTimeMillis();
                //If we are very close to the macro target and there are no enemies, mark it as eliminated
                macroLoc = new MapLocation(Utils.PLANET, (int) macroTarget[0], (int) macroTarget[1]);
                if (nearbyEnemies.size() == 0 && macroLoc.distanceSquaredTo(myMapLoc) <= 4) {
                    cman.markCentroidEliminated(macroTarget);
                    //Macro target location will change when eliminated so update its MapLocation
                    macroLoc = new MapLocation(Utils.PLANET, (int) macroTarget[0], (int) macroTarget[1]);
                }
                //Move towards our macro target
                PathFinder.pf.setTarget(macroLoc);
                Direction towardsEnemy = PathFinder.pf.directionToTargetFrom(myMapLoc);
                //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                if (towardsEnemy != Direction.Center && isAccessible(towardsEnemy)) {
                    move(towardsEnemy);
                    moved = true;
                }
                //time3 += (System.currentTimeMillis() - startTime);
                //println("time3: " + time3);
            }

            if (!moved) {
                //Move randomly
                int offset = Utils.rand.nextInt(Utils.dirs.length);
                for (int i = 0; i < Utils.dirs.length; i++) {
                    Direction dir = Utils.dirs[(i + offset) % Utils.dirs.length]; //Cycle through based on random offset
                    //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                    if (isAccessible(dir)) {
                        move(dir);
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void onDeath() {

    }

    private boolean hasMacroTarget() {
        return macroTarget != null && !cman.hasEliminatedAll();
    }
}
