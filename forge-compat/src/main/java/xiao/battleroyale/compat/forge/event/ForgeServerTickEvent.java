package xiao.battleroyale.compat.forge.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IServerTickEvent;

public class ForgeServerTickEvent extends ForgeEvent implements IServerTickEvent {

    protected TickEvent.ServerTickEvent serverTickEvent;

    public ForgeServerTickEvent(Event event) {
        super(event);
        if (event instanceof TickEvent.ServerTickEvent eventIn) {
            this.serverTickEvent = eventIn;
        } else {
            throw new RuntimeException("Expected ServerTickEvent but received: " + event.getClass().getName());
        }
    }
    @Override public EventType getType() {
        return EventType.SERVER_TICK_EVENT;
    }
}
