package com.rapitor3.riseofages.progress;

/**
 * High-level type of activity that can produce progression.
 *
 * IMPORTANT:
 * These values are intentionally generic.
 * Core should not depend on specific mods or specific block/entity implementations.
 *
 * Examples:
 * - SMITHING for an anvil interaction
 * - COOKING for cooking-related activity
 * - CARPENTRY for woodworking activity
 */
public enum ActivityType {

    /**
     * Fallback generic activity type.
     * Useful for tests or unknown integrations.
     */
    GENERIC,

    /**
     * Smithing-related activity.
     */
    SMITHING,

    /**
     * Cooking-related activity.
     */
    COOKING,

    /**
     * Carpentry-related activity.
     */
    CARPENTRY,

    /**
     * Agriculture-related activity.
     */
    AGRICULTURE,

    /**
     * Mining-related activity.
     */
    MINING,

    /**
     * Structure-related activity.
     * Example: structure registered, upgraded, or validated.
     */
    STRUCTURE
}
