package com.rapitor3.riseofages.gameplay.profession;

import com.rapitor3.riseofages.bootstrap.CoreServices;
import com.rapitor3.riseofages.core.profession.ProfessionKey;
import com.rapitor3.riseofages.core.profession.ProfessionState;
import com.rapitor3.riseofages.core.subject.SubjectRef;
import com.rapitor3.riseofages.network.ModNetwork;
import com.rapitor3.riseofages.network.ProfessionStateSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Synchronizes profession point state from server persistence
 * into both runtime caches:
 * - server runtime cache
 * - client runtime cache
 */
public final class ProfessionSync {

    private ProfessionSync() {
    }

    /**
     * Pushes the current profession invested points to the specified player.
     *
     * @param player target player
     * @param coreServices initialized core services
     */
    public static void pushTo(ServerPlayer player, CoreServices coreServices) {
        Objects.requireNonNull(player, "ServerPlayer must not be null");
        Objects.requireNonNull(coreServices, "CoreServices must not be null");

        SubjectRef subjectRef = coreServices.getSubjectService().resolve(player);

        Optional<ProfessionState> optionalState = coreServices.getProfessionService()
                .findState(player.serverLevel(), subjectRef);

        Map<ProfessionKey, Integer> investedPoints = new LinkedHashMap<>();
        optionalState.ifPresent(state -> investedPoints.putAll(state.getInvestedPointsByProfession()));

        ServerProfessionState.replace(player, investedPoints);

        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new ProfessionStateSyncPacket(investedPoints)
        );
    }
}