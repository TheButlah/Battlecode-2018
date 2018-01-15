package org.battlecode.bc18.api;

import bc.Direction;
import bc.UnitType;

public interface MyWorker extends MyRobot {
    UnitType TYPE = UnitType.Worker;

    @Override
    default UnitType getType() {
        return TYPE;
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
    boolean canBlueprint(UnitType type, Direction dir);

    /**
     * Blueprints a structure of the given type in the given direction.
     * NOTE: Does not check to see if it can first.
     * @param type The UnitType to blueprint. Must be a structure.
     * @param dir The direction to create the blueprint.
     * @return The structure blueprinted.
     */
    MyStructure blueprint(UnitType type, Direction dir);

    /**
     * Checks to see if this worker can build up a particular structure's blueprint.
     * The worker and the blueprint must be adjacent to each other.
     * The worker cannot already have performed an action this round.
     * @param blueprint The blueprint to build up.
     * @return Whether the worker can build up the blueprint.
     */
    boolean canBuild(MyStructure blueprint);

    /**
     * Builds a given structure's blueprint, increasing its health by the worker's build amount.
     * If raised to maximum health, the blueprint becomes a completed structure.
     * NOTE: Does not check to see if it can build first.
     * @param blueprint The structure to build.
     */
    void build(MyStructure blueprint);

    /**
     * Checks to see if this worker can repair a particular structure.
     * The structure must be built.
     * The worker must be within range of the structure.
     * The worker cannot already have performed an action this round.
     * @param structure The structure to repair.
     * @return Whether the worker can repair the structure.
     */
    boolean canRepair(MyStructure structure);

    /**
     * Commands the worker to repair a structure, replenishing health to it.
     * This can only be done to structures which have been fully built.
     * NOTE: Does not check to see if it can repair first.
     * @param structure The structure to repair
     */
    void repair(MyStructure structure);

    /**
     * Checks to see if this worker can harvest karbonite in a particular direction.
     * The worker cannot already have performed an action this round.
     * The direction must contain karbonite.
     * @param direction The direction in which to harvest.
     * @return Whether the worker can harvest the karbonite.
     */
    boolean canHarvest(Direction direction);

    /**
     * Harvests up to the worker's harvest amount of karbonite from the given location.
     * NOTE: Does not check if harvesting is permitted.
     * @param direction The direction in which to harvest.
     */
    void harvest(Direction direction);

    /**
     * Checks to see if this worker can replicate in a particular direction.
     * The the worker's ability heat must be sufficiently low.
     * The team must have sufficient karbonite in its resource pool.
     * The square in the given direction must be empty.
     * @param direction The direction in which to replicate.
     * @return Whether the worker can replicate.
     */
    boolean canReplicate(Direction direction);

    /**
     * Replicates a worker in the given direction.
     * NOTE: Does not check if replication is permitted.
     * @param direction The direction in which to replicate.
     * @return The new replicated worker.
     */
    MyWorker replicate(Direction direction);

    /** Whether worker has acted (harvested, blueprinted, built, or repaired) this round. */
    boolean hasActed();
}
