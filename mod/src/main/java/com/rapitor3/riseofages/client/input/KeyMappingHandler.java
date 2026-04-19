package com.rapitor3.riseofages.client.input;

import com.rapitor3.riseofages.client.gui.profession.ProfessionMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles client key mapping presses.
 */
public final class KeyMappingHandler {

    /**
     * Opens the professions screen when the mapped key is pressed.
     *
     * @param event keyboard input event
     */
    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.screen != null) {
            return;
        }

        while (ModKeyMappings.OPEN_PROFESSIONS.consumeClick()) {
            minecraft.setScreen(new ProfessionMenuScreen());
        }
    }
}