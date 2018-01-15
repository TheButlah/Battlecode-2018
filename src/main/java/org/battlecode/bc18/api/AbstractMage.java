package org.battlecode.bc18.api;

import bc.*;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class AbstractMage extends AbstractRobot {

    public static final UnitType TYPE = UnitType.Mage;

    @Override
    public UnitType getType() {
        return AbstractMage.TYPE;
    }

    /**
     * Checks to see if a location is accessible by blink, meaning there is nothing blocking physical access.
     * Takes into account only the mage's ability range, map terrain, other units, and the edge of the game map.
     * NOTE: Does not take into account ability heat. Use `canBlink()` or `isBlinkReady()` for that.
     * @param loc The location to check.
     * @return If there are no physical objects preventing movement to that direction.
     */
    public boolean isBlinkAcessible(MapLocation loc) {
        return gc.canBlink(getID(), loc);
    }

    /** Whether the mage is ready to blink */
    public boolean isBlinkReady() {
        return gc.isBlinkReady(getID());
    }

    /**
     * Checks to see if a location is able to be blinked to.
     * This means there is nothing blocking physical access and blink is off cooldown.
     * Takes into account ability heat, ability range, map terrain, other units, and the edge of the game map.
     * @param loc The location to check.
     * @return If there is nothing preventing movement to that direction.
     */
    public boolean canBlink(MapLocation loc) {
        return isBlinkAcessible(loc) && isBlinkReady();
    }

    /**
     * Blinks the mage to the given location.
     * NOTE: Does not check to see if it can blink first.
     * @param loc The MapLocation to blink to.
     */
    public void blink(MapLocation loc) {
        assert canBlink(loc);
        gc.blink(getID(), loc);
    }



    //////////END OF API//////////

    /**
     * Constructor for AbstractMage.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AbstractMage(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Mage;
    }
}
