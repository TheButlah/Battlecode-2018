package org.battlecode.bc18.bots.noobbot;

import java.util.List;

import org.battlecode.bc18.api.ARocket;
import org.battlecode.bc18.api.MyRobot;
import org.battlecode.bc18.api.MyUnit;
import org.battlecode.bc18.util.Utils;

import bc.Direction;
import bc.MapLocation;
import bc.Planet;
import bc.Unit;
import bc.UnitType;

public class Rocket extends ARocket {

    private static final int TAKEOFF_DELAY = 10;
    private int liveRounds = 0;
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
                ++liveRounds;
                List<MyUnit> nearbyFriendlies = senseNearbyFriendlies();
                for (MyUnit unit : nearbyFriendlies) {
                    if (unit instanceof MyRobot) {
                        MyRobot robot = (MyRobot) unit;
                        if (canLoad(robot)) {
                            load(robot);
                        }
                        // TODO: Move nearby robots towards factory
                    }
                }
                if (liveRounds >= TAKEOFF_DELAY || isGarrisonFull()) {
                    // Move adjacent robots away from factory
                    MapLocation myMapLoc = getMapLocation();
                    List<MyUnit> nearbyFriendliesInBlastRadius = senseNearbyFriendlies(); // Note that this value may have changed from the previous sensed value
                    for (MyUnit unit : nearbyFriendliesInBlastRadius) {
                        if (unit instanceof MyRobot) {
                            MyRobot robot = (MyRobot) unit;
                            if (robot.isMoveReady()) {
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
