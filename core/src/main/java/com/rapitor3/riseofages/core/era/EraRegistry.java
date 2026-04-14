package com.rapitor3.riseofages.core.era;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Registry of all known era definitions.
 *
 * Responsibilities:
 * - register era definitions
 * - find era by key
 * - return ordered list of eras
 * - resolve the next era in progression
 *
 * IMPORTANT:
 * This registry stores definitions, not subject state.
 * Subject progression is stored separately in {@link EraState}.
 */
public class EraRegistry {

    /**
     * Era definitions indexed by era key.
     *
     * LinkedHashMap is used to preserve registration order,
     * although sorted queries should still rely on explicit "order".
     */
    private final Map<EraKey, EraDefinition> eras = new LinkedHashMap<>();

    /**
     * Registers a new era definition.
     *
     * @param definition era definition to register
     * @throws IllegalStateException if the same era key is already registered
     */
    public void register(EraDefinition definition) {
        Objects.requireNonNull(definition, "EraDefinition must not be null");

        if (eras.containsKey(definition.getKey())) {
            throw new IllegalStateException("Era already registered: " + definition.getKey());
        }

        eras.put(definition.getKey(), definition);
    }

    /**
     * Finds an era definition by key.
     *
     * @param key era key
     * @return optional era definition
     */
    public Optional<EraDefinition> get(EraKey key) {
        Objects.requireNonNull(key, "EraKey must not be null");
        return Optional.ofNullable(eras.get(key));
    }

    /**
     * Returns true if the given era key is registered.
     *
     * @param key era key
     * @return true if registry contains this era
     */
    public boolean contains(EraKey key) {
        Objects.requireNonNull(key, "EraKey must not be null");
        return eras.containsKey(key);
    }

    /**
     * Returns all registered eras as an unmodifiable collection.
     *
     * Registration order is preserved, but callers should prefer
     * {@link #getOrdered()} when order matters semantically.
     *
     * @return read-only collection of era definitions
     */
    public Collection<EraDefinition> getAll() {
        return Collections.unmodifiableCollection(eras.values());
    }

    /**
     * Returns all registered eras sorted by progression order.
     *
     * @return ordered read-only list of eras
     */
    public List<EraDefinition> getOrdered() {
        List<EraDefinition> ordered = new ArrayList<>(eras.values());
        ordered.sort(Comparator.comparingInt(EraDefinition::getOrder));
        return Collections.unmodifiableList(ordered);
    }

    /**
     * Returns the next era after the given key.
     *
     * If the given era is the last one, returns empty.
     *
     * @param currentKey current era key
     * @return optional next era definition
     */
    public Optional<EraDefinition> getNext(EraKey currentKey) {
        Objects.requireNonNull(currentKey, "Current era key must not be null");

        EraDefinition current = eras.get(currentKey);
        if (current == null) {
            return Optional.empty();
        }

        return getOrdered().stream()
                .filter(definition -> definition.getOrder() > current.getOrder())
                .findFirst();
    }

    /**
     * Returns the previous era before the given key.
     *
     * If the given era is the first one, returns empty.
     *
     * @param currentKey current era key
     * @return optional previous era definition
     */
    public Optional<EraDefinition> getPrevious(EraKey currentKey) {
        Objects.requireNonNull(currentKey, "Current era key must not be null");

        EraDefinition current = eras.get(currentKey);
        if (current == null) {
            return Optional.empty();
        }

        List<EraDefinition> ordered = getOrdered();

        EraDefinition previous = null;
        for (EraDefinition definition : ordered) {
            if (definition.getOrder() == current.getOrder()) {
                return Optional.ofNullable(previous);
            }
            previous = definition;
        }

        return Optional.empty();
    }

    /**
     * Returns the first era in progression order.
     *
     * @return optional first era
     */
    public Optional<EraDefinition> getFirst() {
        return getOrdered().stream().findFirst();
    }

    /**
     * Returns the last era in progression order.
     *
     * @return optional last era
     */
    public Optional<EraDefinition> getLast() {
        List<EraDefinition> ordered = getOrdered();
        if (ordered.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ordered.get(ordered.size() - 1));
    }

    /**
     * @return number of registered eras
     */
    public int size() {
        return eras.size();
    }

    /**
     * Returns true if no eras are registered.
     *
     * @return true if registry is empty
     */
    public boolean isEmpty() {
        return eras.isEmpty();
    }
}