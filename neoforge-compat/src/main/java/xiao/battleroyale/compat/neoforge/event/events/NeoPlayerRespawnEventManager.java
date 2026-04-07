package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoPlayerRespawnEvent;

public class NeoPlayerRespawnEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> PlayerRespawnProxyHighest.INSTANCE;
            case HIGH -> PlayerRespawnProxyHigh.INSTANCE;
            case NORMAL -> PlayerRespawnProxyNormal.INSTANCE;
            case LOW -> PlayerRespawnProxyLow.INSTANCE;
            case LOWEST -> PlayerRespawnProxyLowest.INSTANCE;
        };
    }

    private static abstract class PlayerRespawnProxy extends AbstractNeoEventCommon {
        public PlayerRespawnProxy() {
            super(EventType.PLAYER_RESPAWN_EVENT);
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
            return new NeoPlayerRespawnEvent(event);
        }

        protected void handle(PlayerEvent.PlayerRespawnEvent event) {
            super.onEvent(event);
        }
    }

    public static class PlayerRespawnProxyHighest extends PlayerRespawnProxy {
        static final PlayerRespawnProxyHighest INSTANCE = new PlayerRespawnProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerRespawnEvent e) { handle(e); }
    }

    public static class PlayerRespawnProxyHigh extends PlayerRespawnProxy {
        static final PlayerRespawnProxyHigh INSTANCE = new PlayerRespawnProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerRespawnEvent e) { handle(e); }
    }

    public static class PlayerRespawnProxyNormal extends PlayerRespawnProxy {
        static final PlayerRespawnProxyNormal INSTANCE = new PlayerRespawnProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerRespawnEvent e) { handle(e); }
    }

    public static class PlayerRespawnProxyLow extends PlayerRespawnProxy {
        static final PlayerRespawnProxyLow INSTANCE = new PlayerRespawnProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerRespawnEvent e) { handle(e); }
    }

    public static class PlayerRespawnProxyLowest extends PlayerRespawnProxy {
        static final PlayerRespawnProxyLowest INSTANCE = new PlayerRespawnProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerRespawnEvent e) { handle(e); }
    }
}