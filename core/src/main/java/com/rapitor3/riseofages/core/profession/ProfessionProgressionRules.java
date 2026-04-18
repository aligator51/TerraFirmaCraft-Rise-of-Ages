package com.rapitor3.riseofages.core.profession;

import java.util.Objects;

/**
 * Rule set that defines profession progression constraints.
 *
 * Responsibilities:
 * - global point cap
 * - experience requirement for each point
 * - validation of whether a point can be invested
 *
 * This interface exists to keep progression math out of services
 * and make balancing replaceable later.
 */
public interface ProfessionProgressionRules {

    /**
     * Returns the maximum total number of profession points
     * that a single subject can invest across all profession tracks.
     *
     * @return global profession point cap
     */
    int getGlobalPointCap();

    /**
     * Returns the required total profession experience
     * for a concrete point index in a concrete profession track.
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
    long getRequiredExperienceForPoint(ProfessionDefinition definition, int pointIndex);

    /**
     * Checks whether the subject can invest one more point
     * into the given profession track under the current rules.
     *
     * @param state profession state
     * @param definition profession definition
     * @return true if one more point can be invested
     */
    default boolean canInvest(ProfessionState state, ProfessionDefinition definition) {
        Objects.requireNonNull(state, "ProfessionState must not be null");
        Objects.requireNonNull(definition, "ProfessionDefinition must not be null");

        if (state.getTotalSpentPoints() >= getGlobalPointCap()) {
            return false;
        }

        int currentPoints = state.getInvestedPoints(definition.getKey());
        if (currentPoints >= definition.getMaxPoints()) {
            return false;
        }

        int nextPointIndex = currentPoints + 1;
        long requiredExperience = getRequiredExperienceForPoint(definition, nextPointIndex);
        long currentExperience = state.getExperience(definition.getKey());

        return currentExperience >= requiredExperience;
    }
}