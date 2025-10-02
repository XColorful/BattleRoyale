package xiao.battleroyale.compat.neoforge.event;

import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.TickEvent;
import xiao.battleroyale.api.event.IServerTickEvent;

public class NeoServerTickEvent extends NeoEvent implements IServerTickEvent {

    protected TickEvent.ServerTickEvent serverTickEvent;

    public NeoServerTickEvent(Event event) {
        super(event);
        if (event instanceof TickEvent.ServerTickEvent serverTickEvent) {
            this.serverTickEvent = serverTickEvent;
        } else {
            throw new RuntimeException("Expected ServerTickEvent but received: " + event.getClass().getName());
        }
    }
}