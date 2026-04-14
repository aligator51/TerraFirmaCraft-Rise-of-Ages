package com.rapitor3.riseofages.service;

import com.rapitor3.riseofages.core.progress.ProgressEvent;
import com.rapitor3.riseofages.core.progress.SubjectProgressData;
import com.rapitor3.riseofages.core.subject.SubjectRef;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

/**
 * Main application service responsible for recording and reading progression.
 * <p>
 * Responsibilities:
 * - accept progression events
 * - update subject progression state
 * - provide read access to subject progression
 * <p>
 * IMPORTANT:
 * This service does not define gameplay hooks by itself.
 * External integrations are expected to create ProgressEvent instances
 * and pass them into this service.
 */
public interface ProgressService {

    /**
     * Records a progression event and applies it to subject data.
     *
     * @param level server level
     * @param event progression event
     */
    void record(ServerLevel level, ProgressEvent event);

    /**
     * Finds progression data for a subject.
     *
     * @param level server level
     * @param subjectRef subject reference
     * @return optional subject progression data
     */
    Optional<SubjectProgressData> find(ServerLevel level, SubjectRef subjectRef);

    /**
     * Returns existing progression data or creates a new one.
     *
     * @param level server level
     * @param subjectRef subject reference
     * @return existing or newly created progression data
     */
    SubjectProgressData getOrCreate(ServerLevel level, SubjectRef subjectRef);
}
