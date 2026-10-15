package xiao.battleroyale.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class NoBackgroundContainerScreen<L extends AbstractContainerMenu> extends AbstractContainerScreen<L> {

    protected NoBackgroundContainerScreen(L menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    // --------移除虚化背景--------

    @ApiStatus.AvailableSince("1.20.2")
    @Override
    public void renderTransparentBackground(
            @NotNull GuiGraphics graphics
    ) {
    }
    @ApiStatus.AvailableSince("1.21.1")
    @Override
    protected void renderBlurredBackground(
//            float partickTick
    ) {
    }
    @ApiStatus.AvailableSince("1.21.1")
    @Override
    protected void renderMenuBackground(
            @NotNull GuiGraphics graphics
    ) {
    }
    @ApiStatus.AvailableSince("26.1.x")
//    @Override
    public void extractTransparentBackground(
            @NotNull GuiGraphics graphics
    ) {
    }
    @ApiStatus.AvailableSince("26.1.x")
//    @Override
    protected void extractBlurredBackground(
            @NotNull GuiGraphics graphics
    ) {
    }
    @ApiStatus.AvailableSince("26.1.x")
//    @Override
    protected void extractMenuBackground(
            @NotNull GuiGraphics graphics
    ) {
    }
}
