package com.rapitor3.riseofages.client.gui.profession;

import com.rapitor3.riseofages.core.profession.ProfessionKey;

import java.util.List;

/**
 * Static content provider for the professions menu.
 *
 * <p>This class contains presentation-only text used by the client GUI.
 * It does not perform gameplay calculations and does not depend on
 * world state or networking.
 * </p>
 */
public final class ProfessionMenuContent {

    /**
     * Utility class.
     */
    private ProfessionMenuContent() {
    }

    /**
     * Returns all profession entries displayed in the menu.
     *
     * @return ordered menu entries
     */
    public static List<Entry> entries() {
        return List.of(
                new Entry(
                        ProfessionKey.of("extraction"),
                        "Extraction",
                        "Mining, gathering and resource efficiency.",
                        List.of(
                                "Lvl 1: mining penalty reduced",
                                "Lvl 2: mining speed improved",
                                "Lvl 3: rare extra drop chance",
                                "Lvl 4: better ore efficiency",
                                "Lvl 5: expert extraction bonuses"
                        )
                ),
                new Entry(
                        ProfessionKey.of("smithing"),
                        "Smithing",
                        "Metalworking, tools and forging quality.",
                        List.of(
                                "Lvl 1: basic smithing unlocked",
                                "Lvl 2: better forging outcome",
                                "Lvl 3: more durability",
                                "Lvl 4: advanced recipes unlocked",
                                "Lvl 5: master smith bonuses"
                        )
                ),
                new Entry(
                        ProfessionKey.of("agriculture"),
                        "Agriculture",
                        "Crop yield, farming efficiency and food stability.",
                        List.of(
                                "Lvl 1: slightly better harvest",
                                "Lvl 2: more reliable crops",
                                "Lvl 3: bonus produce chance",
                                "Lvl 4: reduced farming losses",
                                "Lvl 5: master grower bonuses"
                        )
                )
        );
    }

    /**
     * Finds menu entry by profession key.
     *
     * @param key profession key
     * @return matching entry or null if absent
     */
    public static Entry find(ProfessionKey key) {
        if (key == null) {
            return null;
        }

        for (Entry entry : entries()) {
            if (entry.key().equals(key)) {
                return entry;
            }
        }

        return null;
    }

    /**
     * One menu entry describing a profession.
     *
     * @param key profession key
     * @param title display title
     * @param summary short summary
     * @param levelBonuses bonus lines by level
     */
    public record Entry(
            ProfessionKey key,
            String title,
            String summary,
            List<String> levelBonuses
    ) {
    }
}