package com.rapitor3.riseofages.bootstrap;

import com.rapitor3.riseofages.bootstrap.era.DefaultEraRegistryFactory;
import com.rapitor3.riseofages.bootstrap.institution.DefaultInstitutionRegistryFactory;
import com.rapitor3.riseofages.era.DefaultEraCalculationService;
import com.rapitor3.riseofages.era.EraCalculationService;
import com.rapitor3.riseofages.era.EraRegistry;
import com.rapitor3.riseofages.institution.InstitutionRegistry;
import com.rapitor3.riseofages.repository.ProgressRepository;
import com.rapitor3.riseofages.repository.SavedDataProgressRepository;
import com.rapitor3.riseofages.service.*;

/**
 * Bootstrap factory for wiring the core progression system.
 *
 * <p>This class is responsible for constructing the default dependency graph
 * of the core module.
 * </p>
 *
 * <p>IMPORTANT:
 * Core domain classes should not create their dependencies directly.
 * Wiring belongs to the bootstrap layer.
 * </p>
 */
public final class CoreBootstrap {

    /**
     * Utility class.
     */
    private CoreBootstrap() {
    }

    /**
     * Builds the default core service graph.
     *
     * <p>This includes:
     * <ul>
     *     <li>default era registry</li>
     *     <li>default institution registry</li>
     *     <li>default era calculation service</li>
     *     <li>saved-data-backed progress repository</li>
     *     <li>default subject service</li>
     *     <li>default progress service</li>
     * </ul>
     * </p>
     *
     * @return initialized immutable core services container
     */
    public static CoreServices bootstrap() {
        EraRegistry eraRegistry = DefaultEraRegistryFactory.create();

        InstitutionRegistry institutionRegistry = DefaultInstitutionRegistryFactory.create();

        ProgressRepository progressRepository = new SavedDataProgressRepository();

        SubjectService subjectService = new DefaultSubjectService();

        EraCalculationService eraCalculationService =
                DefaultEraCalculationService.withDefaults(eraRegistry);

        EraService eraService =
                new DefaultEraService(progressRepository, eraCalculationService);

        ProgressService progressService =
                new DefaultProgressService(progressRepository, eraService);

        return new CoreServices(
                eraRegistry,
                institutionRegistry,
                progressRepository,
                subjectService,
                eraCalculationService,
                eraService,
                progressService
        );
    }
}