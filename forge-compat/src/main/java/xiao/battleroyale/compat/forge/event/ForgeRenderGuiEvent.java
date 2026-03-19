package xiao.battleroyale.compat.forge.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IRenderGuiEvent;

public class ForgeRenderGuiEvent extends ForgeEvent implements IRenderGuiEvent {

    private final RenderGuiEvent.Post event;

    public ForgeRenderGuiEvent(Event event) {
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
        return "ForgeRenderGuiEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
