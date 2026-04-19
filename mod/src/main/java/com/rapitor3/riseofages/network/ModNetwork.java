package com.rapitor3.riseofages.network;

import com.rapitor3.riseofages.RiseOfAgesMod;
import com.rapitor3.riseofages.network.packet.OpenProfessionMenuS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Central network channel registration for the mod.
 */
public final class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";
    private static int nextPacketId = 0;

    /**
     * Shared mod network channel.
     */
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(RiseOfAgesMod.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private ModNetwork() {
    }

    /**
     * Registers all mod packets.
     */
    public static void register() {
        CHANNEL.registerMessage(nextPacketId++, ProfessionStateSyncPacket.class, ProfessionStateSyncPacket::encode, ProfessionStateSyncPacket::decode, ProfessionStateSyncPacket::handle);
        CHANNEL.registerMessage(nextPacketId++, OpenProfessionMenuS2CPacket.class, OpenProfessionMenuS2CPacket::encode, OpenProfessionMenuS2CPacket::decode, OpenProfessionMenuS2CPacket::handle);
    }
}