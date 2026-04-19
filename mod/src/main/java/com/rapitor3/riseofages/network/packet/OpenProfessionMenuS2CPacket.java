package com.rapitor3.riseofages.network.packet;

import com.rapitor3.riseofages.client.gui.profession.ProfessionMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Clientbound packet that opens the professions menu screen.
 */
public class OpenProfessionMenuS2CPacket {

    public OpenProfessionMenuS2CPacket() {
    }

    public OpenProfessionMenuS2CPacket(FriendlyByteBuf buffer) {
    }

    public void encode(FriendlyByteBuf buffer) {
    }

    public static OpenProfessionMenuS2CPacket decode(FriendlyByteBuf buffer) {
        return new OpenProfessionMenuS2CPacket(buffer);
    }

    public static void handle(OpenProfessionMenuS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                minecraft.setScreen(new ProfessionMenuScreen());
            }
        });

        context.setPacketHandled(true);
    }
}