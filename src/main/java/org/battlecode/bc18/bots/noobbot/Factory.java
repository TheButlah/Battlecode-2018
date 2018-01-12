package org.battlecode.bc18.bots.noobbot;

import bc.*;
import org.battlecode.bc18.Utils;

import static org.battlecode.bc18.Utils.gc;

public class Factory extends Bot {

    public static final UnitType TYPE = UnitType.Factory;

    public Factory(int id) {
        super(id);
    }

    @Override
    public void act() {
        // make and place workers until you can't :D
        if (gc.canProduceRobot(this.id, UnitType.Worker)) {
            gc.produceRobot(this.id, UnitType.Worker);
            // check where it can be unloaded, and unload it
            for (Direction dir : Utils.dirs) {
                // if there are no more units to unload, break
                if (this.getAsUnit().structureGarrison().size() == 0) break;
                if (gc.canUnload(this.id, dir)) {
                    gc.unload(this.id, dir);
                    // no break here so it can unload multiple (I think that's allowed)
                }
            }
        }
    }

    @Override
    public UnitType getType() {
        return Factory.TYPE;
    }
}
