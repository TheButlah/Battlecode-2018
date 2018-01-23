package org.battlecode.bc18.bots.noobbot;

import static org.battlecode.bc18.util.Utils.gc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.battlecode.bc18.ProductionManager;
import org.battlecode.bc18.api.AUnit;
import org.battlecode.bc18.api.AWorker;
import org.battlecode.bc18.api.MyStructure;
import org.battlecode.bc18.api.MyUnit;
import org.battlecode.bc18.util.Pair;
import org.battlecode.bc18.util.Utils;
import org.battlecode.bc18.util.pathfinder.PathFinder;

import bc.Direction;
import bc.MapLocation;
import bc.Planet;
import bc.Unit;
import bc.UnitType;
import bc.VecUnit;

public class Worker extends AWorker {
    //static int time1, time2, time3, time4, time5, time6, time7;
    //static long startTime;

    /**
     * A mapping of structures to numbers of workers assigned to each structure
     */
    public static final Map<Integer, Integer> workersPerStructure = new HashMap<>();
    /**
     * A mapping of workers to the structures they are assigned to
     */
    public static final Map<Integer, MyStructure> workerStructureAssignment = new HashMap<>();
    private static final Direction[] dirsBottomLeft = new Direction[] {
            Direction.Northeast, Direction.North, Direction.East, Direction.Northwest,
            Direction.Southeast, Direction.West, Direction.South, Direction.Southwest
    };
    private static final Direction[] dirsTopLeft = new Direction[] {
            Direction.Southeast, Direction.South, Direction.East, Direction.Southwest,
            Direction.Northeast, Direction.West, Direction.North, Direction.Northwest
    };
    private static final Direction[] dirsLeft = new Direction[] {
            Direction.East, Direction.Northeast, Direction.Southeast, Direction.North,
            Direction.South, Direction.Northwest, Direction.Southwest, Direction.West
    };
    private static final Direction[] dirsBottomRight = new Direction[] {
            Direction.Northwest, Direction.North, Direction.West, Direction.Northeast,
            Direction.Southwest, Direction.East, Direction.South, Direction.Southeast
    };
    private static final Direction[] dirsTopRight = new Direction[] {
            Direction.Southwest, Direction.South, Direction.West, Direction.Southeast,
            Direction.Northwest, Direction.East, Direction.North, Direction.Northeast
    };
    private static final Direction[] dirsRight = new Direction[] {
            Direction.West, Direction.Northwest, Direction.Southwest, Direction.North,
            Direction.South, Direction.Northeast, Direction.Southeast, Direction.East
    };
    private static final Direction[] dirsBottom = new Direction[] {
            Direction.North, Direction.Northwest, Direction.Northeast, Direction.West,
            Direction.East, Direction.Southwest, Direction.Southeast, Direction.South
    };
    private static final Direction[] dirsTop = new Direction[] {
            Direction.South, Direction.Southwest, Direction.Southeast, Direction.West,
            Direction.East, Direction.Northwest, Direction.Northeast, Direction.North
    };
    private static final Direction[] dirsNoPrefs = new Direction[] {
            Direction.North, Direction.Northeast, Direction.East, Direction.Southeast,
            Direction.South, Direction.Southwest, Direction.West, Direction.Northwest
    };
    /**
     * Constructor for Worker.
     * @exception RuntimeException Occurs for unknown UnitType, unit already exists, unit doesn't belong to our player.
     */
    protected Worker(Unit unit) {
        super(unit);
        assert unit.unitType() == UnitType.Worker;
    }

    @Override
    public void act() {
        if (!isOnMap()) {
            // println("TODO: handle worker not on map");
            return;
        }
        long turn = gc.round();
        if (Utils.PLANET == Planet.Mars && turn >= 750) {
            // Spam replicate after Earth is wiped out
            for (Direction dir : Utils.dirs) {
                if (canReplicate(dir)) {
                    //println("Replicating");
                    MyUnit newWorker = replicate(dir);
                    if (newWorker != null) {
                        break;
                    }
                }
            }
        }
        //We already checked that we were on the map
        MapLocation myMapLoc = getMapLocation();

        MyStructure targetStructure = getStructureAssignment();
        if (targetStructure != null && targetStructure.isDead()) {
            println("Assigned to dead factory!");
            deassignStructure();
            targetStructure = null;
        }

        if (targetStructure == null) {
            if (Utils.PLANET == Planet.Earth) {
                UnitType nextDesiredProduction = ProductionManager.getNextProductionType();
                if ((turn == 1 && getID() == Main.initializingWorkerId) || nextDesiredProduction == UnitType.Factory) {
                    //startTime = System.currentTimeMillis();
                    List<MyStructure> nearbyStructures = getNearbyStructures();
                    ArrayList<MapLocation> nearbyStructuresLoc = new ArrayList<>();
                    for (MyStructure structure : nearbyStructures) {
                        nearbyStructuresLoc.add(structure.getMapLocation());
                    }
                    for (Direction dir : preferredDirections(myMapLoc)) {
                        if (canBlueprint(UnitType.Factory, dir)
                                && !Utils.isAnyWithinDistance(nearbyStructuresLoc, myMapLoc.add(dir), 4)) {
                            targetStructure = (Factory) blueprint(UnitType.Factory, dir);
                            assignStructure(targetStructure);
                            break;
                        }
                    }
                    //time1 += System.currentTimeMillis() - startTime;
                    //System.out.println("time 1: " + time1);
                }
                if (!hasActed() && nextDesiredProduction == UnitType.Rocket) {
                    //startTime = System.currentTimeMillis();
                    List<MyStructure> nearbyStructures = getNearbyStructures();
                    ArrayList<MapLocation> nearbyStructuresLoc = new ArrayList<>();
                    for (MyStructure structure : nearbyStructures) {
                        nearbyStructuresLoc.add(structure.getMapLocation());
                    }
                    for (Direction dir : preferredDirections(myMapLoc)) {
                        if (canBlueprint(UnitType.Rocket, dir)
                                && !Utils.isAnyWithinDistance(nearbyStructuresLoc, myMapLoc.add(dir), 4)) {
                            targetStructure = (Rocket) blueprint(UnitType.Rocket, dir);
                            assignStructure(targetStructure);
                            break;
                        }
                    }
                    //time2 += System.currentTimeMillis() - startTime;
                    //System.out.println("time 2: " + time2);
                }
            }
            // Search for target structures
            //startTime = System.currentTimeMillis();
            List<MyUnit> nearbyStructures = senseNearbyFriendlies(UnitType.Factory);
            nearbyStructures.addAll(senseNearbyFriendlies(UnitType.Rocket));
            MyStructure closestStructure = null;
            long closestStructureDist = Long.MAX_VALUE;
            for (MyUnit unit : nearbyStructures) {
                MyStructure structure = (MyStructure) unit;
                if (!structure.isBuilt()
                        || structure.getHealth() < structure.getMaxHealth()) {
                    // Ignore empty rockets on mars
                    if (Utils.PLANET == Planet.Mars && structure.getType() == UnitType.Rocket) {
                        if (((AUnit)structure).getAsUnit().structureGarrison().size() == 0) {
                            continue;
                        }
                    }
                    long distance = structure.getMapLocation().distanceSquaredTo(myMapLoc);
                    if (distance < closestStructureDist) {
                        closestStructure = structure;
                        closestStructureDist = distance;
                    }
                }
            }
            targetStructure = closestStructure;
            if (targetStructure != null) {
                assignStructure(targetStructure);
            }
            //time3 += System.currentTimeMillis() - startTime;
            //System.out.println("time 3: " + time3);
        }

        if (isMoveReady()) {
            boolean cannotReachStructure = false;
            if (targetStructure != null) {
                //startTime = System.currentTimeMillis();
                // Move towards target structure
                MapLocation structureLoc = targetStructure.getMapLocation();
                PathFinder.pf.setTarget(structureLoc);
                if (PathFinder.pf.getCostFrom(myMapLoc) > PathFinder.INFINITY) {
                    // Deassign structure if it is unreachable
                    cannotReachStructure = true;
                    deassignStructure();
                    targetStructure = null;
                }
                else {
                    Direction towardsStructure = PathFinder.pf.directionToTargetFrom(myMapLoc);
                    if (towardsStructure != Direction.Center && isAccessible(towardsStructure)) {
                        move(towardsStructure);
                    }
                }
                //time4 += System.currentTimeMillis() - startTime;
                //System.out.println("time 4: " + time4);
            }
            if (targetStructure == null || cannotReachStructure) {
                boolean moved = false;
                //startTime = System.currentTimeMillis();
                int numCloseEnemies = 0;
                double closeEnemyAvgX = 0;
                double closeEnemyAvgY = 0;
                VecUnit nearbyEnemies = Utils.gc.senseNearbyUnitsByTeam(myMapLoc, getVisionRange(), Utils.OTHER_TEAM);
                for (int i = 0; i < nearbyEnemies.size(); ++i) {
                    Unit enemy = nearbyEnemies.get(i);
                    UnitType enemyType = enemy.unitType();
                    if (enemyType == UnitType.Knight || enemyType == UnitType.Ranger || enemyType == UnitType.Mage) {
                        MapLocation enemyLoc = enemy.location().mapLocation();
                        ++numCloseEnemies;
                        closeEnemyAvgX += (enemyLoc.getX() - closeEnemyAvgX) / numCloseEnemies;
                        closeEnemyAvgY += (enemyLoc.getY() - closeEnemyAvgY) / numCloseEnemies;
                    }
                }
                if (numCloseEnemies != 0) {
                    double deltaX = myMapLoc.getX() - closeEnemyAvgX;
                    double deltaY = myMapLoc.getY() - closeEnemyAvgY;
                    double angleAwayFromEnemy = Math.atan2(deltaY, deltaX);
                    Direction directionAwayFromEnemy = Utils.angleToDirection(angleAwayFromEnemy);
                    if (fuzzyMove(directionAwayFromEnemy) != null) {
                        moved = true;
                    }
                }

                if (!moved) {
                    //No target structure, so look for nearby karbonite
                    List<Pair<MapLocation, Integer>> deposits = senseNearbyKarbonite();
                    if (deposits.size() != 0) {
                        Pair<MapLocation, Integer> targetDeposit = Utils.closestPair(deposits, myMapLoc);
                        MapLocation targetLoc = targetDeposit.getFirst();
                        PathFinder.pf.setTarget(targetLoc);
                        Direction towardsKarbonite = PathFinder.pf.directionToTargetFrom(myMapLoc);
                        if (towardsKarbonite != Direction.Center && isAccessible(towardsKarbonite)) {
                            move(towardsKarbonite);
                            moved = true;
                        }
                    }
                    if (AUnit.getNumUnits(UnitType.Worker) < gc.round() / 5 && (deposits.size() > 5 || sumDeposits(deposits) > 100)) {
                        for (Direction dir : Utils.dirs) {
                            if (canReplicate(dir)) {
                                //println("Replicating");
                                MyUnit newWorker = replicate(dir);
                                if (newWorker != null) {
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!moved) {
                    //Move randomly
                    int offset = Utils.rand.nextInt(Utils.dirs.length);
                    for (int i = 0; i < Utils.dirs.length; i++) {
                        Direction dir = Utils.dirs[(i + offset) % Utils.dirs.length]; //Cycle through based on random offset
                        if (isAccessible(dir)) {
                            //println("Moving");
                            move(dir);
                            break;
                        }
                    }
                }
                //time5 += System.currentTimeMillis() - startTime;
                //System.out.println("time 5: " + time5);
            }
        }

        if (Utils.toBool(getAsUnit().workerHasActed())) {
            return;
        }

        if (targetStructure != null) {
            boolean structureBuilt = targetStructure.isBuilt();
            boolean needsRepair = targetStructure.getHealth() < targetStructure.getMaxHealth();
            if (!structureBuilt || needsRepair) {
                //startTime = System.currentTimeMillis();
                // replicate if factory not yet built or factory damaged
                List<MyUnit> nearbyWorkers = senseNearbyFriendlies(UnitType.Worker);
                if (nearbyWorkers.size() < 4) {
                    for (Direction dir : Utils.dirs) {
                        if (canReplicate(dir)) {
                            //println("Replicating");
                            MyUnit newWorker = replicate(dir);
                            if (newWorker != null) {
                                break;
                            }
                        }
                    }
                }
                // building a factory based on the blueprint created.
                if (!structureBuilt) {
                    if (canBuild(targetStructure)) {
                        //println("Building");
                        build(targetStructure);
                        return;
                    }
                }
                else if (canRepair(targetStructure)) {
                    //println("Repairing");
                    repair(targetStructure);
                    return;
                }
                //time6 += System.currentTimeMillis() - startTime;
                //System.out.println("time 6: " + time6);
            }
            else {
                // De-assign worker from factory so he can explore the map
                deassignStructure();
                targetStructure = null;
            }
        }

        //startTime = System.currentTimeMillis();
        // if can see Karbonite, mine it
        for (Direction dir : Utils.dirs) {
            if (canHarvest(dir)) {
                //println("Harvesting");
                harvest(dir);
                return;
            }
        }
        //time7 += System.currentTimeMillis() - startTime;
        //System.out.println("time 7: " + time7);
    }

    @Override
    protected void onDeath() {
        deassignStructure();
    }

    /**
     * Gets the structure assigned to the {@link Worker} calling this method
     * Pre-condition: this method should only be called by instances of the {@link Worker} class
     * @return the factory
     */
    MyStructure getStructureAssignment() {
        return workerStructureAssignment.get(getID());
    }

    /**
     * Assigns the structure with the given ID to the {@link Worker} calling this method.
     * Pre-condition: this method should only be called by instances of the {@link Worker} class.
     * @param structure the structure.
     */
    void assignStructure(MyStructure structure) {
        MyStructure previousAssignment = workerStructureAssignment.put(getID(), structure);
        if (previousAssignment != null) {
            workersPerStructure.put(previousAssignment.getID(), workersPerStructure.get(previousAssignment.getID()) - 1);
        }
        if (!workersPerStructure.containsKey(structure.getID())) {
            workersPerStructure.put(structure.getID(), 1);
        }
        else {
            workersPerStructure.put(structure.getID(), workersPerStructure.get(structure.getID()) + 1);
        }
    }

    /**
     * De-assigns the structure assigned to the {@link Worker} calling this method.
     * If there is no assigned factory, no changes are made
     * Pre-condition: this method should only be called by instances of the {@link Worker} class
     * @return the the de-assigned factory, or null if none
     */
    MyStructure deassignStructure() {
        MyStructure structure = workerStructureAssignment.remove(getID());
        if (structure != null) {
            Integer count = workersPerStructure.get(structure.getID());
            if (count != null) {
                workersPerStructure.put(structure.getID(), count - 1);
            }
        }
        return structure;
    }

    List<MyStructure> getNearbyStructures() {
        ArrayList<MyStructure> structures = new ArrayList<>();
        MapLocation myMapLoc = getMapLocation();
        for (int structureId : workersPerStructure.keySet()) {
            MyStructure structure = (MyStructure) AUnit.getUnit(structureId);
            if (myMapLoc.distanceSquaredTo(structure.getMapLocation()) <= 10) {
                structures.add(structure);
            }
        }
        return structures;
    }

    private Direction[] preferredDirections(MapLocation myMapLoc) {
        int x = myMapLoc.getX();
        int y = myMapLoc.getY();
        if (x < 2) {
            if (y < 2) {
                // Avoid bottom-left
                return dirsBottomLeft;
            }
            else if (y >= Utils.MAP_HEIGHT - 2) {
                // Avoid top-left
                return dirsTopLeft;
            }
            else {
                // Avoid left
                return dirsLeft;
            }
        }
        else if (x >= Utils.MAP_WIDTH - 2) {
            if (y < 2) {
                // Avoid bottom-right
                return dirsBottomRight;
            }
            else if (y >= Utils.MAP_HEIGHT - 2) {
                // Avoid top-right
                return dirsTopRight;
            }
            else {
                // Avoid right
                return dirsRight;
            }
        }
        else if (y < 2) {
            // Avoid bottom
            return dirsBottom;
        }
        else if (y >= Utils.MAP_HEIGHT - 2) {
            // Avoid top
            return dirsTop;
        }
        else {
            return dirsNoPrefs;
        }
    }

    private int sumDeposits(List<Pair<MapLocation, Integer>> karboniteDeposits) {
        int total = 0;
        for (int i = 0; i < karboniteDeposits.size(); ++i) {
            total += karboniteDeposits.get(i).getSecond();
        }
        return total;
    }
}
