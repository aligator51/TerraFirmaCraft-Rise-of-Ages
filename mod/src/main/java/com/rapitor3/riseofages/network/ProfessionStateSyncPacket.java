package com.rapitor3.riseofages.network;

import com.rapitor3.riseofages.core.profession.ProfessionKey;
import com.rapitor3.riseofages.gameplay.profession.ClientProfessionState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Clientbound packet that syncs invested profession points.
 */
public final class ProfessionStateSyncPacket {

    private final Map<ProfessionKey, Integer> investedPointsByProfession;

    public ProfessionStateSyncPacket(Map<ProfessionKey, Integer> investedPointsByProfession) {
        this.investedPointsByProfession = new LinkedHashMap<>();
        if (investedPointsByProfession != null) {
            this.investedPointsByProfession.putAll(investedPointsByProfession);
        }
    }

    public static void encode(ProfessionStateSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.investedPointsByProfession.size());

        for (Map.Entry<ProfessionKey, Integer> entry : packet.investedPointsByProfession.entrySet()) {
            buffer.writeUtf(entry.getKey().id());
            buffer.writeVarInt(entry.getValue());
        }
    }

    public static ProfessionStateSyncPacket decode(FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        Map<ProfessionKey, Integer> investedPoints = new LinkedHashMap<>();

        for (int i = 0; i < size; i++) {
            ProfessionKey key = ProfessionKey.of(buffer.readUtf());
            int value = buffer.readVarInt();
            investedPoints.put(key, value);
        }

        return new ProfessionStateSyncPacket(investedPoints);
    }

    public static void handle(
            ProfessionStateSyncPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> ClientProfessionState.replace(packet.investedPointsByProfession));
        context.setPacketHandled(true);
    }
}