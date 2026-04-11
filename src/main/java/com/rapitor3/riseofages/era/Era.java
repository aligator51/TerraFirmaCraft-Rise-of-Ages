package com.rapitor3.riseofages.era;

/**
 * Represents high-level technological eras in the progression system.
 *
 * <p>
 * Eras define the overall stage of development for a subject
 * (player, group, or other entity) and are primarily used for:
 * <ul>
 *     <li>tracking long-term progression</li>
 *     <li>displaying progress in UI</li>
 *     <li>grouping technological capabilities</li>
 * </ul>
 * </p>
 *
 * <p>
 * IMPORTANT:
 * This enum is a lightweight identifier and does not contain:
 * <ul>
 *     <li>progression rules</li>
 *     <li>unlock conditions</li>
 *     <li>balancing logic</li>
 * </ul>
 *
 * All progression logic should be handled in dedicated services
 * (e.g., EraService or progression calculators).
 * </p>
 *
 * <p>
 * NOTE:
 * Naming and ordering loosely follow TerraFirmaCraft progression,
 * extended toward medieval stages.
 * </p>
 */
@Deprecated
public enum Era {

    /**
     * Primitive stage with basic survival tools and no metallurgy.
     */
    STONE,

    /**
     * Early metalworking stage using native copper.
     */
    COPPER,

    /**
     * Alloy metallurgy stage focused on bronze tools and equipment.
     */
    BRONZE,

    /**
     * Advanced metalworking stage with iron tools and infrastructure.
     */
    IRON,

    /**
     * Early medieval stage with emerging institutions and production systems.
     */
    EARLY_MEDIEVAL,

    /**
     * High medieval stage with advanced craftsmanship and complex structures.
     */
    HIGH_MEDIEVAL
}