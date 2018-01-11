package org.battlecode.bc18.bots.noobbot;

import bc.Direction;
import bc.Unit;
import bc.UnitType;
import bc.bc;

import static org.battlecode.bc18.bots.noobbot.Main.gc;

public class Worker extends Bot {
    
    private boolean hasBuiltFactory = false;

    public Worker(Unit u) {
        super(u);
    }

    @Override
    public void act() {
        // first turn, build a factory [by one unit].
        // if the factory has not been built, replicate if can and help building a factory.
        // else, move randomly.
        // try mining if walked over the Karbonite.

        long turn = gc.round();

        // for each direction, find the first availability for building a blueprint for factory.
        // if it can put down a blueprint, it performs gc.blueprint (although we don't know how to access
        // blueprint_id, which needs to get accessed in order to call gc.build() function.
        for (Direction dir : Direction.values()) {
            if (!hasBuiltFactory && gc.karbonite() >= bc.bcUnitTypeBlueprintCost(UnitType.Factory)) {
                if (gc.canBlueprint(this.ID, UnitType.Factory, dir)) {
                    gc.blueprint(this.ID, UnitType.Factory, dir);
                }
            }
        }

        int blueprint_id = 1; // ??

        // we need to check whether this unit has already performed an action this turn.
        // If not, it will have one of the following options to perform.

        // building a factory based on the blueprint created.
        if (gc.canBuild(this.ID, blueprint_id)) {
            gc.build(this.ID, blueprint_id);
        }

        // or move randomly.
        for (Direction dir : Direction.values()) {
            if (gc.canReplicate(this.ID, dir)) {
                gc.replicate(this.ID, dir);
            }
        }

        // if stepped on the Karbonite, it will try mining up to its limit.
        for (Direction dir : Direction.values()) {
            if (gc.canHarvest(this.ID, dir)) {
                gc.harvest(this.ID, dir);
            }
        }
    }
}
