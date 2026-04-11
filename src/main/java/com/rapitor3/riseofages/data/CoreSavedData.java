package com.rapitor3.riseofages.data;

import com.rapitor3.riseofages.era.EraKey;
import com.rapitor3.riseofages.era.EraState;
import com.rapitor3.riseofages.institution.InstitutionKey;
import com.rapitor3.riseofages.institution.InstitutionState;
import com.rapitor3.riseofages.progress.SubjectProgressData;
import com.rapitor3.riseofages.subject.SubjectRef;
import com.rapitor3.riseofages.subject.SubjectType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Main persistent storage for Rise of Ages core progression data.
 *
 * This class is the single world-level storage entry point for:
 * - subject progression states
 * - player -> subject bindings
 *
 * IMPORTANT:
 * This storage is intended to live on the server side only.
 * It should usually be accessed through the overworld data storage.
 */
public class CoreSavedData extends SavedData {

    /**
     * File name used by Minecraft world data storage.
     */
    public static final String DATA_NAME = "riseofages_core";

    /**
     * All progression data indexed by subject UUID.
     *
     * Key:
     * - SubjectRef.id()
     *
     * Value:
     * - full progression state of that subject
     */
    private final Map<UUID, SubjectProgressData> subjects = new HashMap<>();

    /**
     * Player -> subject binding.
     *
     * Used to resolve progression ownership.
     *
     * Example:
     * - player UUID -> player subject
     * - player UUID -> group subject
     */
    private final Map<UUID, SubjectRef> playerSubjects = new HashMap<>();

    /**
     * Creates empty storage instance.
     */
    public CoreSavedData() {
    }

    /**
     * Returns shared core saved data from overworld storage.
     *
     * Why overworld:
     * - progression is global for the whole save
     * - we do not want separate progression storages per dimension
     *
     * @param level any server level
     * @return core saved data instance
     */
    public static CoreSavedData get(ServerLevel level) {
        Objects.requireNonNull(level, "ServerLevel must not be null");

        ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);
        Objects.requireNonNull(overworld, "Overworld must not be null");

        return overworld.getDataStorage().computeIfAbsent(
                CoreSavedData::load,
                CoreSavedData::new,
                DATA_NAME
        );
    }

    /**
     * Loads saved data from NBT.
     *
     * @param tag root NBT tag
     * @return loaded storage instance
     */
    public static CoreSavedData load(CompoundTag tag) {
        CoreSavedData data = new CoreSavedData();

        // -----------------------------
        // Load subject progression data
        // -----------------------------
        ListTag subjectsList = tag.getList("subjects", Tag.TAG_COMPOUND);
        for (Tag element : subjectsList) {
            CompoundTag subjectTag = (CompoundTag) element;
            SubjectProgressData progressData = readSubjectProgressData(subjectTag);
            data.subjects.put(progressData.getSubjectRef().id(), progressData);
        }

        // -----------------------------
        // Load player -> subject bindings
        // -----------------------------
        ListTag bindingsList = tag.getList("player_subjects", Tag.TAG_COMPOUND);
        for (Tag element : bindingsList) {
            CompoundTag bindingTag = (CompoundTag) element;

            UUID playerId = bindingTag.getUUID("player_id");
            SubjectRef subjectRef = readSubjectRef(bindingTag.getCompound("subject"));

            data.playerSubjects.put(playerId, subjectRef);
        }

        return data;
    }

    /**
     * Saves current state to NBT.
     *
     * @param tag root NBT tag
     * @return modified tag
     */
    @Override
    public CompoundTag save(CompoundTag tag) {
        // -----------------------------
        // Save subject progression data
        // -----------------------------
        ListTag subjectsList = new ListTag();
        for (SubjectProgressData progressData : subjects.values()) {
            subjectsList.add(writeSubjectProgressData(progressData));
        }
        tag.put("subjects", subjectsList);

        // -----------------------------
        // Save player -> subject bindings
        // -----------------------------
        ListTag bindingsList = new ListTag();
        for (Map.Entry<UUID, SubjectRef> entry : playerSubjects.entrySet()) {
            CompoundTag bindingTag = new CompoundTag();
            bindingTag.putUUID("player_id", entry.getKey());
            bindingTag.put("subject", writeSubjectRef(entry.getValue()));
            bindingsList.add(bindingTag);
        }
        tag.put("player_subjects", bindingsList);

        return tag;
    }

    /**
     * Finds progression data for a subject.
     *
     * @param subjectRef subject reference
     * @return optional progression data
     */
    public Optional<SubjectProgressData> findSubject(SubjectRef subjectRef) {
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");
        return Optional.ofNullable(subjects.get(subjectRef.id()));
    }

    /**
     * Returns existing progression data or creates a new empty one.
     *
     * New subjects start in stone_age by default.
     *
     * @param subjectRef subject reference
     * @return existing or newly created progression data
     */
    public SubjectProgressData getOrCreateSubject(SubjectRef subjectRef) {
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        SubjectProgressData data = subjects.computeIfAbsent(
                subjectRef.id(),
                ignored -> SubjectProgressData.empty(
                        subjectRef,
                        EraState.initial(EraKey.of("stone_age"))
                )
        );

        setDirty();
        return data;
    }

    /**
     * Stores or replaces progression data for a subject.
     *
     * @param progressData subject progression data
     */
    public void putSubject(SubjectProgressData progressData) {
        Objects.requireNonNull(progressData, "SubjectProgressData must not be null");
        subjects.put(progressData.getSubjectRef().id(), progressData);
        setDirty();
    }

    /**
     * Removes progression data for a subject.
     *
     * @param subjectRef subject reference
     * @return removed progression data if present
     */
    public Optional<SubjectProgressData> removeSubject(SubjectRef subjectRef) {
        Objects.requireNonNull(subjectRef, "SubjectRef must not be null");

        SubjectProgressData removed = subjects.remove(subjectRef.id());
        if (removed != null) {
            setDirty();
        }

        return Optional.ofNullable(removed);
    }

    /**
     * Binds a player to a subject.
     *
     * Example:
     * - player -> own personal subject
     * - player -> group subject
     *
     * @param playerId player UUID
     * @param subjectRef subject reference
     */
    public void bindPlayerToSubject(UUID playerId, SubjectRef subjectRef) {
        Objects.requireNonNull(playerId, "playerId must not be null");
        Objects.requireNonNull(subjectRef, "subjectRef must not be null");

        playerSubjects.put(playerId, subjectRef);
        setDirty();
    }

    /**
     * Finds currently bound subject for player.
     *
     * @param playerId player UUID
     * @return optional subject reference
     */
    public Optional<SubjectRef> findPlayerSubject(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId must not be null");
        return Optional.ofNullable(playerSubjects.get(playerId));
    }

    /**
     * Removes player -> subject binding.
     *
     * @param playerId player UUID
     * @return removed subject reference if present
     */
    public Optional<SubjectRef> unbindPlayerSubject(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId must not be null");

        SubjectRef removed = playerSubjects.remove(playerId);
        if (removed != null) {
            setDirty();
        }

        return Optional.ofNullable(removed);
    }

    /**
     * @return number of stored subjects
     */
    public int getSubjectCount() {
        return subjects.size();
    }

    /**
     * @return number of stored player bindings
     */
    public int getPlayerBindingCount() {
        return playerSubjects.size();
    }

    // =========================================================
    // =================== NBT SERIALIZATION ====================
    // =========================================================

    /**
     * Serializes subject progression data into NBT.
     *
     * @param data subject progression data
     * @return serialized NBT tag
     */
    private static CompoundTag writeSubjectProgressData(SubjectProgressData data) {
        CompoundTag tag = new CompoundTag();

        tag.put("subject", writeSubjectRef(data.getSubjectRef()));
        tag.put("era_state", writeEraState(data.getEraState()));
        tag.putLong("updated_at", data.getUpdatedAt());

        ListTag institutionsList = new ListTag();
        for (InstitutionState institutionState : data.getInstitutions()) {
            institutionsList.add(writeInstitutionState(institutionState));
        }
        tag.put("institutions", institutionsList);

        return tag;
    }

    /**
     * Deserializes subject progression data from NBT.
     *
     * @param tag source NBT tag
     * @return parsed subject progression data
     */
    private static SubjectProgressData readSubjectProgressData(CompoundTag tag) {
        SubjectRef subjectRef = readSubjectRef(tag.getCompound("subject"));
        EraState eraState = readEraState(tag.getCompound("era_state"));
        long updatedAt = tag.getLong("updated_at");

        Map<InstitutionKey, InstitutionState> institutions = new HashMap<>();
        ListTag institutionsList = tag.getList("institutions", Tag.TAG_COMPOUND);
        for (Tag element : institutionsList) {
            InstitutionState institutionState = readInstitutionState((CompoundTag) element);
            institutions.put(institutionState.getKey(), institutionState);
        }

        return new SubjectProgressData(
                subjectRef,
                eraState,
                institutions,
                updatedAt
        );
    }

    /**
     * Serializes subject reference into NBT.
     *
     * @param ref subject reference
     * @return serialized NBT tag
     */
    private static CompoundTag writeSubjectRef(SubjectRef ref) {
        CompoundTag tag = new CompoundTag();

        tag.putUUID("id", ref.id());
        tag.putString("type", ref.type().name());

        if (ref.providerId() != null) {
            tag.putString("provider_id", ref.providerId());
        }

        if (ref.externalId() != null) {
            tag.putString("external_id", ref.externalId());
        }

        return tag;
    }

    /**
     * Deserializes subject reference from NBT.
     *
     * @param tag source NBT tag
     * @return parsed subject reference
     */
    private static SubjectRef readSubjectRef(CompoundTag tag) {
        UUID id = tag.getUUID("id");
        SubjectType type = SubjectType.valueOf(tag.getString("type"));

        String providerId = tag.contains("provider_id") ? tag.getString("provider_id") : null;
        String externalId = tag.contains("external_id") ? tag.getString("external_id") : null;

        return new SubjectRef(id, type, providerId, externalId);
    }

    /**
     * Serializes era state into NBT.
     *
     * @param eraState era state
     * @return serialized NBT tag
     */
    private static CompoundTag writeEraState(EraState eraState) {
        CompoundTag tag = new CompoundTag();

        tag.putString("current_era", eraState.getCurrentEra().id());
        tag.putDouble("progress_to_next_era", eraState.getProgressToNextEra());
        tag.putLong("updated_at", eraState.getUpdatedAt());

        return tag;
    }

    /**
     * Deserializes era state from NBT.
     *
     * @param tag source NBT tag
     * @return parsed era state
     */
    private static EraState readEraState(CompoundTag tag) {
        EraKey currentEra = EraKey.of(tag.getString("current_era"));
        double progressToNextEra = tag.getDouble("progress_to_next_era");
        long updatedAt = tag.getLong("updated_at");

        return new EraState(currentEra, progressToNextEra, updatedAt);
    }

    /**
     * Serializes institution state into NBT.
     *
     * @param state institution state
     * @return serialized NBT tag
     */
    private static CompoundTag writeInstitutionState(InstitutionState state) {
        CompoundTag tag = new CompoundTag();

        tag.putString("key", state.getKey().id());
        tag.putInt("level", state.getLevel());
        tag.putDouble("progress", state.getProgress());
        tag.putLong("total_value", state.getTotalValue());
        tag.putLong("updated_at", state.getUpdatedAt());

        return tag;
    }

    /**
     * Deserializes institution state from NBT.
     *
     * @param tag source NBT tag
     * @return parsed institution state
     */
    private static InstitutionState readInstitutionState(CompoundTag tag) {
        InstitutionKey key = InstitutionKey.of(tag.getString("key"));
        int level = tag.getInt("level");
        double progress = tag.getDouble("progress");
        long totalValue = tag.getLong("total_value");
        long updatedAt = tag.getLong("updated_at");

        return new InstitutionState(
                key,
                level,
                progress,
                totalValue,
                updatedAt
        );
    }
}