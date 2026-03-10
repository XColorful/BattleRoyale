package xiao.battleroyale.compat.fabric.event;

import net.minecraft.client.gui.GuiGraphics;
import xiao.battleroyale.api.event.IRenderGuiEvent;

public class FabricRenderGuiEvent extends FabricEvent implements IRenderGuiEvent {
    private final GuiGraphics guiGraphics;

    public FabricRenderGuiEvent(GuiGraphics guiGraphics) {
        super(false);
        this.guiGraphics = guiGraphics;
    }

    @Override
    public GuiGraphics getGuiGraphics() {
        return this.guiGraphics;
    }
}