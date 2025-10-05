package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoServerTickEvent;

public class NeoServerTickNormal extends AbstractNeoEventCommon {

    private static class NeoServerTickNormalHolder {
        private static final NeoServerTickNormal INSTANCE = new NeoServerTickNormal();
    }

    public static NeoServerTickNormal get() {
        return NeoServerTickNormalHolder.INSTANCE;
    }

    private NeoServerTickNormal() {
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

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onServerTickEvent(ServerTickEvent.Post event) {
        super.onEvent(event);
    }
}