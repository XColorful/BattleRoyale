package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoPlayerLoggedOutEvent;

public class NeoPlayerLoggedOutEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoPlayerLoggedOutProxyHighest.INSTANCE;
            case HIGH -> NeoPlayerLoggedOutProxyHigh.INSTANCE;
            case NORMAL -> NeoPlayerLoggedOutProxyNormal.INSTANCE;
            case LOW -> NeoPlayerLoggedOutProxyLow.INSTANCE;
            case LOWEST -> NeoPlayerLoggedOutProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoPlayerLoggedOutProxy extends AbstractNeoEventCommon {
        public NeoPlayerLoggedOutProxy() {
            super(EventType.PLAYER_LOGGED_OUT_EVENT);
        }

        @Override
        protected void registerToNeo() {
            NeoForge.EVENT_BUS.register(this);
        }

        @Override
        protected void unregisterToNeo() {
            NeoForge.EVENT_BUS.unregister(this);
        }

        @Override
        protected NeoEvent getNeoEventType(Event event) {
            return new NeoPlayerLoggedOutEvent(event);
        }

        protected void handle(PlayerEvent.PlayerLoggedOutEvent event) {
            super.onEvent(event);
        }
    }

    public static class NeoPlayerLoggedOutProxyHighest extends NeoPlayerLoggedOutProxy {
        static final NeoPlayerLoggedOutProxyHighest INSTANCE = new NeoPlayerLoggedOutProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedOutEvent e) { handle(e); }
    }

    public static class NeoPlayerLoggedOutProxyHigh extends NeoPlayerLoggedOutProxy {
        static final NeoPlayerLoggedOutProxyHigh INSTANCE = new NeoPlayerLoggedOutProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedOutEvent e) { handle(e); }
    }

    public static class NeoPlayerLoggedOutProxyNormal extends NeoPlayerLoggedOutProxy {
        static final NeoPlayerLoggedOutProxyNormal INSTANCE = new NeoPlayerLoggedOutProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedOutEvent e) { handle(e); }
    }

    public static class NeoPlayerLoggedOutProxyLow extends NeoPlayerLoggedOutProxy {
        static final NeoPlayerLoggedOutProxyLow INSTANCE = new NeoPlayerLoggedOutProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedOutEvent e) { handle(e); }
    }

    public static class NeoPlayerLoggedOutProxyLowest extends NeoPlayerLoggedOutProxy {
        static final NeoPlayerLoggedOutProxyLowest INSTANCE = new NeoPlayerLoggedOutProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedOutEvent e) { handle(e); }
    }
}