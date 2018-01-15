package org.battlecode.bc18.api;

import bc.MapLocation;
import bc.Unit;
import bc.UnitType;

import static org.battlecode.bc18.util.Utils.gc;

public interface MyMage extends MyRobot, MyAttacker {
    UnitType TYPE = UnitType.Mage;

    @Override
    default UnitType getType() {
        return TYPE;
    }

    /**
     * Checks to see if a location is accessible by blink, meaning there is nothing blocking physical access.
     * Takes into account only ability range, terrain, other units, and the edge of the game map.
     * @param loc The location to check.
     * @return If there are no physical objects preventing movement to that direction.
     */
    boolean isAcessibleBlink(MapLocation loc);

    /**
     * Whether blink ability is ready.
     * NOTE: Checks both heat and unlock status.
     */
    boolean isBlinkReady();

    /**
     * Checks to see if a location is able to be blinked to.
     * This means there is nothing blocking physical access and blink is ready to be used.
     * @param loc The location to check.
     * @return Whether the mage can blink.
     */
    boolean canBlink(MapLocation loc);

    /**
     * Blinks the mage to the given location.
     * NOTE: Does not check to see if it can blink first.
     * @param loc The MapLocation to blink to.
     */
    void blink(MapLocation loc);

}
