package com.rapitor3.riseofages.core.profession;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Mutable profession progression state for a specific subject.
 *
 * This class stores:
 * - accumulated experience per profession track
 * - invested specialization points per profession track
 * - total spent points across all tracks
 *
 * Design note:
 * experience and invested points are intentionally separated.
 *
 * Example:
 * a player may have 500 smithing experience,
 * but still 0 invested points if the player never pressed the upgrade button.
 */
public class ProfessionState {

    /**
     * Global maximum number of invested profession points for one subject.
     */
    public static final int GLOBAL_POINT_CAP = 10;

    private final Map<ProfessionKey, Long> experienceByProfession;
    private final Map<ProfessionKey, Integer> investedPointsByProfession;
    private int totalSpentPoints;
    private long updatedAt;

    /**
     * Creates an empty profession state.
     */
    public ProfessionState() {
        this(new LinkedHashMap<>(), new LinkedHashMap<>(), 0, System.currentTimeMillis());
    }

    /**
     * Creates a profession state with explicit values.
     *
     * @param experienceByProfession accumulated experience per profession track
     * @param investedPointsByProfession invested points per profession track
     * @param totalSpentPoints total spent points across all tracks
     * @param updatedAt last update timestamp
     */
    public ProfessionState(
            Map<ProfessionKey, Long> experienceByProfession,
            Map<ProfessionKey, Integer> investedPointsByProfession,
            int totalSpentPoints,
            long updatedAt
    ) {
        this.experienceByProfession = new LinkedHashMap<>(Objects.requireNonNull(
                experienceByProfession,
                "experienceByProfession must not be null"
        ));
        this.investedPointsByProfession = new LinkedHashMap<>(Objects.requireNonNull(
                investedPointsByProfession,
                "investedPointsByProfession must not be null"
        ));
        this.totalSpentPoints = Math.max(totalSpentPoints, 0);
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a new empty profession state.
     *
     * @return empty profession state
     */
    public static ProfessionState empty() {
        return new ProfessionState();
    }

    /**
     * Returns total accumulated experience in the given profession track.
     *
     * @param key profession track key
     * @return total experience, never negative
     */
    public long getExperience(ProfessionKey key) {
        Objects.requireNonNull(key, "ProfessionKey must not be null");
        return experienceByProfession.getOrDefault(key, 0L);
    }

    /**
     * Returns invested specialization points in the given profession track.
     *
     * @param key profession track key
     * @return invested points, never negative
     */
    public int getInvestedPoints(ProfessionKey key) {
        Objects.requireNonNull(key, "ProfessionKey must not be null");
        return investedPointsByProfession.getOrDefault(key, 0);
    }

    /**
     * Returns total spent profession points across all tracks.
     *
     * @return total spent points
     */
    public int getTotalSpentPoints() {
        return totalSpentPoints;
    }

    /**
     * Returns the remaining number of profession points.
     *
     * @return remaining points that can still be invested
     */
    public int getRemainingPoints() {
        return Math.max(GLOBAL_POINT_CAP - totalSpentPoints, 0);
    }

    /**
     * Returns the last update timestamp.
     *
     * @return epoch millis
     */
    public long getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Returns an unmodifiable view of accumulated experience.
     *
     * @return unmodifiable experience map
     */
    public Map<ProfessionKey, Long> getExperienceByProfession() {
        return Collections.unmodifiableMap(experienceByProfession);
    }

    /**
     * Returns an unmodifiable view of invested points.
     *
     * @return unmodifiable point map
     */
    public Map<ProfessionKey, Integer> getInvestedPointsByProfession() {
        return Collections.unmodifiableMap(investedPointsByProfession);
    }

    /**
     * Adds experience to the given profession track.
     *
     * @param key profession track key
     * @param amount positive experience amount
     */
    public void addExperience(ProfessionKey key, long amount) {
        Objects.requireNonNull(key, "ProfessionKey must not be null");

        if (amount <= 0L) {
            return;
        }

        long current = getExperience(key);
        experienceByProfession.put(key, current + amount);
        updatedAt = System.currentTimeMillis();
    }

    /**
     * Checks whether the subject can invest one more point into the given profession.
     *
     * Current rule set:
     * - total spent points must be below the global cap
     * - profession track must be below its max points
     * - accumulated experience must be enough for the next point
     *
     * Current threshold formula:
     * required total experience = definition.experiencePerPoint * nextPointIndex
     *
     * Example:
     * with 2 invested points and 100 expPerPoint,
     * point 3 requires at least 300 total XP.
     *
     * @param definition profession definition
     * @return true if the next point can be invested
     */
    public boolean canInvestPoint(ProfessionDefinition definition) {
        Objects.requireNonNull(definition, "ProfessionDefinition must not be null");

        if (totalSpentPoints >= GLOBAL_POINT_CAP) {
            return false;
        }

        int currentPoints = getInvestedPoints(definition.getKey());
        if (currentPoints >= definition.getMaxPoints()) {
            return false;
        }

        int nextPointIndex = currentPoints + 1;
        long requiredExperience = getRequiredExperienceForPoint(definition, nextPointIndex);
        long currentExperience = getExperience(definition.getKey());

        return currentExperience >= requiredExperience;
    }

    /**
     * Invests one specialization point into the given profession.
     *
     * @param definition profession definition
     * @throws IllegalStateException if the next point cannot be invested
     */
    public void investPoint(ProfessionDefinition definition) {
        Objects.requireNonNull(definition, "ProfessionDefinition must not be null");

        if (!canInvestPoint(definition)) {
            throw new IllegalStateException("Cannot invest point into profession: " + definition.getKey());
        }

        ProfessionKey key = definition.getKey();
        int current = getInvestedPoints(key);
        investedPointsByProfession.put(key, current + 1);
        totalSpentPoints++;
        updatedAt = System.currentTimeMillis();
    }

    /**
     * Returns true if at least one profession point was invested.
     *
     * @return true when the subject has any profession specialization
     */
    public boolean hasAnyInvestedPoints() {
        return totalSpentPoints > 0;
    }

    /**
     * Returns the experience threshold for a concrete point index.
     *
     * Point index is 1-based.
     *
     * Example:
     * point 1 -> 100
     * point 2 -> 200
     * point 3 -> 300
     *
     * @param definition profession definition
     * @param pointIndex target point index, must be at least 1
     * @return required total experience
     */
    public long getRequiredExperienceForPoint(ProfessionDefinition definition, int pointIndex) {
        Objects.requireNonNull(definition, "ProfessionDefinition must not be null");

        if (pointIndex < 1) {
            throw new IllegalArgumentException("pointIndex must be at least 1");
        }

        return definition.getExperiencePerPoint() * pointIndex;
    }

    /**
     * Returns the currently highest invested point value across all professions.
     *
     * @return highest invested point value, or 0 if nothing was invested
     */
    public int getHighestInvestedPoints() {
        int highest = 0;

        for (int value : investedPointsByProfession.values()) {
            if (value > highest) {
                highest = value;
            }
        }

        return highest;
    }

    /**
     * Resets all profession progression data.
     */
    public void reset() {
        experienceByProfession.clear();
        investedPointsByProfession.clear();
        totalSpentPoints = 0;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "ProfessionState{" +
                "experienceByProfession=" + experienceByProfession +
                ", investedPointsByProfession=" + investedPointsByProfession +
                ", totalSpentPoints=" + totalSpentPoints +
                ", updatedAt=" + updatedAt +
                '}';
    }

    /**
     * Checks whether the subject can invest one more point into the given
     * profession track using an external rule set.
     *
     * @param definition profession definition
     * @param rules progression rules
     * @return true if the next point can be invested
     */
    public boolean canInvestPoint(ProfessionDefinition definition, ProfessionProgressionRules rules) {
        Objects.requireNonNull(definition, "ProfessionDefinition must not be null");
        Objects.requireNonNull(rules, "ProfessionProgressionRules must not be null");

        return rules.canInvest(this, definition);
    }

    /**
     * Invests one specialization point into the given profession track
     * using an external rule set.
     *
     * @param definition profession definition
     * @param rules progression rules
     * @throws IllegalStateException if the next point cannot be invested
     */
    public void investPoint(ProfessionDefinition definition, ProfessionProgressionRules rules) {
        Objects.requireNonNull(definition, "ProfessionDefinition must not be null");
        Objects.requireNonNull(rules, "ProfessionProgressionRules must not be null");

        if (!canInvestPoint(definition, rules)) {
            throw new IllegalStateException("Cannot invest point into profession: " + definition.getKey());
        }

        ProfessionKey key = definition.getKey();
        int current = getInvestedPoints(key);
        investedPointsByProfession.put(key, current + 1);
        totalSpentPoints++;
        updatedAt = System.currentTimeMillis();
    }
}