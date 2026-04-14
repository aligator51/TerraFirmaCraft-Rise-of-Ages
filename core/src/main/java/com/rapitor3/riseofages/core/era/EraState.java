package com.rapitor3.riseofages.core.era;

import java.util.Objects;

/**
 * Mutable era state of a specific subject.
 *
 * Example:
 * - a player is currently in bronze_age
 * - a group is currently in iron_age
 *
 * This class stores only the current era state.
 * It does NOT define the rules of era progression.
 */
public class EraState {

    /**
     * Current active era of the subject.
     */
    private EraKey currentEra;

    /**
     * Progress towards the next era.
     *
     * Recommended range:
     * 0.0 .. 1.0
     *
     * Example:
     * 0.40 = 40% progress to next era
     */
    private double progressToNextEra;

    /**
     * Last update timestamp.
     *
     * Recommended usage:
     * store epoch millis from System.currentTimeMillis().
     */
    private long updatedAt;

    /**
     * Creates a new era state.
     *
     * @param currentEra current era key
     * @param progressToNextEra normalized progress to next era
     * @param updatedAt last update timestamp
     */
    public EraState(EraKey currentEra, double progressToNextEra, long updatedAt) {
        this.currentEra = Objects.requireNonNull(currentEra, "EraState.currentEra must not be null");
        this.progressToNextEra = Math.max(progressToNextEra, 0.0D);
        this.updatedAt = updatedAt;
    }

    /**
     * Creates default initial era state.
     *
     * Usually used for newly created subjects.
     *
     * @param initialEra first era of progression
     * @return new default era state
     */
    public static EraState initial(EraKey initialEra) {
        return new EraState(
                initialEra,
                0.0D,
                System.currentTimeMillis()
        );
    }

    public EraKey getCurrentEra() {
        return currentEra;
    }

    public double getProgressToNextEra() {
        return progressToNextEra;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the current era.
     *
     * @param currentEra new current era
     */
    public void setCurrentEra(EraKey currentEra) {
        this.currentEra = Objects.requireNonNull(currentEra, "EraState.currentEra must not be null");
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Sets progress towards the next era.
     *
     * Expected range is usually 0..1.
     * Values below zero will be clamped to zero.
     *
     * @param progressToNextEra normalized progress value
     */
    public void setProgressToNextEra(double progressToNextEra) {
        this.progressToNextEra = Math.max(progressToNextEra, 0.0D);
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Sets last update timestamp manually.
     *
     * Useful when restoring state from storage.
     *
     * @param updatedAt epoch millis timestamp
     */
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Advances to a new era and resets progress to next era.
     *
     * @param nextEra new current era
     */
    public void advanceTo(EraKey nextEra) {
        this.currentEra = Objects.requireNonNull(nextEra, "EraState.nextEra must not be null");
        this.progressToNextEra = 0.0D;
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Resets only progress to the next era.
     */
    public void resetProgress() {
        this.progressToNextEra = 0.0D;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "EraState{" +
                "currentEra=" + currentEra +
                ", progressToNextEra=" + progressToNextEra +
                ", updatedAt=" + updatedAt +
                '}';
    }
}