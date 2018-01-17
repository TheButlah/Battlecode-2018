package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.PathFinder;
import org.battlecode.bc18.TargetManager;
import org.battlecode.bc18.api.AKnight;
import org.battlecode.bc18.util.Utils;

public class Knight extends AKnight {

    static TargetManager tman;
    static {
        PlanetMap myMap = (Utils.PLANET == Planet.Earth) ? Utils.EARTH_START : Utils.MARS_START;
        tman = new TargetManager(myMap.getInitial_units(), 3);
    }
    private MapLocation lastLoc = null;

    //static int time1, time2, time3, time4;
    //static long startTime;
    private Unit target = null; //Although this doesn't update, it will allow us to go to last seen spot.
    /** The macro-strategy (long-term) target. First index is x, second is y. If null, no target. */
    private float[] macroTarget = null;

    /**
     * Constructor for Knight.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Knight(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Knight;
        //If needed, assign this based on some rule - for example,
        //groups with fewer members get priority.
        macroTarget = tman.getTarget(Utils.rand.nextInt(tman.numTargets()));
    }

    @Override
    public void act() {
        // check if enemy unit in sight
        // if so, move towards it or attack it
        // else, move randomly

        //MyUnit myUnit = getAsUnit();
        if (!isOnMap()) {
            //"TODO: handle knight in space
            return;
        }

        MapLocation myMapLoc = getMapLocation();
        MapLocation macroLoc = null;
        VecUnit nearbyEnemies = Utils.gc.senseNearbyUnitsByTeam(myMapLoc, getVisionRange(), Utils.OTHER_TEAM);

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

        if (!hasTarget() || isAttackReady() || isJavelinReady()) {
            //startTime = System.currentTimeMillis();
            // Direct API access to GameController for performance
            //Find nearest unit, prioritize all but workers
            target = Utils.getNearest(nearbyEnemies, myMapLoc, u -> u.unitType() != UnitType.Worker);
            //time1 += System.currentTimeMillis() - startTime;
            //System.out.println("time 1: " + time1);
        }

        if (isMoveReady()) {
            boolean moved = false;
            // If we have a target, move towards it
            if (hasTarget()) {
                MapLocation targetLoc = target.location().mapLocation();
                int[][] distances = PathFinder.myPlanetPathfinder.search(targetLoc.getY(), targetLoc.getX());
                Direction towardsEnemy = PathFinder.directionToDestination(distances, myMapLoc);
                //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                if (towardsEnemy != Direction.Center && isAccessible(towardsEnemy)) {
                    move(towardsEnemy);
                    moved = true;
                }
                //time2 += System.currentTimeMillis() - startTime;
                //System.out.println("time 2: " + time2);
            } else if (hasMacroTarget()) {
                //Attack our macro target
                if (Utils.gc.round() > 50 && macroTarget != null) {
                    int[][] distances = PathFinder.myPlanetPathfinder.search(macroLoc.getY(), macroLoc.getX());
                    Direction towardsEnemy = PathFinder.directionToDestination(distances, myMapLoc);
                    //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                    if (towardsEnemy != Direction.Center && isAccessible(towardsEnemy)) {
                        move(towardsEnemy);
                        moved = true;
                    }
                }
                //time3 += System.currentTimeMillis() - startTime;
                //System.out.println("time 3: " + time3);
            }
            // If we haven't yet moved, move randomly
            if (!moved) {
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
        }

        if (hasTarget() && (isAttackReady() || isJavelinReady())) {
            // if we can attack the target, attack, with javelin as backup
            if (canAttack(target)) {
                attack(target);
            } else if (canJavelin(target)){
                javelin(target);
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
