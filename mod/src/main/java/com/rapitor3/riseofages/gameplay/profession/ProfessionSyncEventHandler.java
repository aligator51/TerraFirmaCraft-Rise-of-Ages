package com.rapitor3.riseofages.gameplay.profession;

import com.rapitor3.riseofages.bootstrap.CoreServices;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Objects;

/**
 * Sends profession sync packets when player session state is initialized.
 */
public final class ProfessionSyncEventHandler {

    private final CoreServices coreServices;

    /**
     * Creates a new profession sync event handler.
     *
     * @param coreServices shared core services
     */
    public ProfessionSyncEventHandler(CoreServices coreServices) {
        this.coreServices = Objects.requireNonNull(coreServices, "CoreServices must not be null");
    }

    /**
     * Pushes profession state to the client after login.
     *
     * @param event login event
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ProfessionSync.pushTo(player, coreServices);
        }
    }

    /**
     * Pushes profession state again after respawn.
     *
     * @param event respawn event
     */
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ProfessionSync.pushTo(player, coreServices);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerProfessionState.clear(player);
        }
    }
}