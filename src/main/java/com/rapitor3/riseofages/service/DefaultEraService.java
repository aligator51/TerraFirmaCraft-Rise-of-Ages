package com.rapitor3.riseofages.service;

import com.rapitor3.riseofages.era.EraCalculationResult;
import com.rapitor3.riseofages.era.EraCalculationService;
import com.rapitor3.riseofages.progress.SubjectProgressData;
import com.rapitor3.riseofages.repository.ProgressRepository;
import com.rapitor3.riseofages.subject.SubjectRef;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;
import java.util.Optional;

/**
 * Default application service for era orchestration.
 *
 * This service does not define era rules by itself.
 * It delegates actual calculation to EraCalculationService.
 */
public class DefaultEraService implements EraService {

    private final ProgressRepository repository;
    private final EraCalculationService eraCalculationService;

    public DefaultEraService(
            ProgressRepository repository,
            EraCalculationService eraCalculationService
    ) {
        this.repository = Objects.requireNonNull(repository, "ProgressRepository must not be null");
        this.eraCalculationService = Objects.requireNonNull(
                eraCalculationService,
                "EraCalculationService must not be null"
        );
    }

    @Override
    public EraCalculationResult evaluateAndSave(ServerLevel level, SubjectRef subjectRef) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        SubjectProgressData data = repository.getOrCreate(level, subjectRef);
        return evaluateAndSave(level, data);
    }

    @Override
    public EraCalculationResult evaluateAndSave(ServerLevel level, SubjectProgressData subjectData) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectData, "SubjectProgressData must not be null");

        EraCalculationResult result = eraCalculationService.apply(subjectData);
        repository.save(level, subjectData);
        return result;
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