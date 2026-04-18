package com.rapitor3.riseofages.core.profession;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Registry of all known profession definitions.
 */
public class ProfessionRegistry {

    private final Map<ProfessionKey, ProfessionDefinition> professions = new LinkedHashMap<>();

    /**
     * Registers a new profession definition.
     *
     * @param definition definition to register
     */
    public void register(ProfessionDefinition definition) {
        Objects.requireNonNull(definition, "ProfessionDefinition must not be null");

        if (professions.containsKey(definition.getKey())) {
            throw new IllegalStateException("Profession already registered: " + definition.getKey());
        }

        professions.put(definition.getKey(), definition);
    }

    /**
     * Finds profession definition by key.
     *
     * @param key profession identifier
     * @return optional definition
     */
    public Optional<ProfessionDefinition> get(ProfessionKey key) {
        Objects.requireNonNull(key, "ProfessionKey must not be null");
        return Optional.ofNullable(professions.get(key));
    }

    /**
     * Checks whether a profession is registered.
     *
     * @param key profession identifier
     * @return true if present
     */
    public boolean contains(ProfessionKey key) {
        Objects.requireNonNull(key, "ProfessionKey must not be null");
        return professions.containsKey(key);
    }

    /**
     * Returns all registered profession definitions.
     *
     * @return unmodifiable collection of definitions
     */
    public Collection<ProfessionDefinition> getAll() {
        return Collections.unmodifiableCollection(professions.values());
    }

    /**
     * @return number of registered professions
     */
    public int size() {
        return professions.size();
    }

    /**
     * @return true if no professions are registered
     */
    public boolean isEmpty() {
        return professions.isEmpty();
    }
}