package com.rapitor3.riseofages;

import com.mojang.logging.LogUtils;
import com.rapitor3.riseofages.bootstrap.CoreBootstrap;
import com.rapitor3.riseofages.bootstrap.CoreServices;
import com.rapitor3.riseofages.command.ModCommands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(RiseOfAgesCoreMod.MODID)
public class RiseOfAgesCoreMod {


    public static final String MODID = "riseofages";
    private static final Logger LOGGER = LogUtils.getLogger();


    /**
     * Shared core service graph.
     */
    private final CoreServices coreServices;

    public RiseOfAgesCoreMod() {
        this.coreServices = CoreBootstrap.bootstrap();

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    /**
     * Registers mod commands.
     *
     * @param event command registration event
     */
    private void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event, coreServices);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
        }
    }

}
