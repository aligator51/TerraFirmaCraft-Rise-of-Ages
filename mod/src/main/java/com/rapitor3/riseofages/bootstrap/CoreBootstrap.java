package com.rapitor3.riseofages.bootstrap;

import com.rapitor3.riseofages.bootstrap.era.DefaultEraRegistryFactory;
import com.rapitor3.riseofages.bootstrap.institution.DefaultInstitutionRegistryFactory;
import com.rapitor3.riseofages.bootstrap.profession.DefaultProfessionRegistryFactory;
import com.rapitor3.riseofages.core.era.DefaultEraCalculationService;
import com.rapitor3.riseofages.core.era.EraCalculationService;
import com.rapitor3.riseofages.core.era.EraRegistry;
import com.rapitor3.riseofages.core.institution.InstitutionRegistry;
import com.rapitor3.riseofages.core.profession.DefaultProfessionProgressionRules;
import com.rapitor3.riseofages.core.profession.ProfessionProgressionRules;
import com.rapitor3.riseofages.core.profession.ProfessionRegistry;
import com.rapitor3.riseofages.core.profession.ProfessionTitleResolver;
import com.rapitor3.riseofages.repository.ProgressRepository;
import com.rapitor3.riseofages.repository.SavedDataProgressRepository;
import com.rapitor3.riseofages.service.DefaultEraService;
import com.rapitor3.riseofages.service.DefaultProfessionService;
import com.rapitor3.riseofages.service.DefaultProgressService;
import com.rapitor3.riseofages.service.DefaultSubjectService;
import com.rapitor3.riseofages.service.EraService;
import com.rapitor3.riseofages.service.ProfessionService;
import com.rapitor3.riseofages.service.ProgressService;
import com.rapitor3.riseofages.service.SubjectService;

/**
 * Bootstrap factory for wiring the core progression system.
 *
 * <p>This class is responsible for constructing the default dependency graph
 * of the progression system.</p>
 *
 * <p>IMPORTANT:
 * Domain classes should not create their dependencies directly.
 * Wiring belongs to the bootstrap layer.</p>
 */
public final class CoreBootstrap {

    /**
     * Utility class.
     */
    private CoreBootstrap() {
    }

    /**
     * Builds the default service graph.
     *
     * <p>This includes:
     * <ul>
     *     <li>default era registry</li>
     *     <li>default institution registry</li>
     *     <li>default profession registry</li>
     *     <li>default era calculation service</li>
     *     <li>saved-data-backed progress repository</li>
     *     <li>default subject service</li>
     *     <li>default progress service</li>
     *     <li>default profession service</li>
     * </ul>
     * </p>
     *
     * @return initialized immutable core services container
     */
    public static CoreServices bootstrap() {
        EraRegistry eraRegistry = DefaultEraRegistryFactory.create();
        InstitutionRegistry institutionRegistry = DefaultInstitutionRegistryFactory.create();
        ProfessionRegistry professionRegistry = DefaultProfessionRegistryFactory.create();

        ProgressRepository progressRepository = new SavedDataProgressRepository();
        SubjectService subjectService = new DefaultSubjectService();

        EraCalculationService eraCalculationService =
                DefaultEraCalculationService.withDefaults(eraRegistry);

        EraService eraService =
                new DefaultEraService(progressRepository, eraCalculationService);

        ProgressService progressService =
                new DefaultProgressService(progressRepository, eraService);

        ProfessionProgressionRules professionProgressionRules =
                new DefaultProfessionProgressionRules();

        ProfessionTitleResolver professionTitleResolver =
                new ProfessionTitleResolver();

        ProfessionService professionService =
                new DefaultProfessionService(
                        progressRepository,
                        professionRegistry,
                        professionProgressionRules,
                        professionTitleResolver
                );

        return new CoreServices(
                eraRegistry,
                institutionRegistry,
                professionRegistry,
                progressRepository,
                subjectService,
                eraCalculationService,
                eraService,
                progressService,
                professionService
        );
    }
}