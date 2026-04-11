package com.rapitor3.riseofages.bootstrap.era;

import com.rapitor3.riseofages.era.EraDefinition;
import com.rapitor3.riseofages.era.EraKey;
import com.rapitor3.riseofages.era.EraRegistry;

/**
 * Factory responsible for creating the default era registry
 * used by the Rise of Ages project.
 */
public final class DefaultEraRegistryFactory {

    private DefaultEraRegistryFactory() {
    }

    /**
     * Creates a registry with all built-in era definitions.
     *
     * @return initialized default era registry
     */
    public static EraRegistry create() {
        EraRegistry registry = new EraRegistry();
        registerDefaults(registry);
        return registry;
    }

    /**
     * Registers all built-in eras into the provided registry.
     *
     * @param registry target registry
     */
    public static void registerDefaults(EraRegistry registry) {
        registry.register(era("stone_age", "Stone Age", 0));
        registry.register(era("copper_age", "Copper Age", 1));
        registry.register(era("bronze_age", "Bronze Age", 2));
        registry.register(era("iron_age", "Iron Age", 3));
        registry.register(era("early_medieval", "Early Medieval Age", 4));
        registry.register(era("high_medieval", "High Medieval Age", 5));
    }

    /**
     * Convenience helper for creating era definitions.
     *
     * @param id stable era identifier
     * @param displayName human-readable era name
     * @param order progression order
     * @return new immutable era definition
     */
    private static EraDefinition era(String id, String displayName, int order) {
        return new EraDefinition(
                EraKey.of(id),
                displayName,
                order
        );
    }
}