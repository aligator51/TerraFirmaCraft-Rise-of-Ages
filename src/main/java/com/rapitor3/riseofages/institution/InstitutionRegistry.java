package com.rapitor3.riseofages.institution;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Registry of all known institution definitions.
 */
public class InstitutionRegistry {

    private final Map<InstitutionKey, InstitutionDefinition> institutions = new LinkedHashMap<>();

    public void register(InstitutionDefinition definition) {
        Objects.requireNonNull(definition, "InstitutionDefinition must not be null");

        if (institutions.containsKey(definition.getKey())) {
            throw new IllegalStateException("Institution already registered: " + definition.getKey());
        }

        institutions.put(definition.getKey(), definition);
    }

    public Optional<InstitutionDefinition> get(InstitutionKey key) {
        Objects.requireNonNull(key, "InstitutionKey must not be null");
        return Optional.ofNullable(institutions.get(key));
    }

    public boolean contains(InstitutionKey key) {
        Objects.requireNonNull(key, "InstitutionKey must not be null");
        return institutions.containsKey(key);
    }

    public Collection<InstitutionDefinition> getAll() {
        return Collections.unmodifiableCollection(institutions.values());
    }

    public int size() {
        return institutions.size();
    }

    public boolean isEmpty() {
        return institutions.isEmpty();
    }
}