package org.battlecode.bc18.bots.noobbot;

import java.util.List;

import org.battlecode.bc18.PathFinding;
import org.battlecode.bc18.api.AKnight;
import org.battlecode.bc18.util.Utils;

import bc.Direction;
import bc.MapLocation;
import bc.Unit;
import bc.UnitType;

public class Knight extends AKnight {

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
        // Get closest enemy MyUnit
        Unit closestUnit = null;
        int closestUnitDist = Integer.MAX_VALUE;

        List<Unit> nearbyEnemies = senseNearbyEnemies();
        for (Unit enemy : nearbyEnemies) {
            int distance = (int) enemy.location().mapLocation().distanceSquaredTo(myMapLoc);
            if (distance < closestUnitDist) {
                closestUnit = enemy;
                closestUnitDist = distance;
            }
        }
        if (closestUnit != null) {
            target = closestUnit;
        }

        if (isMoveReady()) {
            boolean moved = false;
            // If we have a target, move towards it
            if (hasTarget()) {
                MapLocation targetEnemy = target.location().mapLocation();
                int[][] distances = PathFinding.earthPathfinder.search(targetEnemy.getY(), targetEnemy.getX());
                Direction towardsEnemy = PathFinding.moveDirectionToDestination(distances, myMapLoc);
                //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                if (towardsEnemy != Direction.Center && isAccessible(towardsEnemy)) {
                    move(towardsEnemy);
                    moved = true;
                }
            }
            else if (Main.enemySpawns.size() != 0) { // Otherwise, we move towards the enemy spawn
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
                    int[][] distances = PathFinding.earthPathfinder.search(spawnTarget.getY(), spawnTarget.getX());
                    Direction towardsEnemy = PathFinding.moveDirectionToDestination(distances, myMapLoc);
                    //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                    if (towardsEnemy != Direction.Center && isAccessible(towardsEnemy)) {
                        move(towardsEnemy);
                        moved = true;
                    }
                }
            }
            // If we haven't yet moved, move randomly
            if (!moved) {
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
            }
        }

        if (hasTarget()) {
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
