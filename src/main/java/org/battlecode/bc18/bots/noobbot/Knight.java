package org.battlecode.bc18.bots.noobbot;

import bc.Direction;
import bc.MapLocation;
import bc.Unit;
import bc.UnitType;
import org.battlecode.bc18.PathFinding;
import org.battlecode.bc18.api.AbstractKnight;
import org.battlecode.bc18.util.Utils;

import java.util.List;

import static org.battlecode.bc18.util.Utils.gc;

public class Knight extends AbstractKnight {

    private Unit target = null; //Although this doesn't update, it will allow us to go to last seen spot.

    /**
     * Constructor for AbstractKnight.
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

        //Unit myUnit = getAsUnit();
        if (!isOnMap()) {
            println("TODO: handle knight in space");
            return;
        }

        MapLocation myMapLoc = getMapLocation();

        // Get closest enemy Unit
        Unit closestUnit = null;
        long closestUnitDist = Long.MAX_VALUE;

        List<Unit> nearbyEnemies = senseNearbyEnemies();
        for (Unit enemy : nearbyEnemies) {
            long distance = enemy.location().mapLocation().distanceSquaredTo(myMapLoc);
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

        // if we can attack the target, attack
        if (javelin(this.target.id())) {
        }
        else if (gc.isAttackReady(getID()) && gc.canAttack(getID(), this.target.id())) {
            gc.attack(getID(), this.target.id());
        }
    }

    @Override
    protected void onDeath() {

    }

    private boolean hasTarget() {
        return this.target != null;
    }
}
