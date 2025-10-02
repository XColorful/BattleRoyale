package xiao.battleroyale.compat.neoforge.event;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import xiao.battleroyale.api.event.IEvent;

public class NeoEvent implements IEvent {

    protected Event event;

    public NeoEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean isCanceled() {
        if (this.event instanceof ICancellableEvent cancellableEvent) {
            return cancellableEvent.isCanceled();
        }
        return false;
    }

    @Override
    public void setCanceled(boolean cancel) {
        if (this.event instanceof ICancellableEvent cancellableEvent) {
            cancellableEvent.setCanceled(cancel);
        }
    }

    @Override
    public Object getEvent() {
        return this.event;
    }

    public boolean isCancelable() {
        return this.event instanceof ICancellableEvent;
    }
}