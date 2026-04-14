package com.rapitor3.riseofages.command;

import com.rapitor3.riseofages.bootstrap.CoreServices;
import net.minecraftforge.event.RegisterCommandsEvent;

/**
 * Central command registration entry point.
 */
public final class ModCommands {

    /**
     * Utility class.
     */
    private ModCommands() {
    }

    /**
     * Registers all mod commands.
     *
     * @param event Forge command registration event
     * @param coreServices initialized core services
     */
    public static void register(RegisterCommandsEvent event, CoreServices coreServices) {
        DebugProgressCommand.register(event.getDispatcher(), coreServices);
    }
}