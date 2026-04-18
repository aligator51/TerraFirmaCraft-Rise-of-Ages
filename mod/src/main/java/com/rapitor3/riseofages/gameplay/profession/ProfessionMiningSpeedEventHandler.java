package com.rapitor3.riseofages.gameplay.profession;

import com.rapitor3.riseofages.bootstrap.CoreServices;
import com.rapitor3.riseofages.core.profession.ProfessionKey;
import com.rapitor3.riseofages.core.profession.effect.ExtractionMiningSpeedPolicy;
import com.rapitor3.riseofages.core.subject.SubjectRef;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Applies mining speed bonuses from the extraction profession.
 *
 * <p>This is the first MVP integration for profession gameplay effects.
 * </p>
 */
public final class ProfessionMiningSpeedEventHandler {

    private static final ProfessionKey EXTRACTION = ProfessionKey.of("extraction");

    private final CoreServices coreServices;
    private final ExtractionMiningSpeedPolicy speedPolicy;
    private final ExtractionBlockClassifier blockClassifier;

    /**
     * Creates a new mining speed event handler.
     *
     * @param coreServices shared core services
     */
    public ProfessionMiningSpeedEventHandler(CoreServices coreServices) {
        this.coreServices = coreServices;
        this.speedPolicy = new ExtractionMiningSpeedPolicy();
        this.blockClassifier = new ExtractionBlockClassifier();
    }

    /**
     * Applies extraction mining speed multiplier when the player mines a
     * supported block.
     *
     * @param event break speed event
     */
    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        BlockState state = event.getState();

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ServerLevel level = serverPlayer.serverLevel();

        if (state == null || !blockClassifier.matches(state)) {
            return;
        }

        SubjectRef subjectRef = SubjectRef.player(serverPlayer.getUUID());

        int allocatedPoints = coreServices.getProfessionService()
                .getAllocatedPoints(level, subjectRef, EXTRACTION);

        float multiplier = speedPolicy.resolveMultiplier(allocatedPoints);
        float newSpeed = event.getOriginalSpeed() * multiplier;

        event.setNewSpeed(newSpeed);
    }
}