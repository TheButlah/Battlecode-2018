
package org.battlecode.bc18.util.pathfinder;

/**
 * A Cell in a pathfinding grid.
 *
 * @author Jared Junyoung Lim
 */
public class Cell {
    int c;
    int r;
    
    Cell(int r, int c) {
        this.c = c;
        this.r = r;
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
