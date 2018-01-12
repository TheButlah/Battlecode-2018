package org.battlecode.bc18;

import bc.GameController;
import org.battlecode.bc18.bots.noobbot.units.MyUnit;

import java.util.HashMap;
import java.util.Random;

public final class Utils {
    private Utils() {} //Prevent instantiation

    public static final long SEED = 8675309;
    public static final Random rand = new Random(SEED);
    
    public static GameController gc;

    public static boolean toBool(short x) {
        return x > 0;
    }
    
    public static boolean toBool(int x) {
        return x > 0;
    }

    public static boolean toBool(byte x) {
        return x > 0;
    }

    public static boolean toBool(long x) {
        return x > 0;
    }
    
}
