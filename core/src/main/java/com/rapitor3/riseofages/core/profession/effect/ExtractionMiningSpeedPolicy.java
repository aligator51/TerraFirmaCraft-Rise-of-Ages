package com.rapitor3.riseofages.core.profession.effect;

/**
 * Calculates mining speed multiplier for the extraction profession.
 *
 * <p>This class is pure core logic and does not depend on Minecraft or Forge.
 * It only translates allocated profession points into a speed multiplier.
 * </p>
 *
 * <p>Current progression:
 * <ul>
 *     <li>0 points   -> 0.90x</li>
 *     <li>1-2 points -> 1.00x</li>
 *     <li>3-5 points -> 1.10x</li>
 *     <li>6-8 points -> 1.20x</li>
 *     <li>9-10 points -> 1.30x</li>
 * </ul>
 * </p>
 */
public final class ExtractionMiningSpeedPolicy {

    /**
     * Resolves mining speed multiplier for allocated extraction points.
     *
     * @param allocatedPoints allocated extraction profession points
     * @return mining speed multiplier
     */
    public float resolveMultiplier(int allocatedPoints) {
        if (allocatedPoints <= 0) {
            return 0.10f;
        }
        if (allocatedPoints <= 2) {
            return 1.00f;
        }
        if (allocatedPoints <= 5) {
            return 1.50f;
        }
        if (allocatedPoints <= 8) {
            return 2.00f;
        }
        return 3.00f;
    }
}