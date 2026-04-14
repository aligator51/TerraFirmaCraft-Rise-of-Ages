package com.rapitor3.riseofages.service;

import com.rapitor3.riseofages.core.institution.InstitutionState;
import com.rapitor3.riseofages.core.progress.ProgressEvent;
import com.rapitor3.riseofages.core.progress.SubjectProgressData;
import com.rapitor3.riseofages.repository.ProgressRepository;
import com.rapitor3.riseofages.core.subject.SubjectRef;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of ProgressService.
 * <p>
 * Responsibilities:
 * - apply progression events
 * - update institution progression
 * - delegate era recalculation to EraService
 * - persist updated subject state
 * <p>
 * This service does not access CoreSavedData directly.
 * Persistence is delegated to ProgressRepository.
 */
public class DefaultProgressService implements ProgressService {

    /**
     * Repository used to load and persist subject progression data.
     */
    private final ProgressRepository repository;

    /**
     * Service used to evaluate and persist era progression.
     */
    private final EraService eraService;

    /**
     * Creates a new progress service instance.
     *
     * @param repository  progression repository
     * @param eraService  era orchestration service
     */
    public DefaultProgressService(
            ProgressRepository repository,
            EraService eraService
    ) {
        this.repository = Objects.requireNonNull(repository, "ProgressRepository must not be null");
        this.eraService = Objects.requireNonNull(
                eraService,
                "EraService must not be null"
        );
    }

    /**
     * Records a progression event.
     * <p>
     * Flow:
     * 1. load or create subject progression data
     * 2. update target institution
     * 3. persist institution progress
     * 4. recalculate and persist era
     *
     * @param level server level
     * @param event progression event
     */
    @Override
    public void record(ServerLevel level, ProgressEvent event) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(event, "ProgressEvent must not be null");

        // Ignore no-op events early.
        if (!event.isPositive()) {
            return;
        }

        // Load or create the target subject state.
        SubjectProgressData subjectData = repository.getOrCreate(level, event.subjectRef());

        // Resolve or initialize the institution state targeted by this event.
        InstitutionState institutionState = subjectData.getOrCreateInstitution(event.institutionKey());

        /*
         * Apply raw contribution value.
         *
         * For now we convert the event amount into:
         * - total raw accumulated value
         * - simple normalized level progress
         *
         * This is placeholder balancing logic.
         * It can later be extracted into a dedicated progression calculator.
         */
        long rawValue = (long) Math.floor(event.amount());
        institutionState.addValue(rawValue);

        /*
         * Temporary progression formula:
         * every 100 points of event amount roughly correspond to one level.
         *
         * Example:
         * 5.0 -> +0.05 progress
         */
        double normalizedProgressDelta = event.amount() / 100.0D;
        institutionState.addProgress(normalizedProgressDelta);

        /*
         * Simple level-up logic.
         *
         * When progress reaches 1.0 or more:
         * - increase institution level
         * - keep overflow progress
         */
        while (institutionState.getProgress() >= 1.0D) {
            double overflow = institutionState.getProgress() - 1.0D;
            institutionState.levelUp();
            institutionState.setProgress(overflow);
        }

        // Update subject timestamp before persistence.
        subjectData.touch();

        /*
         * Recalculate era after institution progression changes.
         *
         * EraService is responsible for:
         * - applying era calculation
         * - persisting updated era state
         */
        eraService.evaluateAndSave(level, subjectData);
    }

    /**
     * Finds progression data for the given subject.
     *
     * @param level      server level
     * @param subjectRef subject reference
     * @return optional progression data
     */
    @Override
    public Optional<SubjectProgressData> find(ServerLevel level, SubjectRef subjectRef) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        return repository.find(level, subjectRef);
    }

    /**
     * Returns existing progression data or creates a new one.
     *
     * @param level      server level
     * @param subjectRef subject reference
     * @return existing or newly created progression data
     */
    @Override
    public SubjectProgressData getOrCreate(ServerLevel level, SubjectRef subjectRef) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        return repository.getOrCreate(level, subjectRef);
    }
}