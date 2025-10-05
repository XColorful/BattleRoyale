package xiao.battleroyale.compat.neoforge.event;

import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import xiao.battleroyale.api.event.IServerTickEvent;

public class NeoServerTickEvent extends NeoEvent implements IServerTickEvent {

    protected ServerTickEvent.Post serverTickEvent;

    public NeoServerTickEvent(Event event) {
        super(event);
        if (event instanceof ServerTickEvent.Post serverTickEvent) {
            this.serverTickEvent = serverTickEvent;
        } else {
            throw new RuntimeException("Expected ServerTickEvent but received: " + event.getClass().getName());
        }
    }
}