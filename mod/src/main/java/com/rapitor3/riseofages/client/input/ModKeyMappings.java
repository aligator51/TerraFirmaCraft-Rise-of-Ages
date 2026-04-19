package com.rapitor3.riseofages.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.rapitor3.riseofages.RiseOfAgesMod;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * Central place for client key mappings.
 */
public final class ModKeyMappings {

    /**
     * Category displayed in Minecraft controls settings.
     */
    public static final String CATEGORY = "key.categories." + RiseOfAgesMod.MODID;

    /**
     * Opens the professions menu.
     */
    public static final KeyMapping OPEN_PROFESSIONS = new KeyMapping(
            "key." + RiseOfAgesMod.MODID + ".open_professions",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            CATEGORY
    );

    private ModKeyMappings() {
    }
}