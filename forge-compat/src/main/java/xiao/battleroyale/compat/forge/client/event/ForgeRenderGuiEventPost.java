package xiao.battleroyale.compat.forge.client.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.RenderGuiEvent;
import xiao.battleroyale.api.client.event.IRenderGuiEventPost;

public class ForgeRenderGuiEventPost implements IRenderGuiEventPost {

    private final RenderGuiEvent.Post event;

    public ForgeRenderGuiEventPost(RenderGuiEvent.Post event) {
        this.event = event;
    }

    @Override
    public GuiGraphics getGuiGraphics() {
        return this.event.getGuiGraphics();
    }
}
