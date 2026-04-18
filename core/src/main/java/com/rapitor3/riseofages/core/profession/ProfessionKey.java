package com.rapitor3.riseofages.core.profession;

import java.util.Objects;

/**
 * Lightweight identifier of a profession track.
 *
 * IMPORTANT:
 * This is not a player's current profession state.
 * This is only a stable key used to identify a profession direction.
 *
 * Examples:
 * - extraction
 * - agriculture
 * - smithing
 * - construction
 */
public record ProfessionKey(String id) {

    /**
     * Compact constructor with validation.
     *
     * @param id unique string identifier of the profession track
     */
    public ProfessionKey {
        Objects.requireNonNull(id, "ProfessionKey.id must not be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException("ProfessionKey.id must not be blank");
        }
    }

    /**
     * Factory method for convenience.
     *
     * @param id unique profession track id
     * @return new profession key
     */
    public static ProfessionKey of(String id) {
        return new ProfessionKey(id);
    }

    @Override
    public String toString() {
        return id;
    }
}