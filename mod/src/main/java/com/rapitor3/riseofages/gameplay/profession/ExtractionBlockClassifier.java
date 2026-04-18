package com.rapitor3.riseofages.gameplay.profession;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

/**
 * Determines whether a block should receive extraction profession bonuses.
 *
 * <p>This first MVP version intentionally avoids TFC-specific classes and uses
 * generic Minecraft / Forge tags only.
 * </p>
 *
 * <p>Current rules:
 * <ul>
 *     <li>block must be mineable with pickaxe</li>
 *     <li>and be either ore-like or stone-like</li>
 * </ul>
 * </p>
 */
public final class ExtractionBlockClassifier {

    /**
     * Checks whether the given block state is affected by extraction bonuses.
     *
     * @param state block state being mined
     * @return true if extraction bonus should apply
     */
    public boolean matches(BlockState state) {
        if (state == null) {
            return false;
        }

        if (!state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return false;
        }

        return state.is(Tags.Blocks.ORES)
                || state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.BASE_STONE_NETHER);
    }
}