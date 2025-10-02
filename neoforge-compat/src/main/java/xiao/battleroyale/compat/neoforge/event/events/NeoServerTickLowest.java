package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoServerTickEvent;

public class NeoServerTickLowest extends AbstractNeoEventCommon {

    private static class NeoServerTickLowestHolder {
        private static final NeoServerTickLowest INSTANCE = new NeoServerTickLowest();
    }

    public static NeoServerTickLowest get() {
        return NeoServerTickLowestHolder.INSTANCE;
    }

    private NeoServerTickLowest() {
        super(EventType.SERVER_TICK_EVENT);
    }

    @Override
    protected void registerToNeo() {
        NeoForge.EVENT_BUS.register(get());
    }
    @Override
    protected void unregisterToNeo() {
        NeoForge.EVENT_BUS.unregister(get());
    }

    @Override
    protected NeoEvent getNeoEventType(Event event) {
        return new NeoServerTickEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        super.onEvent(event);
    }
}