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
        int desiredNumRockets = round >= 200 ? Math.min(10, AUnit.getNumUnits() / 20) : 0;
        int desiredNumHealers = AUnit.getNumUnits(UnitType.Ranger);
        //int desiredNumRangers = AUnit.getNumUnits(UnitType.Knight);
        if (getTotalUnits(UnitType.Worker) == 0) {
            return UnitType.Worker;
        }
        else if (AUnit.getNumUnits(UnitType.Rocket) < desiredNumRockets || round >= Utils.ESCAPE_TO_MARS_TURN) {
            return UnitType.Rocket;
        }
        else if (getTotalUnits(UnitType.Healer) < desiredNumHealers){
            return UnitType.Healer;
        }
        //else if (getTotalUnits(UnitType.Ranger) < desiredNumRangers){
        //    return UnitType.Ranger;
        //}
        else if ((round > 100 && getTotalUnits() < 15) || (karbonite >= 200 && AUnit.getNumUnits(UnitType.Factory) < 10)) {
            return UnitType.Factory;
        }
        else {
            return UnitType.Ranger;
        }
    }

    private static int getTotalUnits() {
        return AUnit.getNumUnits() + AUnit.getNumQueuedUnits();
    }

    private static int getTotalUnits(UnitType type) {
        return AUnit.getNumUnits(type) + AUnit.getNumQueuedUnits(type);
    }
}
