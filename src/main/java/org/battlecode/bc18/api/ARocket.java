package org.battlecode.bc18.api;

import bc.*;

public abstract class ARocket extends AStructure implements MyRocket {



    //////////END OF API//////////

    /**
     * Constructor for ARocket.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected ARocket(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Rocket;
    }
}
