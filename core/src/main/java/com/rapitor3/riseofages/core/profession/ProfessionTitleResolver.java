package com.rapitor3.riseofages.core.profession;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Resolves a player-facing profession title from invested profession points.
 *
 * Current resolver rules:
 * - no invested points -> Untrained
 * - one dominant track -> rank prefix + profession name
 * - tie on the highest invested points -> Generalist
 *
 * This resolver is intentionally simple for the first iteration.
 * More advanced hybrid titles can be added later.
 */
public class ProfessionTitleResolver {

    /**
     * Default title used when the subject has not invested any profession points.
     */
    public static final ProfessionTitle UNTRAINED = ProfessionTitle.of("untrained", "Untrained");

    /**
     * Default title used when the subject has multiple equally dominant tracks.
     */
    public static final ProfessionTitle GENERALIST = ProfessionTitle.of("generalist", "Generalist");

    /**
     * Resolves the current profession title from profession state and registry.
     *
     * @param state profession state
     * @param registry profession registry
     * @return resolved profession title
     */
    public ProfessionTitle resolve(ProfessionState state, ProfessionRegistry registry) {
        Objects.requireNonNull(state, "ProfessionState must not be null");
        Objects.requireNonNull(registry, "ProfessionRegistry must not be null");

        if (!state.hasAnyInvestedPoints()) {
            return UNTRAINED;
        }

        int highestPoints = state.getHighestInvestedPoints();
        if (highestPoints <= 0) {
            return UNTRAINED;
        }

        List<ProfessionDefinition> leaders = new ArrayList<>();
        for (ProfessionDefinition definition : registry.getAll()) {
            int points = state.getInvestedPoints(definition.getKey());
            if (points == highestPoints) {
                leaders.add(definition);
            }
        }

        if (leaders.isEmpty()) {
            return UNTRAINED;
        }

        if (leaders.size() > 1) {
            return GENERALIST;
        }

        ProfessionDefinition primary = leaders.get(0);
        String rankPrefix = resolveRankPrefix(highestPoints);
        String displayName = rankPrefix + " " + primary.getProfessionName();
        String id = primary.getKey().id() + "_" + highestPoints;

        return ProfessionTitle.of(id, displayName);
    }

    /**
     * Resolves a coarse rank prefix from invested point count.
     *
     * @param investedPoints invested points in the dominant profession track
     * @return rank prefix
     */
    protected String resolveRankPrefix(int investedPoints) {
        if (investedPoints >= 10) {
            return "Legendary";
        }

        if (investedPoints >= 7) {
            return "Master";
        }

        if (investedPoints >= 5) {
            return "Specialist";
        }

        if (investedPoints >= 3) {
            return "Skilled";
        }

        return "Novice";
    }
}