package com.rapitor3.riseofages.core.profession.effect;

import com.rapitor3.riseofages.core.profession.ProfessionKey;

import java.util.Objects;

/**
 * Default implementation of the unified profession effect layer.
 *
 * <p>This class centralizes profession effect resolution.
 * For now it resolves only extraction mining speed.
 * Future profession effects should be added here instead of being computed
 * directly inside Forge event handlers.
 * </p>
 */
public final class DefaultProfessionEffectLayer implements ProfessionEffectLayer {

    private static final ProfessionKey EXTRACTION = ProfessionKey.of("extraction");

    private final ExtractionMiningSpeedPolicy extractionMiningSpeedPolicy;

    /**
     * Creates the effect layer with default internal policies.
     */
    public DefaultProfessionEffectLayer() {
        this(new ExtractionMiningSpeedPolicy());
    }

    /**
     * Creates the effect layer with explicit policies.
     *
     * @param extractionMiningSpeedPolicy extraction mining speed policy
     */
    public DefaultProfessionEffectLayer(
            ExtractionMiningSpeedPolicy extractionMiningSpeedPolicy
    ) {
        this.extractionMiningSpeedPolicy = Objects.requireNonNull(
                extractionMiningSpeedPolicy,
                "ExtractionMiningSpeedPolicy must not be null"
        );
    }

    @Override
    public ProfessionEffectSnapshot resolve(ProfessionPointLookup pointLookup) {
        Objects.requireNonNull(pointLookup, "ProfessionPointLookup must not be null");

        int extractionPoints = Math.max(pointLookup.getInvestedPoints(EXTRACTION), 0);
        float miningSpeedMultiplier = extractionMiningSpeedPolicy.resolveMultiplier(extractionPoints);

        return new ProfessionEffectSnapshot(miningSpeedMultiplier);
    }
}