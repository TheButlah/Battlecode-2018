package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.TargetManager.tman;

import java.util.ArrayList;
import java.util.List;

import org.battlecode.bc18.api.ARanger;
import org.battlecode.bc18.util.Utils;
import org.battlecode.bc18.util.pathfinder.PathFinder;

import bc.Direction;
import bc.MapLocation;
import bc.Unit;
import bc.UnitType;

public class Ranger extends ARanger {


    //private static long startTime;
    //private static long time1, time2, time3, time4, time5;
    private static short MAX_TURNS_STUCK = 5;

    private int turnsMissingEnemies = 0;
    private static final int MAX_TURNS_MISSING_ENEMIES = 10;
    /** The number of turns we have been unable to move */
    private short turnsStuck = 0;

    private Unit target = null; //Although this doesn't update, it will allow us to go to last seen spot.
    /** The macro-strategy (long-term) target. First index is x, second is y. If null, no target. */
    private float[] macroTarget = null;
    private int macroTargetSeed;

    /**
     * Constructor for Ranger.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected Ranger(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Ranger;
        macroTargetSeed = Utils.rand.nextInt(Integer.MAX_VALUE);
    }


    @Override
    public void act() {
        //we want rangers to keep targets in the middle of their range
        //move if target tries to run away/toward (maybe with 1/4 of way from inner/outer edge of target?)
        if (!isOnMap()) {
            return;
        }
        MapLocation myMapLoc = getMapLocation();
        //if (Utils.gc.round() > 400) println("MyLoc: " + myMapLoc);

        MapLocation macroLoc = null;
        //startTime = System.currentTimeMillis();
        List<Unit> nearbyEnemies = fastSenseNearbyEnemies();

        // Update macro target
        macroTarget = tman.getTarget(macroTargetSeed % tman.numTargets());
        //If we are very close to the macro target and there are no enemies, mark it as eliminated
        if (hasMacroTarget()) {
            if (nearbyEnemies.size() == 0) {
                macroLoc = new MapLocation(Utils.PLANET, (int) macroTarget[0], (int) macroTarget[1]);
                ++turnsMissingEnemies;
                int distanceToTarget = (int) macroLoc.distanceSquaredTo(myMapLoc);
                if (distanceToTarget <= 4 || (distanceToTarget <= 30 && turnsMissingEnemies >= MAX_TURNS_MISSING_ENEMIES)) {
                    turnsMissingEnemies = 0;
                    tman.markTargetEliminated(macroTarget);
                    //Macro target location will change when eliminated so update its MapLocation
                    macroLoc = new MapLocation(Utils.PLANET, (int) macroTarget[0], (int) macroTarget[1]);
                }
            }
        }
        //time1 += (System.currentTimeMillis() - startTime);
        //println("time1: " + time1);

        //Drop targets we can't sense
        if (hasTarget() && !Utils.gc.canSenseUnit(target.id())) {
            target = null;
        }

        if (!hasTarget() || isAttackReady() || isSnipeReady()) {
            //startTime = System.currentTimeMillis();
            // Direct API access to GameController for performance
            //Find nearest unit, prioritize all but workers
            //target = Utils.getNearest(nearbyEnemies, myMapLoc, u -> u.unitType() != UnitType.Worker);
            // TODO: Workers don't necessarily need to target the nearest enemy!
            ArrayList<Unit> canAttack = new ArrayList<>();
            for (Unit u : nearbyEnemies) {
                if (u.location().mapLocation().distanceSquaredTo(myMapLoc) > getCannotAttackRange()) {
                    canAttack.add(u);
                }
            }
            target = Utils.getNearest(canAttack, myMapLoc, u -> u.unitType() == UnitType.Rocket);
            //time2 += (System.currentTimeMillis() - startTime);
            //println("time2: " + time2);
        }

        if (isMoveReady()) {
            //println("move ready");
            boolean moved = false;
            // If we have a target, check and fix spacing
            if (hasTarget()) {
                //println("target: " + target);
                //startTime = System.currentTimeMillis();
                MapLocation targetLoc = target.location().mapLocation();
                if (this.isTargetKindaFar(this.target)) {
                    PathFinder.pf.setTarget(targetLoc);
                    Direction towardsEnemy = PathFinder.pf.directionToTargetFrom(myMapLoc);
                    //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                    if (towardsEnemy != Direction.Center && isAccessible(towardsEnemy)) {
                        move(towardsEnemy);
                        moved = true;
                    }
                }
                else if (this.isTargetKindaClose(this.target)) {
                    Direction awayEnemy = targetLoc.directionTo(myMapLoc);
                    if (awayEnemy != Direction.Center && isAccessible(awayEnemy)) {
                        if (fuzzyMove(awayEnemy) != null) {
                            moved = true;
                        }
                    }
                }
                else {
                    if (movePerpendicular(myMapLoc.directionTo(targetLoc)) != null) {
                        moved = true;
                    }
                }
                //time3 += System.currentTimeMillis() - startTime;
                //System.out.println("time 3: " + time3);
            } else if (hasMacroTarget()) {
                //println("macroLoc: " + macroLoc);
                //startTime = System.currentTimeMillis();
                //Attack our macro target
                PathFinder.pf.setTarget(macroLoc);
                Direction towardsEnemy = PathFinder.pf.directionToTargetFrom(myMapLoc);
                //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                if (towardsEnemy != Direction.Center && isAccessible(towardsEnemy)) {
                    move(towardsEnemy);
                    moved = true;
                }
                //time4 += System.currentTimeMillis() - startTime;
                //System.out.println("time 4: " + time4);
            }
            // If we haven't yet moved and don't have a target, move randomly
            if (!moved && !hasTarget()) {
                //println("Moving randomly");
                //startTime = System.currentTimeMillis();
                int offset = Utils.rand.nextInt(Utils.dirs.length);
                for (int i = 0; i < Utils.dirs.length; i++) {
                    Direction dir = Utils.dirs[(i + offset) % Utils.dirs.length]; //Cycle through based on random offset
                    //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                    if (isAccessible(dir)) {
                        //println("Moving");
                        move(dir);
                        moved = true;
                        break;
                    }
                }
                //time5 += System.currentTimeMillis() - startTime;
                //System.out.println("time 5: " + time5);
            }
            if (!moved) {
                //println("not yet moved");
                turnsStuck++;
                if (turnsStuck >= MAX_TURNS_STUCK){
                    //TODO: Tell pathfinders with different macro targets to update weights
                    println("Stuck");
                    // Move randomly
                    turnsStuck = 0;
                    int offset = Utils.rand.nextInt(Utils.dirs.length);
                    for (int i = 0; i < Utils.dirs.length; i++) {
                        Direction dir = Utils.dirs[(i + offset) % Utils.dirs.length]; //Cycle through based on random offset
                        //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                        if (isAccessible(dir)) {
                            //println("Moving");
                            move(dir);
                            moved = true;
                            break;
                        }
                    }
                }
            }
        }
        else {
            //println("move is not ready");
        }

        if (hasTarget() && (isAttackReady() || isSnipeReady())) {
            // if we can attack the target, attack, with snipe as backup
            if (canAttack(target)) {
                attack(target);
            } else if (canSnipe(target)){
                snipe(target);
            }
        }


    }

    @Override
    protected void onDeath() {

    }

    private boolean hasTarget() {
        return target != null;
    }

    private boolean hasMacroTarget() {
        return macroTarget != null && !tman.hasEliminatedAll();
    }
}
