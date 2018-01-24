package org.battlecode.bc18;

import bc.*;
import org.battlecode.bc18.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manages the targets of our army. It achieves this by keeping track of a small number of "centroids"
 * that represent the center of mass of the enemy forces. These centroids are initialized at the start
 * to be the starting locations of the enemy. These centroids are updated any time we sense an enemy,
 * and they essentially are inspired by K-means clustering. Any time we call `updateCentroids()`, we
 * find the centroid closest to the new point and move it towards the midpoint between it and the new
 * point. The further the centroid and new point are, the less we move towards the midpoint.
 *
 * @author Ryan Butler
 */
public class TargetManager {

    /**
     * The base amount by which we resist moving centroids.
     * If this is 0, then we would move the centroid to the midpoint of prev loc and new point.
     */
    public static final float BASE_RESIST = 1f/4096;

    /** The minimum manhattan distance that the total spread between centroids can be */
    public static final float MIN_SEPARATION = 2;

    /** The random spread of centroids (as a diameter) when they are set directly to a point. */
    private static final float RAND_SPREAD = 4;

    public static final TargetManager tman;
    static {
        PlanetMap myMap = (Utils.PLANET == Planet.Earth) ? Utils.EARTH_START : Utils.MARS_START;
        tman = new TargetManager(myMap.getInitial_units(), 3);
    }

    /** `K` centers of enemy mass */
    private final int K;

    /** Centroid positions. Shaped (K,2) where last dim is X and Y*/
    public final float[][] centroids;

    private boolean hasEliminatedAll = false;

    /** Constructs a TargetManager.
     * @param startingUnits The initial starting units. Will use to compute centroid locations.
     * @param numCentroids The number of centroids to use.
     */
    public TargetManager(VecUnit startingUnits, int numCentroids) {
        assert numCentroids > 0;
        this.K = numCentroids;
        this.centroids = new float[K][2];

        //Collect list of starting enemies
        int numUnits = (int) startingUnits.size();
        ArrayList<MapLocation> enemies = new ArrayList<>(numUnits);
        for (int i=0; i<numUnits; i++) {
            Unit unit = startingUnits.get(i);
            if (unit.team() == Utils.TEAM) continue;
            enemies.add(unit.location().mapLocation());
        }

        numUnits = enemies.size();

        //Loop until we run out of units or hit all centroids, whichever comes first
        int i = 0; //Number of computed centroids (max `K`)
        for (; i<numUnits && i<K; i++) {
            MapLocation loc = enemies.get(i);
            centroids[i][0] = loc.getX();
            centroids[i][1] = loc.getY();
        }
        if (i<K) {
            //Still need to fill the remaining centroids - use centroid of centroids
            float xAvg = 0.01f, yAvg = 0.01f; //Slightly off of 0 to avoid any fp errors leaving map
            //Loop over current centroids to get sum
            for (int j=0; j<i; j++) {
                xAvg += centroids[j][0];
                yAvg += centroids[j][1];
            }
            //Turn sum into avg.
            xAvg /= i;
            yAvg /= i;
            //Fill in remaining centroids with avg position, plus random spread.
            for (; i<K; i++) {
                float newX = xAvg + (Utils.rand.nextFloat() - 0.5f) * RAND_SPREAD;
                float newY = yAvg + (Utils.rand.nextFloat() - 0.5f) * RAND_SPREAD;
                //Clamp it to map boundaries
                centroids[i][0] = Utils.clamp(0, Utils.MAP_WIDTH-0.01f, xAvg);
                centroids[i][1] = Utils.clamp(0, Utils.MAP_HEIGHT-0.01f, yAvg);
            }
        } else if (numUnits>K) {
            //We have too many units - we have to merge the excess into the centroids
            for (int j=K; j<numUnits; j++) {
                MapLocation loc = enemies.get(j);
                updateCentroids(loc.getX(), loc.getY());
            }
        }
        //This is when numUnits == K == i, which means we are finished!
        System.out.println(Arrays.deepToString(centroids));
    }

    /**
     * Constructs a TargetManager.
     * @param startingUnits The initial starting units. Will use to compute centroid locations.
     */
    public TargetManager(VecUnit startingUnits) {
        this(startingUnits, 3);
    }

    /**
     * Updates the centroid locations with a new point.
     */
    public void updateCentroids(float x, float y) {
        //If we have no useful centroids, move them all to the first unit we see.
        if (hasEliminatedAll) {
            //Set all centroids directly to new location, with some random spread.
            for (int i=0; i<K; i++) {
                //Compute location with spread
                float newX = x + (Utils.rand.nextFloat() - 0.5f) * RAND_SPREAD;
                float newY = y + (Utils.rand.nextFloat() - 0.5f) * RAND_SPREAD;
                //Clamp it to map boundaries
                centroids[i][0] = Utils.clamp(0, Utils.MAP_WIDTH-0.01f, x);
                centroids[i][1] = Utils.clamp(0, Utils.MAP_HEIGHT-0.01f, y);
            }
            hasEliminatedAll = false;
            return; //We just computed the centroids - we are finished.
        }

        //Find closest centroid
        int closest = -1; //Index of the closest centroid
        float closestDistSq = Float.MAX_VALUE;
        float closestDX = Float.MAX_VALUE, closestDY = Float.MAX_VALUE;
        for (int i=0; i<K; i++) {
            float dx = (x - centroids[i][0]);
            float dy = (y - centroids[i][1]);
            float distSq = dx*dx + dy*dy;
            if (distSq < closestDistSq) {
                closest = i;
                closestDistSq = distSq;
                closestDX = dx;
                closestDY = dy;
            }
        }

        //Take closest centroid and move it towards the midpoint to our location.
        //`factor` scales the adjustment to be less than the midpoint distance
        float factor = 1/(2+closestDistSq*closestDistSq*BASE_RESIST); //For 1D, this would be x += dx/(2+dx^4/constant)
        centroids[closest][0] += closestDX * factor;
        centroids[closest][1] += closestDY * factor;
    }

    /**
     * Call this when we have reached a target but there are no enemies.
     * @param targetLoc The location of the target centroid that has been eliminated.
     * @return Whether all targets have been eliminated.
     */
    public boolean markTargetEliminated(float[] targetLoc) {
        //System.out.println("Target Eliminated: " + Arrays.toString(targetLoc));
        System.out.println("E: " + Arrays.toString(targetLoc) + "\n");
        ArrayList<Integer> closeCentroids = new ArrayList<>(K);
        ArrayList<Integer> farCentroids = new ArrayList<>(K);
        //int indexOfTarget = -1; //We don't know the index yet.
        //Split centroids into near and far while simultaneously searching for our target index.
        for (int i = 0; i<K; i++) {
            float[] centroidLoc = centroids[i];
            //targetLoc references the actual subarray in `centroids` so == will work
            if (targetLoc == centroidLoc || Arrays.equals(targetLoc, centroidLoc)) {
                //In order to update our location, put it in closeCentroids.
                closeCentroids.add(i);
                //Because we know that it should be added, skip the unnecessary calculations.
                continue;
            }

            float dx = Math.abs(centroids[i][0] - targetLoc[0]);
            float dy = Math.abs(centroids[i][1] - targetLoc[1]);
            if (dx+dy < MIN_SEPARATION) closeCentroids.add(i);
            else farCentroids.add(i);
        }
        int numFar = farCentroids.size();
        if (numFar > 0) {
            //Far centroids exist, so move halfway to those
            assert !hasEliminatedAll;
            int offset = Utils.rand.nextInt(numFar);
            int i = 0;
            for (int closeIndex : closeCentroids) {
                //Pick random far centroid and move halfway to it
                int randIndex = farCentroids.get((i + offset) % numFar);
                float[] farLoc = centroids[randIndex];
                centroids[closeIndex][0] += (farLoc[0]-centroids[closeIndex][0])/2;
                centroids[closeIndex][1] += (farLoc[1]-centroids[closeIndex][1])/2;
                i++;
            }
        } else {
            hasEliminatedAll = true;
            System.out.println("HAE");
        }
        return hasEliminatedAll;
    }

    /**
     * Whether we have eliminated all of the targets.
     * True when all the targets are close together and we call `markTargetEliminated()`.
     * Will become false again when we next call `updateCentroids()`.
     */
    public boolean hasEliminatedAll() {
        //if (hasEliminatedAll) System.out.println("Has Eliminated All");
        //if (hasEliminatedAll) System.out.println("HEA");
        return hasEliminatedAll;
    }

    public int numTargets() {
        return K;
    }

    /**
     * Gets the target location based on its ID.
     * NOTE: DO NOT MODIFY. This is an actual reference to the internal location of the target.
     */
    public float[] getTarget(int targetID) {
        return centroids[targetID];
    }

}
