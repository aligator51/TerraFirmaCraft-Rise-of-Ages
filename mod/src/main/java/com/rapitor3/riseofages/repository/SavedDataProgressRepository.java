package com.rapitor3.riseofages.repository;

import com.rapitor3.riseofages.data.CoreSavedData;
import com.rapitor3.riseofages.core.progress.SubjectProgressData;
import com.rapitor3.riseofages.core.subject.SubjectRef;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;
import java.util.Optional;

/**
 * ProgressRepository implementation backed by CoreSavedData.
 *
 * This is the default persistence strategy for the core module.
 *
 * Storage model:
 * - progression is stored in world-level SavedData
 * - data is shared across the whole save
 * - overworld storage is used as the main persistence source
 */
public class SavedDataProgressRepository implements ProgressRepository {

    /**
     * Finds progression data for a subject.
     *
     * @param level server level
     * @param subjectRef subject reference
     * @return optional progression data
     */
    @Override
    public Optional<SubjectProgressData> find(ServerLevel level, SubjectRef subjectRef) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        CoreSavedData storage = CoreSavedData.get(level);
        return storage.findSubject(subjectRef);
    }

    /**
     * Returns existing progression data or creates a new one.
     *
     * @param level server level
     * @param subjectRef subject reference
     * @return existing or newly created progression data
     */
    @Override
    public SubjectProgressData getOrCreate(ServerLevel level, SubjectRef subjectRef) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        CoreSavedData storage = CoreSavedData.get(level);
        return storage.getOrCreateSubject(subjectRef);
    }

    /**
     * Saves progression data.
     *
     * Since CoreSavedData already stores objects in-memory,
     * this method mainly ensures that storage is marked dirty
     * and the updated object is present in the subject map.
     *
     * @param level server level
     * @param data subject progression data
     */
    @Override
    public void save(ServerLevel level, SubjectProgressData data) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(data, "SubjectProgressData must not be null");

        CoreSavedData storage = CoreSavedData.get(level);
        storage.putSubject(data);
    }

    /**
     * Removes progression data for a subject.
     *
     * @param level server level
     * @param subjectRef subject reference
     * @return removed progression data if present
     */
    @Override
    public Optional<SubjectProgressData> remove(ServerLevel level, SubjectRef subjectRef) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        CoreSavedData storage = CoreSavedData.get(level);
        return storage.removeSubject(subjectRef);
    }
}