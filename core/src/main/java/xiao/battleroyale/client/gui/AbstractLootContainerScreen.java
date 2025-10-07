package xiao.battleroyale.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xiao.battleroyale.inventory.AbstractLootMenu;

public abstract class AbstractLootContainerScreen<L extends AbstractLootMenu> extends AbstractContainerScreen<L> {
    protected ResourceLocation TEXTURE;
    protected int textureOffX = 0;
    protected int textureOffY = 0;

    public AbstractLootContainerScreen(L menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    protected void initScreen() {
        adjustTitleLabelOff();
        adjustInventoryLabelOff();
        adjustTextureOff();
        adjustTextureSize();
        adjustTexture();
    }

    protected abstract void adjustTitleLabelOff(); // 相对于材质的偏移

    protected void adjustTitleLabelOff(int x, int y) {
        this.titleLabelX = x;
        this.titleLabelY = y;
    }

    protected abstract void adjustInventoryLabelOff(); // 相对于材质的偏移

    protected void adjustInventoryLabelOff(int x, int y) {
        this.inventoryLabelX = x;
        this.inventoryLabelY = y;
    }

    protected abstract void adjustTextureOff();

    protected void adjustTextureOff(int x, int y) {
        this.textureOffX = x;
        this.textureOffY = y;
    }

    protected abstract void adjustTextureSize();

    protected void adjustTextureSize(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
    }

    protected abstract void adjustTexture();

    protected void adjustTexture(ResourceLocation rl) {
        this.TEXTURE = rl;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, this.TEXTURE);
        int x = getGuiLeft() + this.textureOffX;
        int y = getGuiTop() + this.textureOffY;
        guiGraphics.blit(
                (resourceLocation) -> RenderType.gui(),
                this.TEXTURE,
                x, y,
                (float)0, (float)0,
                this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight,
                256, 256,
                -1
        );
    }
}
