package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.util.Utils.gc;

import bc.*;
import org.battlecode.bc18.TargetManager;
import org.battlecode.bc18.api.AMage;
import org.battlecode.bc18.util.Utils;
import org.battlecode.bc18.util.pathfinder.PathFinder;

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

        if (this.nextDestination != null) {
            if (isMoveReady()) {
                Direction towardsRocket = PathFinder.pf.directionToTargetFrom(myMapLoc);
                if (towardsRocket != Direction.Center && isAccessible(towardsRocket)) {
                    move(towardsRocket);
                    nextDestination = null;
                    return;
                }
            }
        }
        
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
        // As a followup strategy, it will blink to the centroid.
        Planet planet = gc.planet();
        PlanetMap planetMap = gc.startingMap(planet);
        
        if (isBlinkReady()) {
            TargetManager tman = new TargetManager(planetMap.getInitial_units(), 3);

            // FIXME: find a MapLocation to blink.
            MapLocation blinkLoc = new MapLocation(planet, 0, 0);

            if (isAcessibleBlink(blinkLoc)) {
                blink(blinkLoc);
                return;
            }
        }
        
        // At this point, blinking also didn't happen.
        // TODO: move toward a centroid. 
        

        // If we haven't yet moved, move randomly
        moveRandomly();
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
