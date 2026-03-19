package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IClientTickEvent;

public class NeoClientTickEvent extends NeoEvent implements IClientTickEvent {

    protected ClientTickEvent.Post clientTickEvent;

    public NeoClientTickEvent(Event event) {
        super(event);
        if (event instanceof ClientTickEvent.Post clientTickEvent) {
            this.clientTickEvent = clientTickEvent;
        } else {
            throw new RuntimeException("Expected ClientTickEvent but received: " + event.getClass().getName());
        }
    }
    @Override public EventType getType() {
        return EventType.CLIENT_TICK_EVENT;
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return null;
    }

    @Override public String getTextName() {
        return "NeoClientTickEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}