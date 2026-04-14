package com.rapitor3.riseofages.core.institution;

import java.util.Objects;

/**
 * Mutable state of an institution for a specific subject.
 *
 * Example:
 * - a player has smithing level 2
 * - a group has cooking level 3
 *
 * This class stores progression data only.
 * It does NOT define progression rules.
 */
public class InstitutionState {

    /**
     * Which institution this state belongs to.
     */
    private final InstitutionKey key;

    /**
     * Current institution level.
     *
     * Example:
     * level 0 = not developed yet
     * level 1 = basic
     * level 2+ = more advanced
     */
    private int level;

    /**
     * Progress inside the current level.
     *
     * Recommended range:
     * 0.0 .. 1.0
     *
     * Example:
     * 0.25 = 25% progress to next level
     */
    private double progress;

    /**
     * Total accumulated value for this institution.
     *
     * This is the raw long-term contribution value.
     * It can be used for calculations, statistics, and era scoring.
     */
    private long totalValue;

    /**
     * Last update timestamp.
     *
     * Recommended usage:
     * store epoch millis from System.currentTimeMillis().
     */
    private long updatedAt;

    /**
     * Creates new institution state.
     *
     * @param key institution identifier
     * @param level current level
     * @param progress current progress inside the level
     * @param totalValue total accumulated value
     * @param updatedAt last update timestamp
     */
    public InstitutionState(InstitutionKey key, int level, double progress, long totalValue, long updatedAt) {
        this.key = Objects.requireNonNull(key, "InstitutionState.key must not be null");
        this.level = Math.max(level, 0);
        this.progress = Math.max(progress, 0.0D);
        this.totalValue = Math.max(totalValue, 0L);
        this.updatedAt = updatedAt;
    }

    /**
     * Creates empty state for a new institution.
     *
     * @param key institution identifier
     * @return empty institution state
     */
    public static InstitutionState empty(InstitutionKey key) {
        return new InstitutionState(
                key,
                0,
                0.0D,
                0L,
                System.currentTimeMillis()
        );
    }

    public InstitutionKey getKey() {
        return key;
    }

    public int getLevel() {
        return level;
    }

    public double getProgress() {
        return progress;
    }

    public long getTotalValue() {
        return totalValue;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the current level.
     *
     * @param level new level, cannot be negative
     */
    public void setLevel(int level) {
        this.level = Math.max(level, 0);
    }

    /**
     * Sets normalized progress value for current level.
     *
     * @param progress progress value, should usually be in range 0..1
     */
    public void setProgress(double progress) {
        this.progress = Math.max(progress, 0.0D);
    }

    /**
     * Sets total accumulated value.
     *
     * @param totalValue raw accumulated value, cannot be negative
     */
    public void setTotalValue(long totalValue) {
        this.totalValue = Math.max(totalValue, 0L);
    }

    /**
     * Sets last update timestamp.
     *
     * @param updatedAt epoch millis
     */
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Adds raw contribution value to this institution.
     *
     * @param delta amount to add
     */
    public void addValue(long delta) {
        if (delta <= 0) {
            return;
        }

        this.totalValue += delta;
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Adds progress inside the current level.
     *
     * @param delta progress delta
     */
    public void addProgress(double delta) {
        if (delta <= 0.0D) {
            return;
        }

        this.progress += delta;
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Resets only current-level progress.
     *
     * Useful after level-up.
     */
    public void resetProgress() {
        this.progress = 0.0D;
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Increases level by one.
     */
    public void levelUp() {
        this.level++;
        this.progress = 0.0D;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "InstitutionState{" +
                "key=" + key +
                ", level=" + level +
                ", progress=" + progress +
                ", totalValue=" + totalValue +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
