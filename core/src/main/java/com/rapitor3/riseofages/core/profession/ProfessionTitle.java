package com.rapitor3.riseofages.core.profession;

import java.util.Objects;

/**
 * Immutable resolved title of a subject's current profession identity.
 *
 * Example:
 * - Untrained
 * - Novice Farmer
 * - Master Smith
 * - Generalist
 */
public record ProfessionTitle(String id, String displayName) {

    /**
     * Creates a new profession title.
     *
     * @param id stable internal title id
     * @param displayName user-facing display name
     */
    public ProfessionTitle {
        Objects.requireNonNull(id, "ProfessionTitle.id must not be null");
        Objects.requireNonNull(displayName, "ProfessionTitle.displayName must not be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException("ProfessionTitle.id must not be blank");
        }

        if (displayName.isBlank()) {
            throw new IllegalArgumentException("ProfessionTitle.displayName must not be blank");
        }
    }

    /**
     * Factory method for convenience.
     *
     * @param id stable internal title id
     * @param displayName user-facing display name
     * @return new profession title
     */
    public static ProfessionTitle of(String id, String displayName) {
        return new ProfessionTitle(id, displayName);
    }
}