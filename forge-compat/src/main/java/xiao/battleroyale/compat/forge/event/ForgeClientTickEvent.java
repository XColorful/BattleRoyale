package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IClientTickEvent;

public class ForgeClientTickEvent extends ForgeEvent implements IClientTickEvent {

    protected TickEvent.ClientTickEvent.Post clientTickEvent;

    public ForgeClientTickEvent(Event event) {
        super(event);
        if (event instanceof TickEvent.ClientTickEvent.Post eventIn) {
            this.clientTickEvent = eventIn;
        } else {
            throw new RuntimeException("Expected ClientTickEvent.Post but received: " + event.getClass().getName());
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
        return "ForgeClientTickEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
