package com.rapitor3.riseofages.gameplay.progress;

import com.rapitor3.riseofages.core.institution.InstitutionKey;
import net.minecraft.server.level.ServerPlayer;

/**
 * Default focus implementation.
 *
 * Current behavior:
 * - all institutions grant both personal and village progression
 *
 * Later this can be replaced with:
 * - focused institutions
 * - neutral institutions
 * - locked institutions
 */
public class DefaultPlayerInstitutionFocusService implements PlayerInstitutionFocusService {

    @Override
    public ProgressDistribution resolve(ServerPlayer player, InstitutionKey institutionKey) {
        return ProgressDistribution.unrestricted();
    }
}