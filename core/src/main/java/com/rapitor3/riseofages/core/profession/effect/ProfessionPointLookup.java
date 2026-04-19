package com.rapitor3.riseofages.core.profession.effect;

import com.rapitor3.riseofages.core.profession.ProfessionKey;

/**
 * Read-only access abstraction for invested profession points.
 *
 * <p>This interface allows profession effects to be resolved from different
 * data sources:
 * </p>
 * <ul>
 *     <li>server-side {@code ProfessionState}</li>
 *     <li>client-side synced cache</li>
 * </ul>
 *
 * <p>The effect layer should not care where the points come from.
 * It only needs a way to ask:
 * "How many invested points does this profession have?"
 * </p>
 */
@FunctionalInterface
public interface ProfessionPointLookup {

    /**
     * Returns invested points for the given profession key.
     *
     * @param professionKey profession key
     * @return invested points, never negative
     */
    int getInvestedPoints(ProfessionKey professionKey);
}