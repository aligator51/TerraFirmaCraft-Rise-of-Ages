package com.rapitor3.riseofages.client.gui.profession;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rapitor3.riseofages.core.profession.ProfessionKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Main professions menu screen.
 *
 * <p>This screen presents all profession tracks and shows details for the
 * currently selected profession.
 * </p>
 *
 * <p>Current MVP layout:
 * <ul>
 *     <li>left panel: profession list</li>
 *     <li>right panel: selected profession details</li>
 *     <li>bottom: close button</li>
 * </ul>
 * </p>
 */
public class ProfessionMenuScreen extends Screen {

    private final List<Button> professionButtons = new ArrayList<>();
    private ProfessionKey selectedProfessionKey = ProfessionKey.of("extraction");

    /**
     * Creates the professions menu screen.
     */
    public ProfessionMenuScreen() {
        super(Component.literal("Professions"));
    }

    /**
     * Opens the professions menu screen.
     */
    public static void open() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ProfessionMenuScreen());
    }

    @Override
    protected void init() {
        super.init();

        professionButtons.clear();

        int leftX = this.width / 2 - 180;
        int topY = this.height / 2 - 100;

        List<ProfessionMenuContent.Entry> entries = ProfessionMenuContent.entries();

        for (int i = 0; i < entries.size(); i++) {
            ProfessionMenuContent.Entry entry = entries.get(i);

            int buttonY = topY + 20 + i * 24;

            Button button = Button.builder(
                    Component.literal(entry.title()),
                    b -> selectedProfessionKey = entry.key()
            ).bounds(leftX + 10, buttonY, 120, 20).build();

            professionButtons.add(button);
            addRenderableWidget(button);
        }

        addRenderableWidget(
                Button.builder(
                        Component.literal("Back"),
                        button -> onClose()
                ).bounds(this.width / 2 - 40, this.height / 2 + 110, 80, 20).build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int leftPanelX = centerX - 180;
        int leftPanelY = centerY - 110;
        int leftPanelW = 140;
        int leftPanelH = 220;

        int rightPanelX = centerX - 30;
        int rightPanelY = centerY - 110;
        int rightPanelW = 210;
        int rightPanelH = 220;

        drawPanel(guiGraphics, leftPanelX, leftPanelY, leftPanelW, leftPanelH);
        drawPanel(guiGraphics, rightPanelX, rightPanelY, rightPanelW, rightPanelH);

        guiGraphics.drawString(
                this.font,
                this.title,
                centerX - this.font.width(this.title) / 2,
                centerY - 125,
                0x404040,
                false
        );

        guiGraphics.drawString(
                this.font,
                "Professions",
                leftPanelX + 10,
                leftPanelY + 8,
                0x303030,
                false
        );

        renderProfessionDetails(guiGraphics, rightPanelX, rightPanelY, rightPanelW, rightPanelH);
    }

    /**
     * Draws a simple framed panel.
     *
     * @param guiGraphics graphics
     * @param x left
     * @param y top
     * @param width panel width
     * @param height panel height
     */
    private void drawPanel(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + height, 0xE0D8C8);
        guiGraphics.fill(x, y, x + width, y + 1, 0xFF8B7D6B);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, 0xFF8B7D6B);
        guiGraphics.fill(x, y, x + 1, y + height, 0xFF8B7D6B);
        guiGraphics.fill(x + width - 1, y, x + width, y + height, 0xFF8B7D6B);
    }

    /**
     * Renders detailed information for the selected profession.
     *
     * @param guiGraphics graphics
     * @param x panel x
     * @param y panel y
     * @param width panel width
     * @param height panel height
     */
    private void renderProfessionDetails(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        ProfessionMenuContent.Entry entry = ProfessionMenuContent.find(selectedProfessionKey);
        if (entry == null) {
            return;
        }

        int textX = x + 10;
        int lineY = y + 10;

        guiGraphics.drawString(this.font, entry.title(), textX, lineY, 0x303030, false);
        lineY += 16;

        guiGraphics.drawString(this.font, entry.summary(), textX, lineY, 0x505050, false);
        lineY += 24;

        guiGraphics.drawString(this.font, "Progress Branch", textX, lineY, 0x303030, false);
        lineY += 16;

        renderLevelBranch(guiGraphics, textX, lineY);
        lineY += 32;

        guiGraphics.drawString(this.font, "Level Bonuses", textX, lineY, 0x303030, false);
        lineY += 14;

        for (String line : entry.levelBonuses()) {
            guiGraphics.drawString(this.font, "- " + line, textX, lineY, 0x404040, false);
            lineY += 12;
        }
    }

    /**
     * Renders a simple horizontal level branch placeholder.
     *
     * <p>Later this can become a real node tree with icons, locked states
     * and clickable level nodes.
     * </p>
     *
     * @param guiGraphics graphics
     * @param x start x
     * @param y start y
     */
    private void renderLevelBranch(GuiGraphics guiGraphics, int x, int y) {
        int size = 12;
        int spacing = 24;

        for (int i = 0; i < 5; i++) {
            int boxX = x + i * spacing;

            if (i < 4) {
                guiGraphics.fill(boxX + size, y + 5, boxX + spacing, y + 7, 0xFF8B7D6B);
            }

            guiGraphics.fill(boxX, y, boxX + size, y + size, 0xFFB8AA92);
            guiGraphics.fill(boxX, y, boxX + size, y + 1, 0xFF6B5F52);
            guiGraphics.fill(boxX, y + size - 1, boxX + size, y + size, 0xFF6B5F52);
            guiGraphics.fill(boxX, y, boxX + 1, y + size, 0xFF6B5F52);
            guiGraphics.fill(boxX + size - 1, y, boxX + size, y + size, 0xFF6B5F52);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}