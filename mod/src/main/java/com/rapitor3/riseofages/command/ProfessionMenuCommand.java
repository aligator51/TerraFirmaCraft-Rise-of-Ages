package com.rapitor3.riseofages.command;

import com.mojang.brigadier.CommandDispatcher;
import com.rapitor3.riseofages.network.ModNetwork;
import com.rapitor3.riseofages.network.packet.OpenProfessionMenuS2CPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

/**
 * Opens the professions menu on the client.
 */
public final class ProfessionMenuCommand {

    private ProfessionMenuCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("roa")
                        .then(Commands.literal("professionmenu")
                                .executes(context -> executeOpen(context.getSource())))
        );
    }

    private static int executeOpen(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be executed by a player."));
            return 0;
        }

        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new OpenProfessionMenuS2CPacket()
        );

        source.sendSuccess(() -> Component.literal("Opened professions menu."), false);
        return 1;
    }
}