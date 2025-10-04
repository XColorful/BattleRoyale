package xiao.battleroyale.compat.forge.client.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import xiao.battleroyale.api.client.event.IRenderGuiEventPost;

public class ForgeRenderGuiEventPost implements IRenderGuiEventPost {

    private final CustomizeGuiOverlayEvent event;

    public ForgeRenderGuiEventPost(CustomizeGuiOverlayEvent event) {
        this.event = event;
    }

    @Override
    public GuiGraphics getGuiGraphics() {
        return this.event.getGuiGraphics();
    }
}
