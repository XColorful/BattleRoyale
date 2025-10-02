package xiao.battleroyale.compat.neoforge.client.event;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import xiao.battleroyale.api.client.event.IRenderGuiEventPost;

public class NeoRenderGuiEventPost implements IRenderGuiEventPost {

    private final RenderGuiEvent.Post event;

    public NeoRenderGuiEventPost(RenderGuiEvent.Post event) {
        this.event = event;
    }

    @Override
    public GuiGraphics getGuiGraphics() {
        return this.event.getGuiGraphics();
    }
}