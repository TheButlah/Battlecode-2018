package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.util.Utils.gc;

import bc.*;
import org.battlecode.bc18.CentroidManager;
import org.battlecode.bc18.api.AMage;
import org.battlecode.bc18.api.MyUnit;
import org.battlecode.bc18.pathfinder.Cell;
import org.battlecode.bc18.pathfinder.PathFinder;
import org.battlecode.bc18.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Mage extends AMage {

    /**
     * Constructor for Mage.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected Mage(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Mage;
    }

    @Override
    public void act() {
        long turn = gc.round();
        if (!isOnMap()) {
            //TODO: Handle mage in space/garrison?
            println("TODO: handle mage not on map");
            return;
        }

        //We already checked that we were on the map
        MapLocation myMapLoc = getMapLocation();

        // attack if ready and there is an immediate target
        if (isAttackReady()) {

            // Find immediate opponent
            Unit immediateTarget = null;

            VecUnit vec = gc.senseNearbyUnitsByTeam(myMapLoc, 50, gc.team() == Team.Red ? Team.Blue : Team.Red);
            for (int i = 0; i < vec.size(); i++) {
                Unit enemy = vec.get(i);
                MapLocation enemyMapLoc = enemy.location().mapLocation();
                if (myMapLoc.distanceSquaredTo(enemyMapLoc) <= enemy.attackRange()) {
                    if (immediateTarget == null) {
                        immediateTarget = enemy;
                    }
                    else if (enemy.damage() > immediateTarget.damage()) {
                        immediateTarget = enemy;
                    }
                    else if (enemy.damage() == immediateTarget.damage() &&
                            enemy.health() > immediateTarget.health()) {
                        immediateTarget = enemy;
                    }
                }
            }
            
            if (immediateTarget != null && isWithinAttackRange(immediateTarget)) {
                attack(immediateTarget);
                return;
            }
        }
        
        // at this point, immediate attacking didn't happen.
        // It will try blinking to the chokepoint.
        
        List<Cell> chokepoints = new ArrayList<>(); // TODO
        boolean[] isDangerZone = new boolean[1]; // TODO

        MapLocation destinationChoke = getChokeToMoveTowards(myMapLoc, chokepoints);
        
        boolean success = false;
        
        if (canBlink(destinationChoke)) {
            success = moveToChokePoint(null, destinationChoke, myMapLoc, chokepoints, isDangerZone, true);
            if (success) return;
        }

        PathFinder.pf.setTarget(destinationChoke);
        Direction towardsChoke = PathFinder.pf.directionToTargetFrom(myMapLoc);

        if (canMove(towardsChoke)) {
            success = moveToChokePoint(towardsChoke, destinationChoke, myMapLoc, chokepoints, isDangerZone, false);
            if (success) return;
        }

        moveRandomly();
        
    }
    
    /** return true if successful, false otherwise */
    private boolean moveToChokePoint(Direction towardsChoke, MapLocation destinationChoke, MapLocation myMapLoc, List<Cell> chokepoints, boolean[] isDangerZone, boolean blink) {
        
        if (chokepoints.size() < 1)
            return false;
        
        if (blink) {
            if (!isDangerZone[toIndex(destinationChoke.getY(), destinationChoke.getX())]) {
                blink(destinationChoke);
                return true;
            } else {
                int x = ((destinationChoke.getX() - myMapLoc.getX()) * 2 / 3) + myMapLoc.getX();
                int y = ((destinationChoke.getY() - myMapLoc.getY()) * 2 / 3) + myMapLoc.getY();
                if (!isDangerZone[toIndex(y,x)]) {
                    blink(destinationChoke);
                    return true;
                }
                else {
                    if (towardsChoke != Direction.Center && isAccessible(towardsChoke)) {
                        MapLocation towardsChokeLocation = myMapLoc.add(towardsChoke);
                        if (!isDangerZone[toIndex(towardsChokeLocation.getY(), towardsChokeLocation.getX())]) {
                            move(towardsChoke);
                            return true;
                        }
                    }

                    return false;
                }
            }
        } else {
            if (towardsChoke != Direction.Center && isAccessible(towardsChoke)) {
                MapLocation towardsChokeLocation = myMapLoc.add(towardsChoke);
                if (!isDangerZone[toIndex(towardsChokeLocation.getY(), towardsChokeLocation.getX())]) {
                    move(towardsChoke);
                    return true;
                }
            }

            return false;
        }
    }
    
    private MapLocation getChokeToMoveTowards(MapLocation myMapLoc, List<Cell> chokepoints) {
        
        if (chokepoints.size() < 1)
            return null;
        
        MapLocation destinationChoke = chokepoints.get(0).getLoc();
        int myNearbyFriendsCount = (int) gc.senseNearbyUnitsByTeam(destinationChoke, 100, Utils.OTHER_TEAM).size();
        
        for (Cell cell : chokepoints) {
            MapLocation candidateChoke = cell.getLoc();
            int candidateCount = (int) gc.senseNearbyUnitsByTeam(candidateChoke, 100, Utils.OTHER_TEAM).size();
            
            double percentage = ((double) (candidateCount - myNearbyFriendsCount)) / ((double) myNearbyFriendsCount);
            if (percentage >= 0.2) {
                destinationChoke = candidateChoke;
                myNearbyFriendsCount = candidateCount;
            }
        }
        
        return destinationChoke;
    }

    private int toIndex(int r, int c) {
        return c + r * Utils.MAP_WIDTH;
    }

    private void moveRandomly() {
        
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

    @Override
    protected void onDeath() {

    }
}
