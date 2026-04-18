package com.rapitor3.riseofages.service;

import com.rapitor3.riseofages.core.profession.ProfessionDefinition;
import com.rapitor3.riseofages.core.profession.ProfessionKey;
import com.rapitor3.riseofages.core.profession.ProfessionProgressionRules;
import com.rapitor3.riseofages.core.profession.ProfessionRegistry;
import com.rapitor3.riseofages.core.profession.ProfessionState;
import com.rapitor3.riseofages.core.profession.ProfessionTitle;
import com.rapitor3.riseofages.core.profession.ProfessionTitleResolver;
import com.rapitor3.riseofages.core.progress.ActivityType;
import com.rapitor3.riseofages.core.progress.SubjectProgressData;
import com.rapitor3.riseofages.repository.ProgressRepository;
import com.rapitor3.riseofages.core.subject.SubjectRef;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of ProfessionService.
 * <p>
 * Responsibilities:
 * - load or create subject progression data
 * - apply profession XP changes
 * - validate profession point investment through progression rules
 * - persist updated profession state
 * - resolve current profession title
 */
public class DefaultProfessionService implements ProfessionService {

    /**
     * Repository used to load and persist subject progression data.
     */
    private final ProgressRepository repository;

    /**
     * Registry of known profession definitions.
     */
    private final ProfessionRegistry professionRegistry;

    /**
     * Progression rule set used for investment validation.
     */
    private final ProfessionProgressionRules progressionRules;

    /**
     * Resolver used to build current profession title from profession state.
     */
    private final ProfessionTitleResolver titleResolver;

    /**
     * Creates a new profession service.
     *
     * @param repository         progression repository
     * @param professionRegistry profession registry
     * @param progressionRules   profession progression rules
     * @param titleResolver      profession title resolver
     */
    public DefaultProfessionService(
            ProgressRepository repository,
            ProfessionRegistry professionRegistry,
            ProfessionProgressionRules progressionRules,
            ProfessionTitleResolver titleResolver
    ) {
        this.repository = Objects.requireNonNull(repository, "ProgressRepository must not be null");
        this.professionRegistry = Objects.requireNonNull(professionRegistry, "ProfessionRegistry must not be null");
        this.progressionRules = Objects.requireNonNull(progressionRules, "ProfessionProgressionRules must not be null");
        this.titleResolver = Objects.requireNonNull(titleResolver, "ProfessionTitleResolver must not be null");
    }

    @Override
    public void addExperience(ServerLevel level, SubjectRef subjectRef, ProfessionKey professionKey, long amount) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");
        Objects.requireNonNull(professionKey, "ProfessionKey must not be null");

        if (amount <= 0L) {
            return;
        }

        ProfessionDefinition definition = requireProfession(professionKey);
        SubjectProgressData data = repository.getOrCreate(level, subjectRef);

        data.addProfessionExperience(definition.getKey(), amount);
        repository.save(level, data);
    }

    @Override
    public boolean canInvestPoint(ServerLevel level, SubjectRef subjectRef, ProfessionKey professionKey) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");
        Objects.requireNonNull(professionKey, "ProfessionKey must not be null");

        ProfessionDefinition definition = requireProfession(professionKey);
        SubjectProgressData data = repository.getOrCreate(level, subjectRef);

        return data.canInvestProfessionPoint(definition, progressionRules);
    }

    @Override
    public void investPoint(ServerLevel level, SubjectRef subjectRef, ProfessionKey professionKey) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");
        Objects.requireNonNull(professionKey, "ProfessionKey must not be null");

        ProfessionDefinition definition = requireProfession(professionKey);
        SubjectProgressData data = repository.getOrCreate(level, subjectRef);

        data.investProfessionPoint(definition, progressionRules);
        repository.save(level, data);
    }

    @Override
    public ProfessionTitle resolveTitle(ServerLevel level, SubjectRef subjectRef) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        SubjectProgressData data = repository.getOrCreate(level, subjectRef);
        return titleResolver.resolve(data.getProfessionState(), professionRegistry);
    }

    @Override
    public Optional<ProfessionState> findState(ServerLevel level, SubjectRef subjectRef) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        return repository.find(level, subjectRef)
                .map(SubjectProgressData::getProfessionState);
    }

    @Override
    public int getAllocatedPoints(ServerLevel level, SubjectRef subjectRef, ProfessionKey professionKey) {
        Objects.requireNonNull(level, "ServerLevel must not be null");
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");
        Objects.requireNonNull(professionKey, "ProfessionKey must not be null");

        SubjectProgressData data = repository.getOrCreate(level, subjectRef);
        ProfessionState professionState = data.getProfessionState();

        return professionState.getInvestedPoints(professionKey);
    }

    @Override
    public void addExperience(
            ServerLevel level,
            SubjectRef subjectRef,
            ProfessionKey professionKey,
            ActivityType activityType,
            double amount,
            String source
    ) {
        if (level == null || subjectRef == null || professionKey == null || amount <= 0.0D) {
            return;
        }

        long normalizedAmount = Math.max(1L, Math.round(amount));
        addExperience(level, subjectRef, professionKey, normalizedAmount);
    }

    /**
     * Resolves profession definition by key or throws.
     *
     * @param professionKey profession key
     * @return profession definition
     */
    protected ProfessionDefinition requireProfession(ProfessionKey professionKey) {
        return professionRegistry.get(professionKey)
                .orElseThrow(() -> new IllegalArgumentException("Unknown profession: " + professionKey));
    }
}