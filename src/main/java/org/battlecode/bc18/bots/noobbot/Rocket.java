package org.battlecode.bc18.bots.noobbot;

import java.util.List;

import org.battlecode.bc18.ProductionManager;
import org.battlecode.bc18.api.ARocket;
import org.battlecode.bc18.api.AUnit;
import org.battlecode.bc18.api.MyRobot;
import org.battlecode.bc18.api.MyUnit;
import org.battlecode.bc18.util.Utils;
import org.battlecode.bc18.pathfinder.PathFinder;

import bc.Direction;
import bc.MapLocation;
import bc.Planet;
import bc.Unit;
import bc.UnitType;
import bc.VecUnitID;

public class Rocket extends ARocket {

    //private static long startTime;
    //private static long time1, time2;
    private static final int TAKEOFF_DELAY = 30;
    private static final int MAX_WORKERS = 4;
    private int liveRounds = 0;
    private int loadedWorkers = 0;
    /**
     * Constructor for Rocket.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected Rocket(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Rocket;
    }

    @Override
    public void act() {
        if (isBuilt() && isOnMap()) {
            if (Utils.PLANET == Planet.Earth) {
                //startTime = System.currentTimeMillis();
                ++liveRounds;
                Unit myUnit = getAsUnit();
                VecUnitID myGarrison = myUnit.structureGarrison();
                boolean isGarrisonFull = myGarrison.size() == myUnit.structureMaxCapacity();
                if (!isGarrisonFull) {
                    // Move nearby robots toward factory and load adjacent robots
                    MapLocation myMapLoc = getMapLocation();
                    List<AUnit> adjacentFriendlies = fastSenseNearbyFriendlies();
                    if (loadedWorkers >= MAX_WORKERS) {
                        adjacentFriendlies = Utils.filterAUnitsBy(adjacentFriendlies, (u) -> u.getType() != UnitType.Worker);
                    }
                    // Do not move units towards rocket when it is close to taking off
                    if (liveRounds < TAKEOFF_DELAY - 5 && adjacentFriendlies.size() < myUnit.structureMaxCapacity() - myGarrison.size()) {
                        List<AUnit> nearbyFriendlies = fastSenseNearbyFriendlies(ProductionManager.rushRockets() ? 1000 : 30);
                        PathFinder.pf.setTarget(myMapLoc);
                        for (MyUnit unit : nearbyFriendlies) {
                            if (unit instanceof MyRobot) {
                                MyRobot robot = (MyRobot) unit;
                                UnitType robotType = robot.getType();
                                if (robotType == UnitType.Worker && (loadedWorkers >= MAX_WORKERS || !((Worker)robot).shouldLaunch)) {
                                    continue;
                                }
                                Direction towardsRocket = PathFinder.pf.directionToTargetFrom(robot.getMapLocation());
                                if (towardsRocket != Direction.Center && robot.canMove(towardsRocket)) {
                                    //println("moving unit at " + robot.getMapLocation() + " to rocket");
                                    robot.move(towardsRocket);
                                }
                            }
                        }
                    }

                    for (MyUnit unit : adjacentFriendlies) {
                        if (unit instanceof MyRobot) {
                            MyRobot robot = (MyRobot) unit;
                            UnitType robotType = robot.getType();
                            if (robotType == UnitType.Worker && !((Worker)robot).shouldLaunch) {
                                continue;
                            }
                            if (canLoad(robot)) {
                                load(robot);
                                if (robotType == UnitType.Worker) {
                                    ++loadedWorkers;
                                }
                            }
                        }
                    }
                }
                //time1 += (System.currentTimeMillis() - startTime);
                //println("time1: " + time1);
                if (liveRounds >= TAKEOFF_DELAY || isGarrisonFull() || Utils.gc.round() >= 748) {
                    //startTime = System.currentTimeMillis();
                    // Move adjacent robots away from factory
                    MapLocation myMapLoc = getMapLocation();
                    List<AUnit> nearbyFriendliesInBlastRadius = fastSenseNearbyFriendlies(); // Note that this value may have changed from the previous sensed value
                    for (MyUnit unit : nearbyFriendliesInBlastRadius) {
                        if (unit instanceof MyRobot) {
                            MyRobot robot = (MyRobot) unit;
                            if (robot.isMoveReady()) {
                                //println("moving unit at " + robot.getMapLocation() + " away from rocket");
                                robot.fuzzyMove(myMapLoc.directionTo(robot.getMapLocation()));
                            }
                        }
                    }
                    // Launch rocket
                    int landingCoordinate = Utils.getNextLandingLocation();
                    MapLocation landingLoc = new MapLocation(Planet.Mars, landingCoordinate & 0x0000FFFF, landingCoordinate >>> 16);
                    if (canLaunchRocket(landingLoc)) {
                        launchRocket(landingLoc);
                        Utils.advanceLandingLocation();
                        return;
                    }
                    //time2 += (System.currentTimeMillis() - startTime);
                    //println("time2: " + time2);
                }
            }
            else {
                if (getAsUnit().structureGarrison().size() != 0) {
                    for (Direction dir : Utils.dirs) {
                        if (canUnload(dir)) {
                            unload(dir);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onDeath() {
        Worker.workersPerStructure.remove(getID());
    }
}
