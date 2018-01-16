package org.battlecode.bc18.bots.noobbot;

import org.battlecode.bc18.PathFinder;
import org.battlecode.bc18.api.AKnight;
import org.battlecode.bc18.util.Utils;

import bc.Direction;
import bc.MapLocation;
import bc.Unit;
import bc.UnitType;
import bc.VecUnit;

public class Knight extends AKnight {

    //static int time1, time2, time3, time4;
    //static long startTime;
    private Unit target = null; //Although this doesn't update, it will allow us to go to last seen spot.
    private int spawnTargetSeed;

    /**
     * Constructor for Knight.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Knight(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Knight;
        spawnTargetSeed = Utils.rand.nextInt(Integer.MAX_VALUE);
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

        if (hasTarget() && !Utils.gc.canSenseUnit(target.id())) {
            target = null;
        }
        if (!hasTarget() || isAttackReady() || isJavelinReady()) {
            // Get closest enemy MyUnit
            Unit closestUnit = null;
            int closestUnitDist = Integer.MAX_VALUE;

            //startTime = System.currentTimeMillis();
            // Direct API access to GameController for performance
            VecUnit nearbyEnemies = Utils.gc.senseNearbyUnitsByTeam(myMapLoc, getVisionRange(), Utils.OTHER_TEAM);
            for (int i = 0; i < nearbyEnemies.size(); ++i) {
                Unit enemy = nearbyEnemies.get(i);
                int distance = (int) enemy.location().mapLocation().distanceSquaredTo(myMapLoc);
                if (distance < closestUnitDist) {
                    closestUnit = enemy;
                    closestUnitDist = distance;
                }
            }
            if (closestUnit != null) {
                target = closestUnit;
            }
            //time1 += System.currentTimeMillis() - startTime;
            //System.out.println("time 1: " + time1);
        }

        if (isMoveReady()) {
            boolean moved = false;
            // If we have a target, move towards it
            if (hasTarget()) {
                //startTime = System.currentTimeMillis();
                MapLocation targetEnemy = target.location().mapLocation();
                int[][] distances = PathFinder.earthPathfinder.search(targetEnemy.getY(), targetEnemy.getX());
                Direction towardsEnemy = PathFinder.directionToDestination(distances, myMapLoc);
                //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                if (towardsEnemy != Direction.Center && isAccessible(towardsEnemy)) {
                    move(towardsEnemy);
                    moved = true;
                }
                //time2 += System.currentTimeMillis() - startTime;
                //System.out.println("time 2: " + time2);
            }
            else if (Main.enemySpawns.size() != 0) { // Otherwise, we move towards the enemy spawn
                //startTime = System.currentTimeMillis();
                // TODO: this assumes the unit is on earth
                MapLocation spawnTarget = Main.enemySpawns.get(spawnTargetSeed % Main.enemySpawns.size());
                if (spawnTarget.equals(myMapLoc)) {
                    // We are already at the enemy spawn and we see no enemies, so we will
                    // conclude that the enemy spawn has been eliminated
                    Main.enemySpawns.remove(spawnTarget);
                    // Get next spawn target
                    if (Main.enemySpawns.size() != 0) {
                        Main.enemySpawns.get(spawnTargetSeed % Main.enemySpawns.size());
                    }
                }
                if (Utils.gc.round() > 50 && spawnTarget != null) {
                    int[][] distances = PathFinder.earthPathfinder.search(spawnTarget.getY(), spawnTarget.getX());
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
}
