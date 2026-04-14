package com.rapitor3.riseofages.core.institution;

import java.util.Objects;

/**
 * Lightweight identifier of an institution.
 *
 * IMPORTANT:
 * This is not the institution state.
 * This is only a stable key used to identify an institution type.
 *
 * Examples:
 * - smithing
 * - cooking
 * - carpentry
 */
public record InstitutionKey(String id) {

    /**
     * Compact constructor with validation.
     *
     * @param id unique string identifier of the institution
     */
    public InstitutionKey {
        Objects.requireNonNull(id, "InstitutionKey.id must not be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException("InstitutionKey.id must not be blank");
        }
    }

    /**
     * Factory method for convenience.
     *
     * @param id unique institution id
     * @return new institution key
     */
    public static InstitutionKey of(String id) {
        return new InstitutionKey(id);
    }

    @Override
    public String toString() {
        return id;
    }
}