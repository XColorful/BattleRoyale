package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoPlayerOpenContainerEvent;

public class NeoPlayerOpenContainerEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoPlayerOpenContainerProxyHighest.INSTANCE;
            case HIGH -> NeoPlayerOpenContainerProxyHigh.INSTANCE;
            case NORMAL -> NeoPlayerOpenContainerProxyNormal.INSTANCE;
            case LOW -> NeoPlayerOpenContainerProxyLow.INSTANCE;
            case LOWEST -> NeoPlayerOpenContainerProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoPlayerOpenContainerProxy extends AbstractNeoEventCommon {
        public NeoPlayerOpenContainerProxy() {
            super(EventType.PLAYER_OPEN_CONTAINER_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoPlayerOpenContainerEvent(event); }

        protected void handle(PlayerContainerEvent.Open event) { super.onEvent(event); }
    }

    public static class NeoPlayerOpenContainerProxyHighest extends NeoPlayerOpenContainerProxy {
        static final NeoPlayerOpenContainerProxyHighest INSTANCE = new NeoPlayerOpenContainerProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Open e) { handle(e); }
    }

    public static class NeoPlayerOpenContainerProxyHigh extends NeoPlayerOpenContainerProxy {
        static final NeoPlayerOpenContainerProxyHigh INSTANCE = new NeoPlayerOpenContainerProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Open e) { handle(e); }
    }

    public static class NeoPlayerOpenContainerProxyNormal extends NeoPlayerOpenContainerProxy {
        static final NeoPlayerOpenContainerProxyNormal INSTANCE = new NeoPlayerOpenContainerProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Open e) { handle(e); }
    }

    public static class NeoPlayerOpenContainerProxyLow extends NeoPlayerOpenContainerProxy {
        static final NeoPlayerOpenContainerProxyLow INSTANCE = new NeoPlayerOpenContainerProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Open e) { handle(e); }
    }

    public static class NeoPlayerOpenContainerProxyLowest extends NeoPlayerOpenContainerProxy {
        static final NeoPlayerOpenContainerProxyLowest INSTANCE = new NeoPlayerOpenContainerProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Open e) { handle(e); }
    }
}