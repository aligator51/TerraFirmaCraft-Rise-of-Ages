package com.rapitor3.riseofages.gameplay.progress;

import com.rapitor3.riseofages.core.subject.SubjectRef;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * Resolves village / clan / settlement subject for a player.
 *
 * This is intentionally separated from gameplay event handling so that:
 * - clan systems
 * - village systems
 * - FTB teams integration
 * can be plugged in later without rewriting the handler.
 */
public interface VillageSubjectResolver {

    Optional<SubjectRef> resolveVillage(ServerPlayer player);
}