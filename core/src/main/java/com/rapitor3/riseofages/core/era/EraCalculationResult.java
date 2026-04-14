package com.rapitor3.riseofages.core.era;

import java.util.Objects;

/**
 * Result of era progression calculation.
 *
 * Contains:
 * - current era after evaluation
 * - normalized progress towards the next era
 * - whether era changed during calculation
 */
public final class EraCalculationResult {

    /**
     * Current era after calculation.
     */
    private final EraKey currentEra;

    /**
     * Progress towards the next era.
     *
     * Expected range:
     * 0.0 .. 1.0
     */
    private final double progressToNextEra;

    /**
     * Indicates whether the subject advanced to a new era.
     */
    private final boolean eraChanged;

    /**
     * Creates a new immutable calculation result.
     *
     * @param currentEra current era after calculation
     * @param progressToNextEra normalized progress to next era
     * @param eraChanged true if era changed
     */
    public EraCalculationResult(EraKey currentEra, double progressToNextEra, boolean eraChanged) {
        this.currentEra = Objects.requireNonNull(currentEra, "currentEra must not be null");
        this.progressToNextEra = Math.max(progressToNextEra, 0.0D);
        this.eraChanged = eraChanged;
    }

    public EraKey getCurrentEra() {
        return currentEra;
    }

    public double getProgressToNextEra() {
        return progressToNextEra;
    }

    public boolean isEraChanged() {
        return eraChanged;
    }

    @Override
    public String toString() {
        return "EraCalculationResult{" +
                "currentEra=" + currentEra +
                ", progressToNextEra=" + progressToNextEra +
                ", eraChanged=" + eraChanged +
                '}';
    }
}