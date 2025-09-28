package xiao.battleroyale.compat.forge.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.IClientTickEvent;

public class ForgeClientTickEvent extends ForgeEvent implements IClientTickEvent {

    protected TickEvent.ClientTickEvent clientTickEvent;

    public ForgeClientTickEvent(Event event) {
        super(event);
        if (event instanceof TickEvent.ClientTickEvent clientTickEvent) {
            this.clientTickEvent = clientTickEvent;
        } else {
            throw new RuntimeException("Expected ClientTickEvent but received: " + event.getClass().getName());
        }
    }
}
