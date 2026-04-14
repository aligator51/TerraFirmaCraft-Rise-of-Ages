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
        registry.register(institution("generic", "Generic", 10));
        registry.register(institution("smithing", "Smithing", 10));
        registry.register(institution("cooking", "Cooking", 10));
        registry.register(institution("carpentry", "Carpentry", 10));
        registry.register(institution("agriculture", "Agriculture", 10));
        registry.register(institution("mining", "Mining", 10));
        registry.register(institution("structure", "Structure", 10));
    }

    private static InstitutionDefinition institution(String id, String displayName, int maxLevel) {
        return new InstitutionDefinition(
                InstitutionKey.of(id),
                displayName,
                maxLevel
        );
    }
}