package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoPlayerCloseContainerEvent;

public class NeoPlayerCloseContainerEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoPlayerCloseContainerProxyHighest.INSTANCE;
            case HIGH -> NeoPlayerCloseContainerProxyHigh.INSTANCE;
            case NORMAL -> NeoPlayerCloseContainerProxyNormal.INSTANCE;
            case LOW -> NeoPlayerCloseContainerProxyLow.INSTANCE;
            case LOWEST -> NeoPlayerCloseContainerProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoPlayerCloseContainerProxy extends AbstractNeoEventCommon {
        public NeoPlayerCloseContainerProxy() {
            super(EventType.PLAYER_CLOSE_CONTAINER_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoPlayerCloseContainerEvent(event); }

        protected void handle(PlayerContainerEvent.Close event) { super.onEvent(event); }
    }

    public static class NeoPlayerCloseContainerProxyHighest extends NeoPlayerCloseContainerProxy {
        static final NeoPlayerCloseContainerProxyHighest INSTANCE = new NeoPlayerCloseContainerProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Close e) { handle(e); }
    }

    public static class NeoPlayerCloseContainerProxyHigh extends NeoPlayerCloseContainerProxy {
        static final NeoPlayerCloseContainerProxyHigh INSTANCE = new NeoPlayerCloseContainerProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Close e) { handle(e); }
    }

    public static class NeoPlayerCloseContainerProxyNormal extends NeoPlayerCloseContainerProxy {
        static final NeoPlayerCloseContainerProxyNormal INSTANCE = new NeoPlayerCloseContainerProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Close e) { handle(e); }
    }

    public static class NeoPlayerCloseContainerProxyLow extends NeoPlayerCloseContainerProxy {
        static final NeoPlayerCloseContainerProxyLow INSTANCE = new NeoPlayerCloseContainerProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Close e) { handle(e); }
    }

    public static class NeoPlayerCloseContainerProxyLowest extends NeoPlayerCloseContainerProxy {
        static final NeoPlayerCloseContainerProxyLowest INSTANCE = new NeoPlayerCloseContainerProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Close e) { handle(e); }
    }
}