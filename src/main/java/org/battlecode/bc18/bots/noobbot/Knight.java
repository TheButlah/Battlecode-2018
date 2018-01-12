package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.Utils;

import static org.battlecode.bc18.Utils.gc;

public class Knight extends Bot {

    public static final UnitType TYPE = UnitType.Knight;

    private Unit target = null;

    public Knight(int id) {
        super(id);
    }

    @Override
    public void act() {
        // check if enemy unit in sight
        // if so, move towards it or attack it
        // else, move randomly

        Unit myUnit = getAsUnit();
        Location myLoc = myUnit.location();
        Team enemyTeam = (myUnit.team().toString().equalsIgnoreCase("Blue")) ? Team.Red : Team.Blue;

        if (!myLoc.isOnMap()) {
            println("TODO: handle knight in space");
            return;
        }

        MapLocation myMapLoc = myLoc.mapLocation();

        // Get closest enemy Unit
        Unit closestUnit = null;
        long closestUnitDist = Long.MAX_VALUE;

        VecUnit nearbyUnits = gc.senseNearbyUnitsByTeam(myMapLoc, myUnit.visionRange(), enemyTeam);
        for (int i = 0; i < nearbyUnits.size(); ++i) {
            Unit checkUnit = nearbyUnits.get(i);
            long distance = checkUnit.location().mapLocation().distanceSquaredTo(myMapLoc);
            if (distance < closestUnitDist) {
                closestUnit = checkUnit;
                closestUnitDist = distance;
            }
        }
        this.target = closestUnit;


        // if we don't have a target, try to move randomly, return if can't
        if (this.target == null) {
            if (gc.isMoveReady(this.id)) {
                //Move randomly
                int rand = Utils.rand.nextInt(Utils.dirs.length);
                for (int i = 0; i < Utils.dirs.length; i++) {
                    Direction dir = Utils.dirs[(i + rand) % Utils.dirs.length]; //Cycle through based on random offset
                    if (gc.canMove(this.id, dir)) {
                        //println("Moving");
                        gc.moveRobot(this.id, dir);
                        return;
                    }
                }
            }
            // can't move, no target
            return;
        }

        // if we can move towards the target, move
        if (gc.isMoveReady(this.id)) {
            Direction towardsTarget = myMapLoc.directionTo(this.target.location().mapLocation());
            if (gc.canMove(this.id, towardsTarget)) {
                gc.moveRobot(this.id, towardsTarget);
            }
        }

        // if we can attack the target, attack
        if (gc.canAttack(this.id, this.target.id())) {
            if (gc.isAttackReady(this.id)) {
                gc.attack(this.id, this.target.id());
            }
        }

        return;

    }

    @Override
    public UnitType getType() {
        return Knight.TYPE;
    }
}
