package com.rapitor3.riseofages.service;

import com.rapitor3.riseofages.data.CoreSavedData;
import com.rapitor3.riseofages.institution.InstitutionState;
import com.rapitor3.riseofages.progress.ProgressEvent;
import com.rapitor3.riseofages.progress.SubjectProgressData;
import com.rapitor3.riseofages.repository.ProgressRepository;
import com.rapitor3.riseofages.subject.SubjectRef;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of ProgressService.
 * <p>
 * Responsibilities:
 * - apply progression events
 * - update institution progression
 * - persist updated subject state
 * <p>
 * This service does not access CoreSavedData directly.
 * Persistence is delegated to ProgressRepository.
 */
public class DefaultProgressService implements ProgressService {

    private final ProgressRepository repository;

    /**
     * Creates progress service with explicit repository dependency.
     *
     * @param repository progression repository
     */
    public DefaultProgressService(ProgressRepository repository) {
        this.repository = Objects.requireNonNull(repository, "ProgressRepository must not be null");
    }

    @Override
    public void record(ServerLevel level, ProgressEvent event) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(event, "ProgressEvent must not be null");

        if (!event.isPositive()) {
            return;
        }

        SubjectProgressData subjectData = repository.getOrCreate(level, event.subjectRef());

        InstitutionState institutionState = subjectData.getOrCreateInstitution(event.institutionKey());

        long rawValue = (long) Math.floor(event.amount());
        institutionState.addValue(rawValue);

        double normalizedProgressDelta = event.amount() / 100.0D;
        institutionState.addProgress(normalizedProgressDelta);

        while (institutionState.getProgress() >= 1.0D) {
            institutionState.levelUp();
            institutionState.setProgress(institutionState.getProgress() - 1.0D);
        }

        subjectData.touch();

        repository.save(level, subjectData);
    }

    @Override
    public Optional<SubjectProgressData> find(ServerLevel level, SubjectRef subjectRef) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        return repository.find(level, subjectRef);
    }

    @Override
    public SubjectProgressData getOrCreate(ServerLevel level, SubjectRef subjectRef) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        return repository.getOrCreate(level, subjectRef);
    }
}