package com.rapitor3.riseofages.gameplay.progress;

import com.rapitor3.riseofages.core.institution.InstitutionKey;
import net.minecraft.server.level.ServerPlayer;

/**
 * Decides how a player's gameplay action should be split between:
 * - personal progression
 * - village progression
 *
 * Future lock / focus system should be implemented here.
 */
public interface PlayerInstitutionFocusService {

    ProgressDistribution resolve(ServerPlayer player, InstitutionKey institutionKey);
}