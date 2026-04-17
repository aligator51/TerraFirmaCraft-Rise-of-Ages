package com.rapitor3.riseofages.core.progress;

/**
 * High-level type of activity that can produce progression.
 * <p>
 * IMPORTANT:
 * These values are intentionally generic.
 * Core should not depend on specific mods or specific block/entity implementations.
 * <p>
 * Examples:
 * - SMITHING for an anvil interaction
 * - COOKING for cooking-related activity
 * - CARPENTRY for woodworking activity
 */
public enum ActivityType {

    GENERIC,
    MINING,
    LOGGING,
    GATHERING,
    COOKING,
    CRAFTING,
    SMELTING,
    FORGING,
    TOOLMAKING,
    BUILDING,
    ENGINEERING
}
