package xiao.battleroyale.compat.neoforge.event;

import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.TickEvent;
import xiao.battleroyale.api.event.IClientTickEvent;

public class NeoClientTickEvent extends NeoEvent implements IClientTickEvent {

    protected TickEvent.ClientTickEvent clientTickEvent;

    public NeoClientTickEvent(Event event) {
        super(event);
        if (event instanceof TickEvent.ClientTickEvent clientTickEvent) {
            this.clientTickEvent = clientTickEvent;
        } else {
            throw new RuntimeException("Expected ClientTickEvent but received: " + event.getClass().getName());
        }
    }
}