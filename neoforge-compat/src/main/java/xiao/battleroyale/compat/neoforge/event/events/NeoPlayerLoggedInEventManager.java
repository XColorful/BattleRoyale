package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoPlayerLoggedInEvent;

public class NeoPlayerLoggedInEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoPlayerLoggedInProxyHighest.INSTANCE;
            case HIGH -> NeoPlayerLoggedInProxyHigh.INSTANCE;
            case NORMAL -> NeoPlayerLoggedInProxyNormal.INSTANCE;
            case LOW -> NeoPlayerLoggedInProxyLow.INSTANCE;
            case LOWEST -> NeoPlayerLoggedInProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoPlayerLoggedInProxy extends AbstractNeoEventCommon {
        public NeoPlayerLoggedInProxy() {
            super(EventType.PLAYER_LOGGED_IN_EVENT);
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
            return new NeoPlayerLoggedInEvent(event);
        }

        protected void handle(PlayerEvent.PlayerLoggedInEvent event) {
            super.onEvent(event);
        }
    }

    public static class NeoPlayerLoggedInProxyHighest extends NeoPlayerLoggedInProxy {
        static final NeoPlayerLoggedInProxyHighest INSTANCE = new NeoPlayerLoggedInProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class NeoPlayerLoggedInProxyHigh extends NeoPlayerLoggedInProxy {
        static final NeoPlayerLoggedInProxyHigh INSTANCE = new NeoPlayerLoggedInProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class NeoPlayerLoggedInProxyNormal extends NeoPlayerLoggedInProxy {
        static final NeoPlayerLoggedInProxyNormal INSTANCE = new NeoPlayerLoggedInProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class NeoPlayerLoggedInProxyLow extends NeoPlayerLoggedInProxy {
        static final NeoPlayerLoggedInProxyLow INSTANCE = new NeoPlayerLoggedInProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class NeoPlayerLoggedInProxyLowest extends NeoPlayerLoggedInProxy {
        static final NeoPlayerLoggedInProxyLowest INSTANCE = new NeoPlayerLoggedInProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }
}