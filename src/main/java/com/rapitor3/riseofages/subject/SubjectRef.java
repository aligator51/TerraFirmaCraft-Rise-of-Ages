package com.rapitor3.riseofages.subject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * Lightweight reference to a progress subject.
 *
 * IMPORTANT:
 * This is NOT the actual object (player, group, etc).
 * This is only an identifier used to reference a subject.
 *
 * Why this exists:
 * - Can be safely stored in NBT / SavedData
 * - Works even if player is offline
 * - Decouples core logic from Minecraft entities
 */
public record SubjectRef(

        /**
         * Unique identifier of the subject.
         *
         * PLAYER  -> player's UUID
         * GROUP   -> deterministic UUID based on provider + externalId
         */
        UUID id,

        /**
         * Type of subject (PLAYER / GROUP / SETTLEMENT)
         */
        SubjectType type,

        /**
         * External system identifier.
         *
         * Example:
         * - "ftb" (FTB Teams)
         * - "roa" (our mod)
         *
         * Null for PLAYER.
         */
        String providerId,

        /**
         * External identifier inside provider.
         *
         * Example:
         * - teamId
         * - guildId
         *
         * Null for PLAYER.
         */
        String externalId

) {

    /**
     * Compact constructor with validation.
     */
    public SubjectRef {
        Objects.requireNonNull(id, "SubjectRef.id must not be null");
        Objects.requireNonNull(type, "SubjectRef.type must not be null");
    }

    // =========================================================
    // ===================== FACTORIES ==========================
    // =========================================================

    /**
     * Creates reference for a player.
     *
     * @param playerId UUID of the player
     */
    public static SubjectRef player(UUID playerId) {
        return new SubjectRef(
                playerId,
                SubjectType.PLAYER,
                null,
                null
        );
    }

    /**
     * Creates reference for a group.
     *
     * IMPORTANT:
     * Uses deterministic UUID (nameUUID) so it is stable
     * across server restarts.
     *
     * @param providerId external system (e.g. "ftb")
     * @param externalId id inside that system
     */
    public static SubjectRef group(String providerId, String externalId) {
        Objects.requireNonNull(providerId, "providerId must not be null");
        Objects.requireNonNull(externalId, "externalId must not be null");

        String key = providerId + ":" + externalId;

        UUID id = UUID.nameUUIDFromBytes(
                key.getBytes(StandardCharsets.UTF_8)
        );

        return new SubjectRef(
                id,
                SubjectType.GROUP,
                providerId,
                externalId
        );
    }

    // =========================================================
    // ===================== HELPERS ============================
    // =========================================================

    /**
     * @return true if this reference points to a player
     */
    public boolean isPlayer() {
        return type == SubjectType.PLAYER;
    }

    /**
     * @return true if this reference points to a group
     */
    public boolean isGroup() {
        return type == SubjectType.GROUP;
    }

    /**
     * @return true if this reference points to a settlement
     */
    public boolean isSettlement() {
        return type == SubjectType.SETTLEMENT;
    }

    /**
     * Debug-friendly string representation.
     */
    @Override
    public String toString() {
        return "SubjectRef{" +
                "id=" + id +
                ", type=" + type +
                ", providerId='" + providerId + '\'' +
                ", externalId='" + externalId + '\'' +
                '}';
    }
}