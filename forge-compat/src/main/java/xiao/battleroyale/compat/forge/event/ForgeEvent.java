package xiao.battleroyale.compat.forge.event;

import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.IEvent;

public class ForgeEvent implements IEvent {

    protected Event event;

    public ForgeEvent(Event event) {
        this.event = event;
    }

    public boolean isCanceled() {
        return this.event.isCanceled();
    }

    public void setCanceled(boolean cancel) {
        this.event.setCanceled(cancel);
    }

    public Object getEvent() {
        return this.event;
    }
}
