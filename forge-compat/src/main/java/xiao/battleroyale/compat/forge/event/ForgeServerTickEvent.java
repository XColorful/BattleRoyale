package xiao.battleroyale.compat.forge.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.IServerTickEvent;

public class ForgeServerTickEvent extends ForgeEvent implements IServerTickEvent {

    protected TickEvent.ServerTickEvent serverTickEvent;

    public ForgeServerTickEvent(Event event) {
        super(event);
        if (event instanceof TickEvent.ServerTickEvent serverTickEvent) {
            this.serverTickEvent = serverTickEvent;
        } else {
            throw new RuntimeException("Expected ServerTickEvent but received: " + event.getClass().getName());
        }
    }
}
