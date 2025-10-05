package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.neoforge.event.NeoClientTickEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class NeoClientTickLowest extends AbstractNeoEventCommon {

    private static class NeoClientTickLowestHolder {
        private static final NeoClientTickLowest INSTANCE = new NeoClientTickLowest();
    }

    public static NeoClientTickLowest get() {
        return NeoClientTickLowestHolder.INSTANCE;
    }

    private NeoClientTickLowest() {
        super(EventType.CLIENT_TICK_EVENT);
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
        return new NeoClientTickEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onClientTickEvent(ClientTickEvent.Post event) {
        super.onEvent(event);
    }
}