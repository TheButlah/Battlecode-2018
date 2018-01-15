package org.battlecode.bc18.api;

import bc.*;
import org.battlecode.bc18.util.Utils;

import static org.battlecode.bc18.util.Utils.gc;

public abstract class AbstractWorker extends AbstractRobot {

    public static final UnitType TYPE = UnitType.Worker;

    @Override
    public UnitType getType() {
        return AbstractWorker.TYPE;
    }

    /**
     * Checks to see if this worker can blueprint a particular structure in a given direction.
     * The worker can only blueprint factories, and rockets if Rocketry has been researched.
     * The team must have sufficient karbonite in its resource pool.
     * The worker cannot already have performed an action this round.
     * @param type The type of structure to blueprint.
     * @param dir The direction to create the blueprint.
     * @return Whether the worker can perform the blueprint.
     */
    public boolean canBlueprint(UnitType type, Direction dir) {
        return gc.canBlueprint(getID(), type, dir);
    }

    /**
     * Blueprints a structure of the given type in the given direction.
     * NOTE: Does not check to see if it can first.
     * @param type The UnitType to blueprint. Must be a structure.
     * @param dir The direction to create the blueprint.
     * @return The structure blueprinted.
     */
    public AbstractStructure blueprint(UnitType type, Direction dir) {
        assert canBlueprint(type, dir);
        println("Blueprinting: " + type + " towards " + dir);
        gc.blueprint(getID(), type, dir);
        Unit unit = gc.senseUnitAtLocation(getMapLocation().add(dir));
        return (AbstractStructure) AbstractUnit.makeUnit(unit);
    }

    /**
     * Checks to see if this worker can build up a particular structure's blueprint.
     * The worker and the blueprint must be adjacent to each other.
     * The worker cannot already have performed an action this round.
     * @param blueprint The blueprint to build up.
     * @return Whether the worker can build up the blueprint.
     */
    public boolean canBuild(AbstractStructure blueprint) {
        return gc.canBuild(getID(), blueprint.getID());
    }

    /**
     * Builds a given structure's blueprint, increasing its health by the worker's build amount.
     * If raised to maximum health, the blueprint becomes a completed structure.
     * NOTE: Does not check to see if it can build first.
     * @param blueprint The structure to build.
     */
    public void build(AbstractStructure blueprint) {
        assert canBuild(blueprint);
        println("Building: " + blueprint);
        gc.build(getID(), blueprint.getID());
    }

    /**
     * Checks to see if this worker can repair a particular structure.
     * The structure must be built.
     * The worker must be within range of the structure.
     * The worker cannot already have performed an action this round.
     * @param structure The structure to repair.
     * @return Whether the worker can repair the structure.
     */
    public boolean canRepair(AbstractStructure structure) {
        return gc.canRepair(getID(), structure.getID());
    }

    /**
     * Commands the worker to repair a structure, replenishing health to it.
     * This can only be done to structures which have been fully built.
     * NOTE: Does not check to see if it can repair first.
     * @param structure The structure to repair
     */
    public void repair(AbstractStructure structure) {
        assert canRepair(structure);
        //println("Repairing: " + structure);
        gc.repair(getID(), structure.getID());
    }

    /**
     * Checks to see if this worker can harvest karbonite in a particular direction.
     * The worker cannot already have performed an action this round.
     * The direction must contain karbonite.
     * @param direction The direction in which to harvest.
     * @return Whether the worker can harvest the karbonite.
     */
    public boolean canHarvest(Direction direction) {
        return gc.canHarvest(getID(), direction);
    }
    
    /**
     * Harvests up to the worker's harvest amount of karbonite from the given location.
     * NOTE: Does not check if harvesting is permitted.
     * @param direction The direction in which to harvest.
     */
    public void harvest(Direction direction) {
        assert canHarvest(direction);
        //println("Harvesting: towards " + direction);
        gc.harvest(getID(), direction);
    }

    /**
     * Checks to see if this worker can replicate in a particular direction.
     * The the worker's ability heat must be sufficiently low.
     * The team must have sufficient karbonite in its resource pool.
     * The square in the given direction must be empty.
     * @param direction The direction in which to replicate.
     * @return Whether the worker can replicate.
     */
    public boolean canReplicate(Direction direction) {
        return gc.canReplicate(getID(), direction);
    }

    /**
     * Replicates a worker in the given direction.
     * NOTE: Does not check if replication is permitted.
     * @param direction The direction in which to replicate.
     * @return The new replicated worker.
     */
    public AbstractWorker replicate(Direction direction) {
        //println("Replicating: towards " + direction);
        gc.replicate(getID(), direction);
        Unit unit = gc.senseUnitAtLocation(getMapLocation().add(direction));
        if (unit.unitType() != UnitType.Worker) {
            return null;
        }
        return (AbstractWorker) makeUnit(unit);
    }

    /** Whether worker has acted (harvested, blueprinted, built, or repaired) this round. */
    public boolean hasActed() {
        return Utils.toBool(getAsUnit().workerHasActed());
    }



    //////////END OF API//////////



    private boolean hasActed = false;

    /**
     * Constructor for AbstractWorker.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected AbstractWorker(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Worker;
    }

}
