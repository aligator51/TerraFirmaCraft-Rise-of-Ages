package com.rapitor3.riseofages.core.profession.effect;

/**
 * Immutable snapshot of resolved profession gameplay effects.
 *
 * <p>This object is the unified output of the profession effect layer.
 * Event handlers and UI should consume this snapshot instead of recalculating
 * profession bonuses on their own.
 * </p>
 *
 * <p>Initially only mining speed is included.
 * Later this snapshot can be extended with:
 * </p>
 * <ul>
 *     <li>smithing quality modifiers</li>
 *     <li>durability modifiers</li>
 *     <li>agriculture yield modifiers</li>
 *     <li>food spoilage modifiers</li>
 * </ul>
 */
public record ProfessionEffectSnapshot(
        float miningSpeedMultiplier
) {

    /**
     * Returns an empty effect snapshot with neutral modifiers.
     *
     * @return neutral effect snapshot
     */
    public static ProfessionEffectSnapshot empty() {
        return new ProfessionEffectSnapshot(1.0F);
    }
}