package com.rapitor3.riseofages.service;

import com.rapitor3.riseofages.core.subject.SubjectRef;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

/**
 * Default subject resolution strategy.
 *
 * Current behavior:
 * - each player progresses individually
 * - subject is always the player itself
 *
 * This is the simplest possible implementation and a good starting point.
 *
 * Later this service can be replaced by more advanced implementations:
 * - group-based subject resolution
 * - settlement-based subject resolution
 * - hybrid strategies
 */
public class DefaultSubjectService implements SubjectService {

    /**
     * Resolves the subject for the given player.
     *
     * Current rule:
     * - player UUID = subject UUID
     *
     * @param player server-side player
     * @return player-based subject reference
     */
    @Override
    public SubjectRef resolve(ServerPlayer player) {
        Objects.requireNonNull(player, "ServerPlayer must not be null");
        return SubjectRef.player(player.getUUID());
    }
}