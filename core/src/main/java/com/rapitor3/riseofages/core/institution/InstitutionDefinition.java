package com.rapitor3.riseofages.core.institution;

import java.util.Objects;

/**
 * Immutable definition of a single institution.
 *
 * Contains institution metadata only.
 */
public final class InstitutionDefinition {

    private final InstitutionKey key;
    private final String displayName;
    private final int maxLevel;

    public InstitutionDefinition(InstitutionKey key, String displayName, int maxLevel) {
        this.key = Objects.requireNonNull(key, "Institution key must not be null");
        this.displayName = Objects.requireNonNull(displayName, "Display name must not be null");

        if (displayName.isBlank()) {
            throw new IllegalArgumentException("Display name must not be blank");
        }

        if (maxLevel < 1) {
            throw new IllegalArgumentException("Max level must be at least 1");
        }

        this.maxLevel = maxLevel;
    }

    public InstitutionKey getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}