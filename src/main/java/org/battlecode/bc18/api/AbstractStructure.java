package org.battlecode.bc18.api;

import bc.Unit;
import org.battlecode.bc18.bots.util.Utils;

public abstract class AbstractStructure extends AbstractUnit {

    /** Whether the structure is fully built or not. */
    public boolean isBuilt() {
        return Utils.toBool(getAsUnit().structureIsBuilt());
        //TODO: Cache/calculate this
    }

    //////////END OF API//////////

    /**
     * Constructor for AbstractStructure.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AbstractStructure(Unit unit) {
        super(unit);
    }

}
