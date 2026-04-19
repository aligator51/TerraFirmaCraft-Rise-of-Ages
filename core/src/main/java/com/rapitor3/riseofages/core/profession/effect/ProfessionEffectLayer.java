package com.rapitor3.riseofages.core.profession.effect;

/**
 * Resolves gameplay effects from invested profession points.
 *
 * <p>This is the central profession effect layer.
 * All gameplay systems should use this layer as the single source of truth
 * for profession-derived bonuses and penalties.
 * </p>
 */
public interface ProfessionEffectLayer {

    /**
     * Resolves a snapshot of profession effects using the provided point lookup.
     *
     * @param pointLookup invested profession point lookup
     * @return resolved profession effect snapshot
     */
    ProfessionEffectSnapshot resolve(ProfessionPointLookup pointLookup);
}