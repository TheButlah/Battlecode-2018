
package org.battlecode.bc18.util.pathfinder;

import bc.MapLocation;
import bc.Planet;

/**
 * Node.java
 * The Description.
 *
 * @author Jared Junyoung Lim
 * @version 1.0
 * @since 1/20/18
 */
public class Node {
    int c;
    int r;
    
    Node(int r, int c) {
        this.c = c;
        this.r = r;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Node)) return false;

        Node n = (Node) obj;
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
        return "Node (" + r + "," + c + ")";
    }
}
