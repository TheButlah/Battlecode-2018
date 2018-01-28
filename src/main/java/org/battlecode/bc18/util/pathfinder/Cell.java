
package org.battlecode.bc18.util.pathfinder;

import bc.MapLocation;
import org.battlecode.bc18.util.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A Cell in a pathfinding grid.
 *
 * @author Jared Junyoung Lim
 */
public class Cell {

    final int c;
    final int r;
    private MapLocation loc;

    Cell(int r, int c) {
        this.r = r;
        this.c = c;
    }

    Cell(MapLocation loc) {
        this.r = loc.getY();
        this.c = loc.getX();
        this.loc = loc;
    }

    public MapLocation getLoc() {
        if (loc == null) {
            loc = new MapLocation(Utils.PLANET, c, r);
        }
        return loc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Cell)) return false;

        Cell n = (Cell) obj;
        return n.r == this.r && n.c == this.c;
    }
    
    @Override
    public int hashCode() {
        // Pairing function
        // http://www.lsi.upc.edu/~alvarez/calculabilitat/enumerabilitat.pdf
        int t = r + ((c+1)/2);
        return c + (t * t);
    }
    
    @Override
    public String toString() {
        return "Cell (" + r + "," + c + ")";
    }
}
