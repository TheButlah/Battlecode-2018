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
        int desiredNumRockets = round >= 100 ? Math.min(10, AUnit.getNumUnits() / 20) : 0;
        int desiredNumHealers = AUnit.getNumUnits(UnitType.Knight) / 5;
        if (AUnit.getNumUnits(UnitType.Worker) == 0) {
            return UnitType.Worker;
        }
        if ((round > 100 && AUnit.getNumUnits() < 15) || (karbonite >= 200 && AUnit.getNumUnits(UnitType.Factory) < 10)) {
            return UnitType.Factory;
        }
        else if (AUnit.getNumUnits(UnitType.Rocket) < desiredNumRockets) {
            return UnitType.Rocket;
        }
        else if (AUnit.getNumUnits(UnitType.Healer) < desiredNumHealers){
            return UnitType.Healer;
        }
        else {
            return UnitType.Knight;
        }
    }
}
