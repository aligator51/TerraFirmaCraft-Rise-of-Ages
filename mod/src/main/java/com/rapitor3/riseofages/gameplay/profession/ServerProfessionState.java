package com.rapitor3.riseofages.gameplay.profession;

import com.rapitor3.riseofages.core.profession.ProfessionKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight server-side runtime cache for profession points.
 *
 * <p>This cache is used in hot gameplay paths such as block breaking speed.
 * The source of truth still remains in the persisted profession progression data.
 * </p>
 */
public final class ServerProfessionState {

    private static final Map<UUID, Map<ProfessionKey, Integer>> INVESTED_POINTS_BY_PLAYER =
            new ConcurrentHashMap<>();

    private ServerProfessionState() {
    }

    /**
     * Returns invested points for the specified player and profession key.
     *
     * @param player player
     * @param professionKey profession key
     * @return invested points or 0 if absent
     */
    public static int getInvestedPoints(ServerPlayer player, ProfessionKey professionKey) {
        Objects.requireNonNull(player, "ServerPlayer must not be null");
        Objects.requireNonNull(professionKey, "ProfessionKey must not be null");

        Map<ProfessionKey, Integer> byProfession = INVESTED_POINTS_BY_PLAYER.get(player.getUUID());
        if (byProfession == null) {
            return 0;
        }

        return byProfession.getOrDefault(professionKey, 0);
    }

    /**
     * Replaces the full invested-point snapshot for the specified player.
     *
     * @param player player
     * @param investedPoints invested point map
     */
    public static void replace(ServerPlayer player, Map<ProfessionKey, Integer> investedPoints) {
        Objects.requireNonNull(player, "ServerPlayer must not be null");
        Objects.requireNonNull(investedPoints, "investedPoints must not be null");

        Map<ProfessionKey, Integer> sanitized = new LinkedHashMap<>();

        for (Map.Entry<ProfessionKey, Integer> entry : investedPoints.entrySet()) {
            ProfessionKey key = Objects.requireNonNull(entry.getKey(), "ProfessionKey must not be null");
            int value = Math.max(entry.getValue() == null ? 0 : entry.getValue(), 0);
            sanitized.put(key, value);
        }

        INVESTED_POINTS_BY_PLAYER.put(player.getUUID(), sanitized);
    }

    /**
     * Clears cached profession state for the specified player.
     *
     * @param player player
     */
    public static void clear(ServerPlayer player) {
        Objects.requireNonNull(player, "ServerPlayer must not be null");
        INVESTED_POINTS_BY_PLAYER.remove(player.getUUID());
    }
}