package org.battlecode.bc18;

import org.battlecode.bc18.api.AUnit;
import org.battlecode.bc18.util.Utils;

import bc.UnitType;

public class ProductionManager {
    public static UnitType getNextProductionType() {
        long round = Utils.gc.round();
        long karbonite = Utils.gc.karbonite();
        int desiredNumRockets = round >= 100 ? Math.min(10, AUnit.getNumUnits() / 10) : 0;
        if (AUnit.getNumUnits(UnitType.Worker) == 0) {
            return UnitType.Worker;
        }
        if ((round > 100 && AUnit.getNumUnits() < 10) || (karbonite >= 200 && AUnit.getNumUnits(UnitType.Factory) < 10)) {
            return UnitType.Factory;
        }
        else if (AUnit.getNumUnits(UnitType.Rocket) < desiredNumRockets) {
            return UnitType.Rocket;
        }
        else {
            return UnitType.Knight;
        }
    }
}
