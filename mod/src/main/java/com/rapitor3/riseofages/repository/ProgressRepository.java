package com.rapitor3.riseofages.repository;

import com.rapitor3.riseofages.core.progress.SubjectProgressData;
import com.rapitor3.riseofages.core.subject.SubjectRef;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

/**
 * Repository abstraction for subject progression persistence.
 *
 * Responsibilities:
 * - find progression data for a subject
 * - create progression data when missing
 * - persist updated progression state
 *
 * IMPORTANT:
 * This interface hides the actual storage implementation.
 * Services should depend on this repository instead of working
 * with CoreSavedData directly.
 */
public interface ProgressRepository {

    /**
     * Finds progression data for the given subject.
     *
     * @param level server level
     * @param subjectRef subject reference
     * @return optional progression data
     */
    Optional<SubjectProgressData> find(ServerLevel level, SubjectRef subjectRef);

    /**
     * Returns existing progression data or creates a new one.
     *
     * @param level server level
     * @param subjectRef subject reference
     * @return existing or newly created subject progression data
     */
    SubjectProgressData getOrCreate(ServerLevel level, SubjectRef subjectRef);

    /**
     * Saves progression data.
     *
     * In the current implementation this may simply mark world storage as dirty,
     * but the repository interface keeps that detail hidden from services.
     *
     * @param level server level
     * @param data subject progression data
     */
    void save(ServerLevel level, SubjectProgressData data);

    /**
     * Removes progression data for the given subject.
     *
     * @param level server level
     * @param subjectRef subject reference
     * @return removed progression data if present
     */
    Optional<SubjectProgressData> remove(ServerLevel level, SubjectRef subjectRef);
}