package xiao.battleroyale.compat.forge.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.IRenderGuiEvent;

public class ForgeRenderGuiEvent extends ForgeEvent implements IRenderGuiEvent {

    private final CustomizeGuiOverlayEvent event;

    public ForgeRenderGuiEvent(Event event) {
        super(event);
        if (event instanceof CustomizeGuiOverlayEvent eventIn) {
            this.event = eventIn;
        } else {
            throw new RuntimeException("Expected RenderGuiEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public GuiGraphics getGuiGraphics() {
        return this.event.getGuiGraphics();
    }
}
