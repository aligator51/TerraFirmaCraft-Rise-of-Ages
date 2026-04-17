package com.rapitor3.riseofages.bootstrap.institution;

import com.rapitor3.riseofages.core.institution.InstitutionDefinition;
import com.rapitor3.riseofages.core.institution.InstitutionKey;
import com.rapitor3.riseofages.core.institution.InstitutionRegistry;

/**
 * Factory responsible for creating the default institution registry.
 */
public final class DefaultInstitutionRegistryFactory {

    private DefaultInstitutionRegistryFactory() {
    }

    public static InstitutionRegistry create() {
        InstitutionRegistry registry = new InstitutionRegistry();
        registerDefaults(registry);
        return registry;
    }

    public static void registerDefaults(InstitutionRegistry registry) {
        registry.register(institution("extraction", "Extraction", 10));
        registry.register(institution("metallurgy", "Metallurgy", 10));
        registry.register(institution("smithing", "Smithing", 10));
        registry.register(institution("agriculture", "Agriculture", 10));
        registry.register(institution("animal_husbandry", "Animal Husbandry", 10));
        registry.register(institution("foodcraft", "Foodcraft", 10));
        registry.register(institution("crafts", "Crafts", 10));
        registry.register(institution("construction", "Construction", 10));
        registry.register(institution("engineering", "Engineering", 10));
    }

    private static InstitutionDefinition institution(String id, String displayName, int maxLevel) {
        return new InstitutionDefinition(
                InstitutionKey.of(id),
                displayName,
                maxLevel
        );
    }
}