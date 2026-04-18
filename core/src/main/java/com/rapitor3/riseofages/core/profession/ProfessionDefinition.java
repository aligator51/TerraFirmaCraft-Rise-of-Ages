package com.rapitor3.riseofages.core.profession;

import java.util.Objects;

/**
 * Immutable definition of a single profession track.
 *
 * Contains profession metadata only.
 * This class does NOT store player progression.
 */
public final class ProfessionDefinition {

    private final ProfessionKey key;
    private final String displayName;
    private final String professionName;
    private final int maxPoints;
    private final long experiencePerPoint;

    /**
     * Creates a new profession definition.
     *
     * @param key profession identifier
     * @param displayName human-readable name of the track itself
     * @param professionName human-readable title used for the player
     * @param maxPoints maximum number of invested points in this track
     * @param experiencePerPoint base experience step required for each invested point
     */
    public ProfessionDefinition(
            ProfessionKey key,
            String displayName,
            String professionName,
            int maxPoints,
            long experiencePerPoint
    ) {
        this.key = Objects.requireNonNull(key, "Profession key must not be null");
        this.displayName = Objects.requireNonNull(displayName, "Display name must not be null");
        this.professionName = Objects.requireNonNull(professionName, "Profession name must not be null");

        if (displayName.isBlank()) {
            throw new IllegalArgumentException("Display name must not be blank");
        }

        if (professionName.isBlank()) {
            throw new IllegalArgumentException("Profession name must not be blank");
        }

        if (maxPoints < 1) {
            throw new IllegalArgumentException("Max points must be at least 1");
        }

        if (experiencePerPoint < 1L) {
            throw new IllegalArgumentException("Experience per point must be at least 1");
        }

        this.maxPoints = maxPoints;
        this.experiencePerPoint = experiencePerPoint;
    }

    public ProfessionKey getKey() {
        return key;
    }

    /**
     * Returns the display name of the profession track.
     *
     * Example:
     * "Smithing"
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the player-facing profession name.
     *
     * Example:
     * "Smith"
     */
    public String getProfessionName() {
        return professionName;
    }

    /**
     * Returns the maximum number of invested points in this track.
     */
    public int getMaxPoints() {
        return maxPoints;
    }

    /**
     * Returns the experience step required for each point.
     *
     * Current formula in profession state:
     * required total experience = experiencePerPoint * nextPointIndex
     *
     * Example:
     * experiencePerPoint = 100
     * point 1 requires 100 total XP
     * point 2 requires 200 total XP
     * point 3 requires 300 total XP
     */
    public long getExperiencePerPoint() {
        return experiencePerPoint;
    }
}