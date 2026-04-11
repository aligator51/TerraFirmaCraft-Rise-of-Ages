package com.rapitor3.riseofages.progress;

import com.rapitor3.riseofages.era.EraState;
import com.rapitor3.riseofages.institution.InstitutionKey;
import com.rapitor3.riseofages.institution.InstitutionState;
import com.rapitor3.riseofages.subject.SubjectRef;

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
 *
 * IMPORTANT:
 * This class stores state only.
 * It does NOT define progression rules.
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
     * Last update timestamp.
     *
     * Recommended usage:
     * store epoch millis from System.currentTimeMillis().
     */
    private long updatedAt;

    /**
     * Creates a new subject progress data container.
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
        this.subjectRef = Objects.requireNonNull(subjectRef, "SubjectProgressData.subjectRef must not be null");
        this.eraState = Objects.requireNonNull(eraState, "SubjectProgressData.eraState must not be null");
        this.institutions = new HashMap<>(Objects.requireNonNull(institutions, "SubjectProgressData.institutions must not be null"));
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

    @Override
    public String toString() {
        return "SubjectProgressData{" +
                "subjectRef=" + subjectRef +
                ", eraState=" + eraState +
                ", institutions=" + institutions +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
