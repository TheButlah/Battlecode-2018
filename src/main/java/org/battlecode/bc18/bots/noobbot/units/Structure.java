package org.battlecode.bc18.bots.noobbot.units;

import bc.Unit;
import org.battlecode.bc18.bots.util.Utils;

public abstract class Structure extends MyUnit {

    /** Whether the structure is fully built or not. */
    public boolean isBuilt() {
        return Utils.toBool(getAsUnit().structureIsBuilt());
        //TODO: Cache/calculate this
    }

    //////////END OF API//////////

    /**
     * Constructor for Structure.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected Structure(Unit unit) {
        super(unit);
    }

}
