package com.rapitor3.riseofages.core.era;

import com.rapitor3.riseofages.core.progress.SubjectProgressData;

/**
 * Service responsible for evaluating era progression.
 *
 * <p>This service inspects current subject progression data and determines:
 * <ul>
 *     <li>what the current era should be</li>
 *     <li>how much progress exists towards the next era?</li>
 * </ul>
 * </p>
 *
 * <p>IMPORTANT:
 * This service does not persist changes by itself.
 * It only calculates and/or applies era state updates.
 * </p>
 */
public interface EraCalculationService {

    /**
     * Evaluates progression and returns calculation result
     * without mutating the target object.
     *
     * @param data subject progression data
     * @return calculation result
     */
    EraCalculationResult evaluate(SubjectProgressData data);

    /**
     * Evaluates progression and applies the result directly
     * to the subject's era state.
     *
     * @param data subject progression data
     * @return calculation result after application
     */
    EraCalculationResult apply(SubjectProgressData data);
}