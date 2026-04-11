package com.rapitor3.riseofages.era;

import java.util.Objects;

/**
 * Immutable definition of a single era.
 *
 * This class describes an era as a domain concept.
 *
 * It contains:
 * - stable identifier
 * - human-readable display name
 * - progression order
 *
 * IMPORTANT:
 * This class defines metadata only.
 * It does NOT contain:
 * - subject state
 * - progression values
 * - unlock logic
 *
 * State is stored in {@link EraState}.
 */
public final class EraDefinition {

    /**
     * Stable unique identifier of the era.
     *
     * Example:
     * - stone_age
     * - copper_age
     * - bronze_age
     */
    private final EraKey key;

    /**
     * Human-readable display name.
     *
     * Example:
     * - Stone Age
     * - Copper Age
     * - Early Medieval Age
     */
    private final String displayName;

    /**
     * Absolute progression order.
     *
     * Lower value means earlier era.
     *
     * Example:
     * Stone Age        -> 0
     * Copper Age       -> 1
     * Bronze Age       -> 2
     * Iron Age         -> 3
     */
    private final int order;

    /**
     * Creates a new immutable era definition.
     *
     * @param key unique era key
     * @param displayName UI-friendly display name
     * @param order progression order
     */
    public EraDefinition(EraKey key, String displayName, int order) {
        this.key = Objects.requireNonNull(key, "EraDefinition.key must not be null");
        this.displayName = Objects.requireNonNull(displayName, "EraDefinition.displayName must not be null");

        if (displayName.isBlank()) {
            throw new IllegalArgumentException("EraDefinition.displayName must not be blank");
        }

        this.order = order;
    }

    public EraKey getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "EraDefinition{" +
                "key=" + key +
                ", displayName='" + displayName + '\'' +
                ", order=" + order +
                '}';
    }
}