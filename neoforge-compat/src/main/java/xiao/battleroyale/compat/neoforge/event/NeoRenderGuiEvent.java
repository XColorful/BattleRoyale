package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.bus.api.Event;
import xiao.battleroyale.api.event.IRenderGuiEvent;

public class NeoRenderGuiEvent extends NeoEvent implements IRenderGuiEvent {

    private final RenderGuiEvent.Post event;

    public NeoRenderGuiEvent(Event event) {
        super(event);
        if (event instanceof RenderGuiEvent.Post eventIn) {
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