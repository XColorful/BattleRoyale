package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
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
    @Override public EventType getType() {
        return EventType.RENDER_GUI_EVENT;
    }

    @Override
    public GuiGraphics getGuiGraphics() {
        return this.event.getGuiGraphics();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return null;
    }

    @Override public String getTextName() {
        return "NeoRenderGuiEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}