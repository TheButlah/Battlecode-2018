package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.PathFinder;
import org.battlecode.bc18.TargetManager;
import org.battlecode.bc18.api.ARanger;
import org.battlecode.bc18.util.Utils;

public class Ranger extends ARanger {



    private static short MAX_TURNS_STUCK = 5;

    static TargetManager tman;
    static {
        PlanetMap myMap = (Utils.PLANET == Planet.Earth) ? Utils.EARTH_START : Utils.MARS_START;
        tman = new TargetManager(myMap.getInitial_units(), 3);
    }

    /** The number of turns we have been unable to move */
    private short turnsStuck = 0;

    //static int time1, time2, time3, time4;
    //static long startTime;
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

        MapLocation myMapLoc = getMapLocation();

        MapLocation macroLoc = null;
        VecUnit nearbyEnemies = Utils.gc.senseNearbyUnitsByTeam(myMapLoc, getVisionRange(), Utils.OTHER_TEAM);

        // Update macro target
        macroTarget = tman.getTarget(macroTargetSeed % tman.numTargets());
        //If we are very close to the macro target and there are no enemies, mark it as eliminated
        if (hasMacroTarget()) {
            macroLoc = new MapLocation(Utils.PLANET, (int) macroTarget[0], (int) macroTarget[1]);
            if (nearbyEnemies.size() == 0 && macroLoc.distanceSquaredTo(myMapLoc) <= 4) {
                tman.markTargetEliminated(macroTarget);
                //Macro target location will change when eliminated so update its MapLocation
                macroLoc = new MapLocation(Utils.PLANET, (int) macroTarget[0], (int) macroTarget[1]);
            }
        }

        //Drop targets we can't sense
        if (hasTarget() && !Utils.gc.canSenseUnit(target.id())) {
            target = null;
        }

        if (!hasTarget() || isAttackReady() || isSnipeReady()) {
            //startTime = System.currentTimeMillis();
            // Direct API access to GameController for performance
            //Find nearest unit, prioritize all but workers
            //target = Utils.getNearest(nearbyEnemies, myMapLoc, u -> u.unitType() != UnitType.Worker);
            // TODO: we probably need a finer ranking system rather than just excluding workers
            target = Utils.getNearest(nearbyEnemies, myMapLoc);
            //time1 += System.currentTimeMillis() - startTime;
            //System.out.println("time 1: " + time1);
        }

        if (isMoveReady()) {
            boolean moved = false;
            // If we have a target, check and fix spacing
            if (hasTarget()) {
                MapLocation targetLoc = target.location().mapLocation();
                int[][] distances = PathFinder.myPlanetPathfinder.search(targetLoc.getY(), targetLoc.getX());
                Direction towardsEnemy = PathFinder.directionToDestination(distances, myMapLoc);
                //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                if (this.isTargetKindaFar(this.target)) {
                    if (towardsEnemy != Direction.Center && isAccessible(towardsEnemy)) {
                        move(towardsEnemy);
                        moved = true;
                    }
                }
                else if (this.isTargetKindaClose(this.target)) {
                    Direction awayEnemy;
                    //pls ignore
                    switch (towardsEnemy){
                        case East:
                            awayEnemy = Direction.West;
                            return;
                        case West:
                            awayEnemy = Direction.East;
                            return;
                        case North:
                            awayEnemy = Direction.North;
                            return;
                        case South:
                            awayEnemy = Direction.North;
                            return;
                        case Northeast:
                            awayEnemy = Direction.Southwest;
                            return;
                        case Northwest:
                            awayEnemy = Direction.Southeast;
                            return;
                        case Southeast:
                            awayEnemy = Direction.Northwest;
                            return;
                        case Southwest:
                            awayEnemy = Direction.Northeast;
                            return;
                        default:
                            awayEnemy = Direction.Center;
                    }
                    if (awayEnemy != Direction.Center && isAccessible(awayEnemy)) {
                        move(awayEnemy);
                        moved = true;
                    }
                }
                else {
                    moved = true; //bc we're in a good place
                }
                //time2 += System.currentTimeMillis() - startTime;
                //System.out.println("time 2: " + time2);
            } else if (hasMacroTarget()) {
                //Attack our macro target
                int[][] distances = PathFinder.myPlanetPathfinder.search(macroLoc.getY(), macroLoc.getX());
                Direction towardsEnemy = PathFinder.directionToDestination(distances, myMapLoc);
                //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                if (towardsEnemy != Direction.Center && isAccessible(towardsEnemy)) {
                    move(towardsEnemy);
                    moved = true;
                }
                //time3 += System.currentTimeMillis() - startTime;
                //System.out.println("time 3: " + time3);
            }
            // If we haven't yet moved and don't have a target, move randomly
            if (!moved && !hasTarget()) {
                //startTime = System.currentTimeMillis();
                int offset = Utils.rand.nextInt(Utils.dirs.length);
                for (int i = 0; i < Utils.dirs.length; i++) {
                    Direction dir = Utils.dirs[(i + offset) % Utils.dirs.length]; //Cycle through based on random offset
                    //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                    if (isAccessible(dir)) {
                        //println("Moving");
                        move(dir);
                        break;
                    }
                }
                //time4 += System.currentTimeMillis() - startTime;
                //System.out.println("time 4: " + time4);
            }
            if (!moved) {
                turnsStuck++;
                if (turnsStuck >= MAX_TURNS_STUCK){
                    //TODO: Tell pathfinders with different macro targets to update weights
                }
            }
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
