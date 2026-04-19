package com.rapitor3.riseofages.gameplay.profession;

import com.rapitor3.riseofages.core.profession.ProfessionKey;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Client-side cache of profession progression data.
 *
 * <p>This state exists only on the physical client and is intended for
 * gameplay effects that must be evaluated locally, such as mining speed
 * adjustments in {@code PlayerEvent.BreakSpeed}.
 * </p>
 *
 * <p>Important:
 * this class is not the source of truth.
 * The authoritative profession progression remains on the server.
 * This cache only mirrors the subset of data required by client-side effects.
 * </p>
 *
 * <p>Current cached data:
 * <ul>
 *     <li>invested profession points by profession key</li>
 * </ul>
 * </p>
 */
public final class ClientProfessionState {

    /**
     * Cached invested points per profession.
     */
    private static final Map<ProfessionKey, Integer> INVESTED_POINTS = new LinkedHashMap<>();

    /**
     * Last update timestamp from the latest sync operation.
     */
    private static long updatedAt;

    /**
     * Utility class.
     */
    private ClientProfessionState() {
    }

    /**
     * Returns invested points for the given profession.
     *
     * @param professionKey profession key
     * @return invested points, or 0 when absent
     */
    public static int getInvestedPoints(ProfessionKey professionKey) {
        Objects.requireNonNull(professionKey, "ProfessionKey must not be null");
        return INVESTED_POINTS.getOrDefault(professionKey, 0);
    }

    /**
     * Replaces the entire client cache with synced profession point data.
     *
     * @param investedPoints synced invested points map
     */
    public static void replace(Map<ProfessionKey, Integer> investedPoints) {
        Objects.requireNonNull(investedPoints, "investedPoints must not be null");

        INVESTED_POINTS.clear();

        for (Map.Entry<ProfessionKey, Integer> entry : investedPoints.entrySet()) {
            ProfessionKey key = Objects.requireNonNull(entry.getKey(), "ProfessionKey must not be null");
            int value = Math.max(entry.getValue() == null ? 0 : entry.getValue(), 0);
            INVESTED_POINTS.put(key, value);
        }

        updatedAt = System.currentTimeMillis();
    }

    /**
     * Updates one profession entry in the client cache.
     *
     * @param professionKey profession key
     * @param investedPoints invested points value
     */
    public static void setInvestedPoints(ProfessionKey professionKey, int investedPoints) {
        Objects.requireNonNull(professionKey, "ProfessionKey must not be null");

        INVESTED_POINTS.put(professionKey, Math.max(investedPoints, 0));
        updatedAt = System.currentTimeMillis();
    }

    /**
     * Returns an immutable snapshot of invested points.
     *
     * @return immutable invested points map
     */
    public static Map<ProfessionKey, Integer> getInvestedPointsSnapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(INVESTED_POINTS));
    }

    /**
     * Returns the last client cache update timestamp.
     *
     * @return epoch millis of the last sync or update
     */
    public static long getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Clears all cached client profession data.
     */
    public static void clear() {
        INVESTED_POINTS.clear();
        updatedAt = System.currentTimeMillis();
    }

    /**
     * Returns true when the cache contains any profession data.
     *
     * @return true if any client-side profession points are present
     */
    public static boolean isEmpty() {
        return INVESTED_POINTS.isEmpty();
    }
}