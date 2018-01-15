package org.battlecode.bc18.api;

import bc.*;
import org.battlecode.bc18.util.Utils;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class AWorker extends ARobot implements MyWorker {

    @Override
    public boolean canBlueprint(UnitType type, Direction dir) {
        return gc.canBlueprint(getID(), type, dir);
    }

    @Override
    public MyStructure blueprint(UnitType type, Direction dir) {
        assert canBlueprint(type, dir);
        gc.blueprint(getID(), type, dir);
        Unit unit = gc.senseUnitAtLocation(getMapLocation().add(dir));
        return (MyStructure) AUnit.makeUnit(unit);
    }

    @Override
    public boolean canBuild(MyStructure blueprint) {
        return gc.canBuild(getID(), blueprint.getID());
    }

    @Override
    public void build(MyStructure blueprint) {
        assert canBuild(blueprint);
        gc.build(getID(), blueprint.getID());
    }

    @Override
    public boolean canRepair(MyStructure structure) {
        return gc.canRepair(getID(), structure.getID());
    }

    @Override
    public void repair(MyStructure structure) {
        assert canRepair(structure);
        gc.repair(getID(), structure.getID());
    }

    @Override
    public boolean canHarvest(Direction direction) {
        return gc.canHarvest(getID(), direction);
    }
    
    @Override
    public void harvest(Direction direction) {
        assert canHarvest(direction);
        gc.harvest(getID(), direction);
    }

    @Override
    public boolean canReplicate(Direction direction) {
        return gc.canReplicate(getID(), direction);
    }

    @Override
    public MyWorker replicate(Direction direction) {
        gc.replicate(getID(), direction);
        Unit unit = gc.senseUnitAtLocation(getMapLocation().add(direction));
        if (unit.unitType() != UnitType.Worker) {
            return null;
        }
        return (MyWorker) makeUnit(unit);
    }

    @Override
    public boolean hasActed() {
        return Utils.toBool(getAsUnit().workerHasActed());
    }



    //////////END OF API//////////



    private boolean hasActed = false;

    /**
     * Constructor for AWorker.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AWorker(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Worker;
    }

}
