package com.rapitor3.riseofages.service;

import com.rapitor3.riseofages.subject.SubjectRef;
import net.minecraft.server.level.ServerPlayer;

/**
 * Service responsible for resolving progression ownership.
 *
 * In other words, it answers the question:
 * "Which subject should receive progression for this player?"
 *
 * Examples:
 * - player-only mode -> subject is the player
 * - group mode -> subject is the group
 * - settlement mode -> subject is the settlement
 *
 * IMPORTANT:
 * This is one of the main extension points of the core.
 * Different implementations can change progression ownership
 * without changing the rest of the progression pipeline.
 */
public interface SubjectService {

    /**
     * Resolves progression subject for the given player.
     *
     * @param player server-side player
     * @return subject reference that should receive progression
     */
    SubjectRef resolve(ServerPlayer player);
}