package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.neoforge.event.NeoClientTickEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class NeoClientTickNormal extends AbstractNeoEventCommon {

    private static class NeoClientTickNormalHolder {
        private static final NeoClientTickNormal INSTANCE = new NeoClientTickNormal();
    }

    public static NeoClientTickNormal get() {
        return NeoClientTickNormalHolder.INSTANCE;
    }

    private NeoClientTickNormal() {
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

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onClientTickEvent(TickEvent.ClientTickEvent event) {
        super.onEvent(event);
    }
}