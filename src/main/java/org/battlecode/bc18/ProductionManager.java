package org.battlecode.bc18;

import org.battlecode.bc18.api.AUnit;
import org.battlecode.bc18.util.Utils;

import bc.Planet;
import bc.UnitType;


public class ProductionManager {
    public static UnitType getNextProductionType() {
        assert Utils.PLANET == Planet.Earth;
        long round = Utils.gc.round();
        long karbonite = Utils.gc.karbonite();
        int desiredNumRockets = round >= 200 ? Math.min(5, AUnit.getNumUnits() / (rushRockets() ? 10 : 40)) : 0;
        int desiredNumHealers = AUnit.getNumUnits(UnitType.Knight) / 2;
        int desiredNumRangers = 0;
        if (getTotalUnits(UnitType.Worker) == 0) {
            return UnitType.Worker;
        }
        else if (AUnit.getNumUnits(UnitType.Rocket) < desiredNumRockets) {
            return UnitType.Rocket;
        }
        else if (getTotalUnits(UnitType.Healer) < desiredNumHealers){
            return UnitType.Healer;
        }
        else if (getTotalUnits(UnitType.Ranger) < desiredNumRangers){
            return UnitType.Ranger;
        }
        else if ((round > 100 && getTotalUnits() < 15 && AUnit.getNumUnits(UnitType.Factory) < 2) || (karbonite >= 200 && AUnit.getNumUnits(UnitType.Factory) < 10)) {
            return UnitType.Factory;
        }
        else {
            return UnitType.Knight;
        }
    }

    public static boolean rushRockets() {
        return Utils.gc.round() > 650 || CentroidManager.cman.hasEliminatedAll();
    }

    private static int getTotalUnits() {
        return AUnit.getNumUnits() + AUnit.getNumQueuedUnits();
    }

    private static int getTotalUnits(UnitType type) {
        return AUnit.getNumUnits(type) + AUnit.getNumQueuedUnits(type);
    }
}
