package com.rapitor3.riseofages.gameplay.profession;

import com.rapitor3.riseofages.core.profession.effect.DefaultProfessionEffectLayer;
import com.rapitor3.riseofages.core.profession.effect.ProfessionEffectLayer;
import com.rapitor3.riseofages.core.profession.effect.ProfessionEffectSnapshot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Client-side handler that applies profession mining speed effects.
 *
 * <p>This handler does not read server persistence directly.
 * It uses the synced client profession cache and the unified profession
 * effect layer to resolve mining speed.
 * </p>
 */
public final class ProfessionMiningSpeedEventHandler {

    private final ProfessionEffectLayer effectLayer;
    private final ExtractionBlockClassifier blockClassifier;

    /**
     * Creates a new mining speed event handler.
     */
    public ProfessionMiningSpeedEventHandler() {
        this.effectLayer = new DefaultProfessionEffectLayer();
        this.blockClassifier = new ExtractionBlockClassifier();
    }

    /**
     * Applies mining speed multiplier for extraction-related blocks.
     *
     * @param event break speed event
     */
    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        BlockState state = event.getState();

        if (player == null || state == null) {
            return;
        }

        if (!blockClassifier.matches(state)) {
            return;
        }

        ProfessionEffectSnapshot effects = effectLayer.resolve(ClientProfessionState::getInvestedPoints);

        float multiplier = effects.miningSpeedMultiplier();
        float newSpeed = event.getOriginalSpeed() * multiplier;

        event.setNewSpeed(newSpeed);
    }
}