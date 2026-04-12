package com.rapitor3.riseofages.bootstrap;

import com.rapitor3.riseofages.era.EraCalculationService;
import com.rapitor3.riseofages.era.EraRegistry;
import com.rapitor3.riseofages.institution.InstitutionRegistry;
import com.rapitor3.riseofages.repository.ProgressRepository;
import com.rapitor3.riseofages.service.ProgressService;
import com.rapitor3.riseofages.service.SubjectService;

import java.util.Objects;

/**
 * Immutable container for core service dependencies.
 *
 * <p>This class groups together the main services and registries
 * used by the core progression system.
 * </p>
 *
 * <p>Purpose:
 * <ul>
 *     <li>keep bootstrap logic in one place</li>
 *     <li>avoid rebuilding the same dependency graph everywhere</li>
 *     <li>provide a clean entry point for mod initialization</li>
 * </ul>
 * </p>
 */
public final class CoreServices {

    /**
     * Registry of all known era definitions.
     */
    private final EraRegistry eraRegistry;

    /**
     * Registry of all known institution definitions.
     */
    private final InstitutionRegistry institutionRegistry;

    /**
     * Repository used for persistence of subject progression.
     */
    private final ProgressRepository progressRepository;

    /**
     * Service responsible for resolving progression ownership.
     */
    private final SubjectService subjectService;

    /**
     * Service responsible for evaluating and applying era progression.
     */
    private final EraCalculationService eraCalculationService;

    /**
     * Main progression application service.
     */
    private final ProgressService progressService;

    /**
     * Creates immutable service container.
     *
     * @param eraRegistry era registry
     * @param progressRepository progress repository
     * @param subjectService subject resolution service
     * @param eraCalculationService era calculation service
     * @param progressService progress service
     */
    public CoreServices(
            EraRegistry eraRegistry,
            InstitutionRegistry institutionRegistry,
            ProgressRepository progressRepository,
            SubjectService subjectService,
            EraCalculationService eraCalculationService,
            ProgressService progressService
    ) {
        this.eraRegistry = Objects.requireNonNull(eraRegistry, "EraRegistry must not be null");
        this.institutionRegistry = Objects.requireNonNull(institutionRegistry, "InstitutionRegistry must not be null");
        this.progressRepository = Objects.requireNonNull(progressRepository, "ProgressRepository must not be null");
        this.subjectService = Objects.requireNonNull(subjectService, "SubjectService must not be null");
        this.eraCalculationService = Objects.requireNonNull(eraCalculationService, "EraCalculationService must not be null");
        this.progressService = Objects.requireNonNull(progressService, "ProgressService must not be null");
    }

    public EraRegistry getEraRegistry() {
        return eraRegistry;
    }

    public InstitutionRegistry getInstitutionRegistry(){
        return institutionRegistry;
    }

    public ProgressRepository getProgressRepository() {
        return progressRepository;
    }

    public SubjectService getSubjectService() {
        return subjectService;
    }

    public EraCalculationService getEraCalculationService() {
        return eraCalculationService;
    }

    public ProgressService getProgressService() {
        return progressService;
    }
}