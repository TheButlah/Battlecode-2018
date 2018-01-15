package org.battlecode.bc18.bots.noobbot;

import bc.Direction;
import bc.MapLocation;
import bc.Unit;
import bc.UnitType;
import org.battlecode.bc18.PathFinding;
import org.battlecode.bc18.api.AKnight;
import org.battlecode.bc18.util.Utils;

import java.util.List;

public class Knight extends AKnight {

    private Unit target = null; //Although this doesn't update, it will allow us to go to last seen spot.

    /**
     * Constructor for Knight.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    Knight(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Knight;
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
            this.target = closestUnit;
        }

        // if we don't have a target, try to move randomly, return if can't
        if (!hasTarget()) {
            if (isMoveReady()) {
                //Move randomly
                int offset = Utils.rand.nextInt(Utils.dirs.length);
                for (int i = 0; i < Utils.dirs.length; i++) {
                    Direction dir = Utils.dirs[(i + offset) % Utils.dirs.length]; //Cycle through based on random offset
                    //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
                    if (isAccessible(dir)) {
                        //println("Moving");
                        move(dir);
                        return;
                    }
                }
            }
            // can't move and no target so return
            return;
        }

        // We do have a target after all. Can we get to them?
        if (isMoveReady()) {
            MapLocation targetEnemy = this.target.location().mapLocation();
            int[][] distances = PathFinding.earthPathfinder.search(targetEnemy.getY(), targetEnemy.getX());
            Direction towardsEnemy = PathFinding.moveDirectionToDestination(distances, myMapLoc.getY(), myMapLoc.getX(), myMapLoc.getPlanet());
            //Already did `isMoveReady()` so instead of doing `canMove()` we just do `isAccessible()`
            if (isAccessible(towardsEnemy)) {
                move(towardsEnemy);
            }
        }

        // if we can attack the target, attack, with javelin as backup
        if (canAttack(target)) {
            attack(target);
        } else if (canJavelin(target)){
            javelin(target);
        }
    }

    @Override
    protected void onDeath() {

    }

    private boolean hasTarget() {
        return this.target != null;
    }
}
