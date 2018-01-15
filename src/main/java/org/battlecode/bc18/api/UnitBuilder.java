package org.battlecode.bc18.api;

import bc.Unit;

/** In charge of constructing the units for the MyUnit class*/
public interface UnitBuilder {

    /**
     * Constructs an MyUnit object based off of a MyUnit and adds it to the collections of units.
     * The unit must belong to our Player, i.e. on our Planet under our Team.
     * The unit must be alive.
     * The unit must not already have been created.
     * @exception RuntimeException When the unit has an unknown type.
     */
    public AUnit newUnit(Unit ourUnit);

}
