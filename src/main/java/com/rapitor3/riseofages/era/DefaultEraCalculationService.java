package com.rapitor3.riseofages.era;

import com.rapitor3.riseofages.institution.InstitutionState;
import com.rapitor3.riseofages.progress.SubjectProgressData;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default implementation of era progression calculation.
 *
 * Current MVP strategy:
 * - calculate total institution score as the sum of institution levels
 * - compare that score against built-in progression thresholds
 * - determine current era
 * - calculate normalized progress to the next era
 *
 * IMPORTANT:
 * This is intentionally simple and should be treated as a placeholder model.
 * Later this can be replaced with more advanced rules based on:
 * - institution-specific weights
 * - milestones
 * - structures
 * - configuration / JSON data
 */
public class DefaultEraCalculationService implements EraCalculationService {

    /**
     * Ordered era registry used for progression traversal.
     */
    private final EraRegistry eraRegistry;

    /**
     * Score required to reach each era.
     *
     * Key = era id
     * Value = minimum total institution level score required
     *
     * Example:
     * bronze_age -> 5 means that total institution score
     * must be at least 5 to be in Bronze Age.
     */
    private final Map<String, Integer> eraThresholds;

    /**
     * Creates a new calculation service.
     *
     * @param eraRegistry registered eras
     * @param eraThresholds progression thresholds keyed by era id
     */
    public DefaultEraCalculationService(EraRegistry eraRegistry, Map<String, Integer> eraThresholds) {
        this.eraRegistry = Objects.requireNonNull(eraRegistry, "EraRegistry must not be null");
        this.eraThresholds = Objects.requireNonNull(eraThresholds, "eraThresholds must not be null");
    }

    /**
     * Creates a default calculation service with built-in thresholds.
     *
     * @param eraRegistry registered eras
     * @return calculation service with default thresholds
     */
    public static DefaultEraCalculationService withDefaults(EraRegistry eraRegistry) {
        return new DefaultEraCalculationService(
                eraRegistry,
                Map.of(
                        "stone_age", 0,
                        "copper_age", 2,
                        "bronze_age", 5,
                        "iron_age", 9,
                        "early_medieval", 14,
                        "high_medieval", 20
                )
        );
    }

    @Override
    public EraCalculationResult evaluate(SubjectProgressData data) {
        Objects.requireNonNull(data, "SubjectProgressData must not be null");

        int totalInstitutionScore = calculateTotalInstitutionScore(data);

        EraDefinition resolvedEra = resolveCurrentEra(totalInstitutionScore);
        EraDefinition nextEra = eraRegistry.getNext(resolvedEra.getKey()).orElse(null);

        double progressToNextEra = calculateProgressToNextEra(totalInstitutionScore, resolvedEra, nextEra);

        boolean eraChanged = !data.getEraState().getCurrentEra().equals(resolvedEra.getKey());

        return new EraCalculationResult(
                resolvedEra.getKey(),
                progressToNextEra,
                eraChanged
        );
    }

    @Override
    public EraCalculationResult apply(SubjectProgressData data) {
        Objects.requireNonNull(data, "SubjectProgressData must not be null");

        EraCalculationResult result = evaluate(data);

        if (result.isEraChanged()) {
            data.getEraState().advanceTo(result.getCurrentEra());
        }

        data.getEraState().setProgressToNextEra(result.getProgressToNextEra());
        data.touch();

        return result;
    }

    /**
     * Calculates total institution score.
     *
     * Current scoring model:
     * - each institution contributes its current level
     *
     * This is intentionally simple in the first version.
     *
     * @param data subject progression data
     * @return summed institution level score
     */
    protected int calculateTotalInstitutionScore(SubjectProgressData data) {
        int total = 0;

        for (InstitutionState institutionState : data.getInstitutions()) {
            total += institutionState.getLevel();
        }

        return total;
    }

    /**
     * Resolves current era based on total score.
     *
     * The highest era whose threshold is less than or equal to the score wins.
     *
     * @param totalInstitutionScore total score
     * @return resolved current era
     */
    protected EraDefinition resolveCurrentEra(int totalInstitutionScore) {
        List<EraDefinition> ordered = eraRegistry.getOrdered();

        EraDefinition resolved = ordered.get(0);

        for (EraDefinition definition : ordered) {
            int threshold = getThreshold(definition.getKey());

            if (totalInstitutionScore >= threshold) {
                resolved = definition;
            } else {
                break;
            }
        }

        return resolved;
    }

    /**
     * Calculates normalized progress towards the next era.
     *
     * Rules:
     * - if there is no next era -> progress is 1.0
     * - if current and next thresholds are equal -> progress is 1.0
     * - otherwise returns normalized ratio between current and next threshold
     *
     * @param totalInstitutionScore total score
     * @param currentEra resolved current era
     * @param nextEra next era, or null if current is final
     * @return normalized progress value
     */
    protected double calculateProgressToNextEra(
            int totalInstitutionScore,
            EraDefinition currentEra,
            EraDefinition nextEra
    ) {
        if (nextEra == null) {
            return 1.0D;
        }

        int currentThreshold = getThreshold(currentEra.getKey());
        int nextThreshold = getThreshold(nextEra.getKey());

        if (nextThreshold <= currentThreshold) {
            return 1.0D;
        }

        int progressInsideBand = totalInstitutionScore - currentThreshold;
        int bandSize = nextThreshold - currentThreshold;

        double normalized = (double) progressInsideBand / (double) bandSize;

        if (normalized < 0.0D) {
            return 0.0D;
        }

        if (normalized > 1.0D) {
            return 1.0D;
        }

        return normalized;
    }

    /**
     * Returns threshold value for the given era.
     *
     * @param key era key
     * @return threshold score
     * @throws IllegalStateException if threshold is missing
     */
    protected int getThreshold(EraKey key) {
        Integer value = eraThresholds.get(key.id());

        if (value == null) {
            throw new IllegalStateException("Missing era threshold for key: " + key.id());
        }

        return value;
    }
}