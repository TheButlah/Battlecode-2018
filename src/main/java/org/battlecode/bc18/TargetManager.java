package org.battlecode.bc18;

import bc.MapLocation;
import bc.Unit;
import bc.VecUnit;
import org.battlecode.bc18.util.Utils;

import java.util.ArrayList;

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
    public static final float BASE_RESIST = 1f/32;

    /** The minimum distance that the total spread between centroids can be */
    public static final float MIN_SEPARATION = 2;

    /** The random spread of centroids (as a diameter) when they are set directly to a point. */
    private static final float RAND_SPREAD = 4;

    /** `K` centers of enemy mass */
    private final int K;

    /** Centroid positions. Shaped (K,2) where last dim is X and Y*/
    private final float[][] centroids;

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
            float xAvg = 0, yAvg = 0;
            //Loop over current centroids to get sum
            for (int j=0; j<i; j++) {
                xAvg += centroids[j][0];
                yAvg += centroids[j][1];
            }
            //Turn sum into avg
            xAvg /= i;
            yAvg /= i;
            //Fill in remaining centroids with avg position, plus random spread.
            for (; i<K; i++) {
                centroids[i][0] = xAvg + (Utils.rand.nextFloat() - 0.5f) * RAND_SPREAD;
                centroids[i][1] = yAvg + (Utils.rand.nextFloat() - 0.5f) * RAND_SPREAD;
            }
        } else if (numUnits>K) {
            //We have too many units - we have to merge the excess into the centroids
            for (int j=K; j<numUnits; j++) {
                MapLocation loc = enemies.get(j);
                updateCentroids(loc.getX(), loc.getY());
            }
        }
        //This would be if numUnits == K == i, which means we are finished!
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
        if (hasEliminatedAll) {
            //Set all centroids directly to new location, with some random spread.
            for (int i=0; i<K; i++) {
                centroids[i][0] = x + (Utils.rand.nextFloat() - 0.5f) * RAND_SPREAD;
                centroids[i][1] = y + (Utils.rand.nextFloat() - 0.5f) * RAND_SPREAD;
            }
            hasEliminatedAll = false;
            return;
        }
        int closest = -1; //Index of the closest centroid
        float closestDistSq = Float.MAX_VALUE;
        float closestDX = Float.MAX_VALUE, closestDY = Float.MAX_VALUE;
        for (int i=0; i<K; i++) {
            float dx = (centroids[i][0] - x);
            float dy = (centroids[i][1] - y);
            float distSq = dx*dx + dy*dy;
            if (distSq < closestDistSq) {
                closest = i;
                closestDistSq = distSq;
                closestDX = dx;
                closestDY = dy;
            }
        }

        //factor scales the adjustment to be less than the midpoint distance
        float factor = 1/(2+closestDistSq*BASE_RESIST); //  for 1D, this would be x += dx/(2+dx*dx/constant)
        centroids[closest][0] += closestDX * factor;
        centroids[closest][1] += closestDY * factor;
    }

    /**
     * Call this when we have reached a target but there are no enemies.
     * @param indexOfTarget The index of the target centroid in the array.
     * @return Whether all targets have been eliminated.
     */
    public boolean markTargetEliminated(int indexOfTarget) {
        float myX = centroids[indexOfTarget][0];
        float myY = centroids[indexOfTarget][1];
        ArrayList<Integer> closeCentroids = new ArrayList<>(K);
        ArrayList<Integer> farCentroids = new ArrayList<>(K);
        //Split centroids into near and far
        for (int i = 0; i<K; i++) {
            //Dont compare against ourself.
            if (i == indexOfTarget) continue;
            float dx = Math.abs(centroids[i][0] - myX);
            float dy = Math.abs(centroids[i][1] - myY);
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
                float[] loc = centroids[randIndex];
                centroids[closeIndex][0] += (loc[0]-centroids[closeIndex][0])/2;
                centroids[closeIndex][1] += (loc[1]-centroids[closeIndex][1])/2;
                i++;
            }
        } else {
            hasEliminatedAll = true;
        }
        return hasEliminatedAll;
    }

    public boolean hasEliminatedAll() {
        return hasEliminatedAll;
    }

    public int numTargets() {
        return K;
    }

}
