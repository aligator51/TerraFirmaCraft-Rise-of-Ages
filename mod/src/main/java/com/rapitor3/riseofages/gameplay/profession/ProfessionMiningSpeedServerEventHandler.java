package com.rapitor3.riseofages.gameplay.profession;

import com.rapitor3.riseofages.core.profession.ProfessionKey;
import com.rapitor3.riseofages.core.profession.effect.ExtractionMiningSpeedPolicy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Applies extraction mining speed bonus on the authoritative server side.
 *
 * <p>This handler must stay lightweight.
 * It must not resolve progression state from repository on every BreakSpeed event.
 * Instead it uses the already prepared server runtime cache.
 * </p>
 */
public final class ProfessionMiningSpeedServerEventHandler {

    private static final ProfessionKey EXTRACTION = ProfessionKey.of("extraction");

    private final ExtractionMiningSpeedPolicy speedPolicy;
    private final ExtractionBlockClassifier blockClassifier;

    public ProfessionMiningSpeedServerEventHandler() {
        this.speedPolicy = new ExtractionMiningSpeedPolicy();
        this.blockClassifier = new ExtractionBlockClassifier();
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        BlockState state = event.getState();

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (state == null || !blockClassifier.matches(state)) {
            return;
        }

        int allocatedPoints = ServerProfessionState.getInvestedPoints(serverPlayer, EXTRACTION);
        float multiplier = speedPolicy.resolveMultiplier(allocatedPoints);

        event.setNewSpeed(event.getOriginalSpeed() * multiplier);
    }
}