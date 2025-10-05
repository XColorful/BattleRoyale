package xiao.battleroyale.compat.neoforge.event;

import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.event.ClientTickEvent;
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
}