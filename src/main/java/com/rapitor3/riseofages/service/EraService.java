package com.rapitor3.riseofages.service;

import com.rapitor3.riseofages.era.EraCalculationResult;
import com.rapitor3.riseofages.progress.SubjectProgressData;
import com.rapitor3.riseofages.subject.SubjectRef;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

/**
 * Application service for reading and updating era state.
 *
 * Responsibilities:
 * - load subject progression data
 * - evaluate/apply current era
 * - persist updated era state
 */
public interface EraService {

    /**
     * Recalculates and persists era state for the given subject.
     *
     * @param level server level
     * @param subjectRef subject reference
     * @return calculation result after application
     */
    EraCalculationResult evaluateAndSave(ServerLevel level, SubjectRef subjectRef);

    /**
     * Recalculates and persists era state for the given subjectData.
     *
     * @param level server level
     * @param subjectData subjectData reference
     * @return calculation result after application
     */
    EraCalculationResult evaluateAndSave(ServerLevel level, SubjectProgressData subjectData);

    /**
     * Finds existing subject progression data.
     *
     * @param level server level
     * @param subjectRef subject reference
     * @return optional subject data
     */
    Optional<SubjectProgressData> find(ServerLevel level, SubjectRef subjectRef);

    /**
     * Returns existing subject progression data or creates a new one.
     *
     * @param level server level
     * @param subjectRef subject reference
     * @return subject progression data
     */
    SubjectProgressData getOrCreate(ServerLevel level, SubjectRef subjectRef);
}