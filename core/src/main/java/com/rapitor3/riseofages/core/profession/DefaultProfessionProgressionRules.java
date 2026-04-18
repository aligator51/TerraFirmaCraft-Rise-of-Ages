package com.rapitor3.riseofages.core.profession;

/**
 * Default linear profession progression rules.
 *
 * Current formula:
 * required total experience = definition.experiencePerPoint * pointIndex
 *
 * Example with experiencePerPoint = 100:
 * point 1 -> 100 XP
 * point 2 -> 200 XP
 * point 3 -> 300 XP
 */
public class DefaultProfessionProgressionRules implements ProfessionProgressionRules {

    /**
     * Default total point cap for one subject.
     */
    public static final int DEFAULT_GLOBAL_POINT_CAP = 10;

    private final int globalPointCap;

    /**
     * Creates rules with the default global point cap.
     */
    public DefaultProfessionProgressionRules() {
        this(DEFAULT_GLOBAL_POINT_CAP);
    }

    /**
     * Creates rules with a custom global point cap.
     *
     * @param globalPointCap maximum total invested profession points
     */
    public DefaultProfessionProgressionRules(int globalPointCap) {
        if (globalPointCap < 1) {
            throw new IllegalArgumentException("Global point cap must be at least 1");
        }

        this.globalPointCap = globalPointCap;
    }

    @Override
    public int getGlobalPointCap() {
        return globalPointCap;
    }

    @Override
    public long getRequiredExperienceForPoint(ProfessionDefinition definition, int pointIndex) {
        if (pointIndex < 1) {
            throw new IllegalArgumentException("pointIndex must be at least 1");
        }

        return definition.getExperiencePerPoint() * pointIndex;
    }
}