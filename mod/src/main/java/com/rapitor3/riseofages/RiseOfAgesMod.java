package com.rapitor3.riseofages;

import com.mojang.logging.LogUtils;
import com.rapitor3.riseofages.bootstrap.CoreBootstrap;
import com.rapitor3.riseofages.bootstrap.CoreServices;
import com.rapitor3.riseofages.command.ModCommands;
import com.rapitor3.riseofages.gameplay.GameplayProgressEventHandler;
import com.rapitor3.riseofages.gameplay.profession.ProfessionMiningSpeedEventHandler;
import com.rapitor3.riseofages.gameplay.profession.ProfessionSyncEventHandler;
import com.rapitor3.riseofages.network.ModNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod(RiseOfAgesMod.MODID)
public class RiseOfAgesMod {

    public static final String MODID = "riseofages";
    private static final Logger LOGGER = LogUtils.getLogger();

    private final CoreServices coreServices;

    public RiseOfAgesMod() {
        this.coreServices = CoreBootstrap.bootstrap();

        ModNetwork.register();

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        MinecraftForge.EVENT_BUS.register(new GameplayProgressEventHandler(coreServices));
        MinecraftForge.EVENT_BUS.register(new ProfessionSyncEventHandler(coreServices));
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event, coreServices);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MinecraftForge.EVENT_BUS.register(new ProfessionMiningSpeedEventHandler());
        }
    }
}