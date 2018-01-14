package org.battlecode.bc18.api;

import bc.Unit;
import bc.UnitType;

/** In charge of constructing the units for the AbstractUnit class*/
public interface UnitBuilder {

    /**
     * Constructs an AbstractUnit object based off of a Unit and adds it to the collections of units.
     * The unit must belong to our Player, i.e. on our Planet under our Team.
     * The unit must be alive.
     * The unit must not already have been created.
     * @exception RuntimeException When the unit has an unknown type.
     */
    public AbstractUnit newUnit(Unit ourUnit);

}
