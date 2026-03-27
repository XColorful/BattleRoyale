package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoPortalSpawnEvent;

public class NeoPortalSpawnEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoPortalSpawnProxyHighest.INSTANCE;
            case HIGH -> NeoPortalSpawnProxyHigh.INSTANCE;
            case NORMAL -> NeoPortalSpawnProxyNormal.INSTANCE;
            case LOW -> NeoPortalSpawnProxyLow.INSTANCE;
            case LOWEST -> NeoPortalSpawnProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoPortalSpawnProxy extends AbstractNeoEventCommon {
        public NeoPortalSpawnProxy() {
            super(EventType.PORTAL_SPAWN_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoPortalSpawnEvent(event); }

        protected void handle(BlockEvent.PortalSpawnEvent event) { super.onEvent(event); }
    }

    public static class NeoPortalSpawnProxyHighest extends NeoPortalSpawnProxy {
        static final NeoPortalSpawnProxyHighest INSTANCE = new NeoPortalSpawnProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(BlockEvent.PortalSpawnEvent e) { handle(e); }
    }

    public static class NeoPortalSpawnProxyHigh extends NeoPortalSpawnProxy {
        static final NeoPortalSpawnProxyHigh INSTANCE = new NeoPortalSpawnProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(BlockEvent.PortalSpawnEvent e) { handle(e); }
    }

    public static class NeoPortalSpawnProxyNormal extends NeoPortalSpawnProxy {
        static final NeoPortalSpawnProxyNormal INSTANCE = new NeoPortalSpawnProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(BlockEvent.PortalSpawnEvent e) { handle(e); }
    }

    public static class NeoPortalSpawnProxyLow extends NeoPortalSpawnProxy {
        static final NeoPortalSpawnProxyLow INSTANCE = new NeoPortalSpawnProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(BlockEvent.PortalSpawnEvent e) { handle(e); }
    }

    public static class NeoPortalSpawnProxyLowest extends NeoPortalSpawnProxy {
        static final NeoPortalSpawnProxyLowest INSTANCE = new NeoPortalSpawnProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(BlockEvent.PortalSpawnEvent e) { handle(e); }
    }
}