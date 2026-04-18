package com.rapitor3.riseofages.bootstrap.profession;

import com.rapitor3.riseofages.core.profession.ProfessionDefinition;
import com.rapitor3.riseofages.core.profession.ProfessionKey;
import com.rapitor3.riseofages.core.profession.ProfessionRegistry;

/**
 * Factory responsible for creating the default profession registry.
 *
 * First iteration note:
 * profession tracks are intentionally aligned with major development directions.
 * This keeps the system simple and easy to connect to institutions later.
 */
public final class DefaultProfessionRegistryFactory {

    private static final int DEFAULT_MAX_POINTS = 10;
    private static final long DEFAULT_EXPERIENCE_PER_POINT = 100L;

    private DefaultProfessionRegistryFactory() {
    }

    /**
     * Creates a profession registry populated with default profession tracks.
     *
     * @return populated profession registry
     */
    public static ProfessionRegistry create() {
        ProfessionRegistry registry = new ProfessionRegistry();
        registerDefaults(registry);
        return registry;
    }

    /**
     * Registers the default profession tracks.
     *
     * @param registry target registry
     */
    public static void registerDefaults(ProfessionRegistry registry) {
        registry.register(profession("extraction", "Extraction", "Harvester"));
        registry.register(profession("metallurgy", "Metallurgy", "Smelter"));
        registry.register(profession("smithing", "Smithing", "Smith"));
        registry.register(profession("agriculture", "Agriculture", "Farmer"));
        registry.register(profession("animal_husbandry", "Animal Husbandry", "Herder"));
        registry.register(profession("foodcraft", "Foodcraft", "Cook"));
        registry.register(profession("crafts", "Crafts", "Artisan"));
        registry.register(profession("construction", "Construction", "Builder"));
        registry.register(profession("engineering", "Engineering", "Engineer"));
    }

    /**
     * Creates a default profession definition.
     *
     * @param id stable profession track id
     * @param displayName human-readable track name
     * @param professionName human-readable player title
     * @return new profession definition
     */
    private static ProfessionDefinition profession(String id, String displayName, String professionName) {
        return new ProfessionDefinition(
                ProfessionKey.of(id),
                displayName,
                professionName,
                DEFAULT_MAX_POINTS,
                DEFAULT_EXPERIENCE_PER_POINT
        );
    }
}