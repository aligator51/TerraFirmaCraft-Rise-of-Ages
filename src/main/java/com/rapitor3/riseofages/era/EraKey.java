package com.rapitor3.riseofages.era;

import java.util.Objects;

/**
 * Lightweight identifier of an era.
 *
 * IMPORTANT:
 * This is not an era state.
 * This is only a stable key used to identify an era type.
 *
 * Examples:
 * - stone_age
 * - copper_age
 * - bronze_age
 * - iron_age
 * - early_medieval
 * - high_medieval
 */
public record EraKey(String id) {

    /**
     * Compact constructor with validation.
     *
     * @param id unique string identifier of the era
     */
    public EraKey {
        Objects.requireNonNull(id, "EraKey.id must not be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException("EraKey.id must not be blank");
        }
    }

    /**
     * Factory method for convenience.
     *
     * @param id unique era id
     * @return new era key
     */
    public static EraKey of(String id) {
        return new EraKey(id);
    }

    @Override
    public String toString() {
        return id;
    }
}