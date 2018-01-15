package org.battlecode.bc18.api;

import bc.*;

public abstract class AbstractRocket extends AStructure {

    public static final UnitType TYPE = UnitType.Rocket;

    @Override
    public UnitType getType() {
        return AbstractRocket.TYPE;
    }



    //////////END OF API//////////

    /**
     * Constructor for AbstractRocket.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AbstractRocket(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Rocket;
    }
}
