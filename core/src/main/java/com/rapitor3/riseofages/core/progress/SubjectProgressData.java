package com.rapitor3.riseofages.core.progress;

import com.rapitor3.riseofages.core.era.EraState;
import com.rapitor3.riseofages.core.institution.InstitutionKey;
import com.rapitor3.riseofages.core.institution.InstitutionState;
import com.rapitor3.riseofages.core.profession.ProfessionDefinition;
import com.rapitor3.riseofages.core.profession.ProfessionKey;
import com.rapitor3.riseofages.core.profession.ProfessionState;
import com.rapitor3.riseofages.core.subject.SubjectRef;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Aggregated progression data of a single subject.
 *
 * A subject can be:
 * - a player
 * - a group
 * - a settlement
 *
 * This class is the main container that ties together:
 * - subject identity
 * - current era state
 * - institution progression
 * - profession progression
 *
 * IMPORTANT:
 * This class stores state only.
 * It does NOT define era or institution progression formulas.
 */
public class SubjectProgressData {

    /**
     * Reference to the subject that owns this progress data.
     */
    private final SubjectRef subjectRef;

    /**
     * Current era state of the subject.
     */
    private final EraState eraState;

    /**
     * Institution progression states owned by this subject.
     *
     * Key   -> institution identifier
     * Value -> mutable state of that institution
     */
    private final Map<InstitutionKey, InstitutionState> institutions;

    /**
     * Profession progression state of this subject.
     *
     * Stores:
     * - profession experience by track
     * - invested profession points
     * - total spent profession points
     */
    private final ProfessionState professionState;

    /**
     * Last update timestamp.
     *
     * Recommended usage:
     * store epoch millis from System.currentTimeMillis().
     */
    private long updatedAt;

    /**
     * Creates a new subject progress data container without explicit profession state.
     *
     * Backward-compatible constructor.
     * New code should prefer the full constructor with professionState.
     *
     * @param subjectRef owner subject reference
     * @param eraState current era state
     * @param institutions existing institution states
     * @param updatedAt last update timestamp
     */
    public SubjectProgressData(
            SubjectRef subjectRef,
            EraState eraState,
            Map<InstitutionKey, InstitutionState> institutions,
            long updatedAt
    ) {
        this(
                subjectRef,
                eraState,
                institutions,
                ProfessionState.empty(),
                updatedAt
        );
    }

    /**
     * Creates a new subject progress data container.
     *
     * @param subjectRef owner subject reference
     * @param eraState current era state
     * @param institutions existing institution states
     * @param professionState profession progression state
     * @param updatedAt last update timestamp
     */
    public SubjectProgressData(
            SubjectRef subjectRef,
            EraState eraState,
            Map<InstitutionKey, InstitutionState> institutions,
            ProfessionState professionState,
            long updatedAt
    ) {
        this.subjectRef = Objects.requireNonNull(subjectRef, "SubjectProgressData.subjectRef must not be null");
        this.eraState = Objects.requireNonNull(eraState, "SubjectProgressData.eraState must not be null");
        this.institutions = new HashMap<>(Objects.requireNonNull(institutions, "SubjectProgressData.institutions must not be null"));
        this.professionState = Objects.requireNonNull(professionState, "SubjectProgressData.professionState must not be null");
        this.updatedAt = updatedAt;
    }

    /**
     * Creates an empty progress container for a new subject.
     *
     * @param subjectRef owner subject reference
     * @param initialEra initial era state
     * @return empty subject progress data
     */
    public static SubjectProgressData empty(SubjectRef subjectRef, EraState initialEra) {
        return new SubjectProgressData(
                subjectRef,
                initialEra,
                new HashMap<>(),
                ProfessionState.empty(),
                System.currentTimeMillis()
        );
    }

    public SubjectRef getSubjectRef() {
        return subjectRef;
    }

    public EraState getEraState() {
        return eraState;
    }

    /**
     * Returns an unmodifiable view of all institution states.
     *
     * @return read-only collection of institution states
     */
    public Collection<InstitutionState> getInstitutions() {
        return Collections.unmodifiableCollection(institutions.values());
    }

    /**
     * Returns an unmodifiable view of raw institution map.
     *
     * Useful for serialization or debugging.
     *
     * @return read-only institution map
     */
    public Map<InstitutionKey, InstitutionState> getInstitutionMap() {
        return Collections.unmodifiableMap(institutions);
    }

    /**
     * Returns profession progression state of this subject.
     *
     * Important:
     * prefer mutating profession progression through helper methods
     * on this class so that subject updatedAt stays in sync.
     *
     * @return mutable profession state
     */
    public ProfessionState getProfessionState() {
        return professionState;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Updates the last-modified timestamp to current time.
     *
     * Should be called whenever subject progress changes.
     */
    public void touch() {
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * Manually sets the update timestamp.
     *
     * Useful when loading persisted data.
     *
     * @param updatedAt epoch millis timestamp
     */
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Finds institution state by key.
     *
     * @param key institution identifier
     * @return optional institution state
     */
    public Optional<InstitutionState> getInstitution(InstitutionKey key) {
        Objects.requireNonNull(key, "Institution key must not be null");
        return Optional.ofNullable(institutions.get(key));
    }

    /**
     * Returns existing institution state or creates a new empty one.
     *
     * This is one of the most important helper methods in the core:
     * it allows services to safely write progress without checking
     * whether institution state already exists.
     *
     * @param key institution identifier
     * @return existing or newly created institution state
     */
    public InstitutionState getOrCreateInstitution(InstitutionKey key) {
        Objects.requireNonNull(key, "Institution key must not be null");

        InstitutionState state = institutions.computeIfAbsent(
                key,
                InstitutionState::empty
        );

        touch();
        return state;
    }

    /**
     * Adds or replaces institution state.
     *
     * @param state institution state to store
     */
    public void putInstitution(InstitutionState state) {
        Objects.requireNonNull(state, "Institution state must not be null");

        institutions.put(state.getKey(), state);
        touch();
    }

    /**
     * Removes institution state by key.
     *
     * @param key institution identifier
     * @return removed state if present
     */
    public Optional<InstitutionState> removeInstitution(InstitutionKey key) {
        Objects.requireNonNull(key, "Institution key must not be null");

        InstitutionState removed = institutions.remove(key);

        if (removed != null) {
            touch();
        }

        return Optional.ofNullable(removed);
    }

    /**
     * Checks whether this subject has any progression data
     * for the given institution.
     *
     * @param key institution identifier
     * @return true if institution state exists
     */
    public boolean hasInstitution(InstitutionKey key) {
        Objects.requireNonNull(key, "Institution key must not be null");
        return institutions.containsKey(key);
    }

    /**
     * Adds profession experience into a specific profession track.
     *
     * This is MOD-SPECIFIC experience.
     * It is NOT vanilla Minecraft experience.
     *
     * @param key profession track key
     * @param amount positive experience amount
     */
    public void addProfessionExperience(ProfessionKey key, long amount) {
        Objects.requireNonNull(key, "ProfessionKey must not be null");

        professionState.addExperience(key, amount);
        touch();
    }

    /**
     * Checks whether one more profession point can be invested
     * into the given profession track.
     *
     * @param definition profession definition
     * @return true if the next point can be invested
     */
    public boolean canInvestProfessionPoint(ProfessionDefinition definition) {
        Objects.requireNonNull(definition, "ProfessionDefinition must not be null");
        return professionState.canInvestPoint(definition);
    }

    /**
     * Invests one profession point into the given profession track.
     *
     * @param definition profession definition
     * @throws IllegalStateException if the point cannot be invested
     */
    public void investProfessionPoint(ProfessionDefinition definition) {
        Objects.requireNonNull(definition, "ProfessionDefinition must not be null");

        professionState.investPoint(definition);
        touch();
    }

    /**
     * Checks whether this subject has any profession specialization.
     *
     * @return true if at least one profession point was invested
     */
    public boolean hasProfessionSpecialization() {
        return professionState.hasAnyInvestedPoints();
    }

    /**
     * Checks whether one more profession point can be invested
     * into the given profession track using an external rule set.
     *
     * @param definition profession definition
     * @param rules progression rules
     * @return true if the next point can be invested
     */
    public boolean canInvestProfessionPoint(
            ProfessionDefinition definition,
            com.rapitor3.riseofages.core.profession.ProfessionProgressionRules rules
    ) {
        Objects.requireNonNull(definition, "ProfessionDefinition must not be null");
        Objects.requireNonNull(rules, "ProfessionProgressionRules must not be null");

        return professionState.canInvestPoint(definition, rules);
    }

    /**
     * Invests one profession point into the given profession track
     * using an external rule set.
     *
     * @param definition profession definition
     * @param rules progression rules
     * @throws IllegalStateException if the point cannot be invested
     */
    public void investProfessionPoint(
            ProfessionDefinition definition,
            com.rapitor3.riseofages.core.profession.ProfessionProgressionRules rules
    ) {
        Objects.requireNonNull(definition, "ProfessionDefinition must not be null");
        Objects.requireNonNull(rules, "ProfessionProgressionRules must not be null");

        professionState.investPoint(definition, rules);
        touch();
    }

    @Override
    public String toString() {
        return "SubjectProgressData{" +
                "subjectRef=" + subjectRef +
                ", eraState=" + eraState +
                ", institutions=" + institutions +
                ", professionState=" + professionState +
                ", updatedAt=" + updatedAt +
                '}';
    }
}