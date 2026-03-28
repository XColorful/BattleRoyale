package xiao.battleroyale.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import xiao.battleroyale.inventory.AbstractLootMenu;

public abstract class AbstractLootContainerScreen<L extends AbstractLootMenu> extends AbstractContainerScreen<L> {
    protected Identifier TEXTURE;
    protected int textureOffX = 0;
    protected int textureOffY = 0;
    protected int textureWidth = 256;
    protected int textureHeight = 256;

    public AbstractLootContainerScreen(L menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
        super(menu, inventory, title, imageWidth, imageHeight);
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
        // 26.1 移到构造函数里设置final字段
//        this.imageWidth = width;
//        this.imageHeight = height;
    }

    protected abstract void adjustTexture();

    protected void adjustTexture(Identifier rl) {
        adjustTexture(rl, 256, 256);
    }

    protected void adjustTexture(Identifier rl, int textureWidth, int textureHeight) {
        this.TEXTURE = rl;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
//        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick); // 移到extractRenderState调用的extractContents里
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
//        this.extractTooltip(guiGraphics, mouseX, mouseY); // 26.1 super.extractRenderState已经有了
    }

//    @Override
    protected void renderBg(GuiGraphicsExtractor guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = getGuiLeft() + this.textureOffX;
        int y = getGuiTop() + this.textureOffY;
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                this.TEXTURE,
                x, y,
                0.0F, 0.0F,
                this.imageWidth, this.imageHeight,
                256, 256
        );
    }
    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 先画背景材质
        renderBg(guiGraphics, partialTick, mouseX, mouseY);
        // 再画其他的
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
    }
}
