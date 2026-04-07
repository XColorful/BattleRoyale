package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgePlayerRespawnEvent;

public class PlayerRespawnEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> PlayerRespawnProxyHighest.INSTANCE;
            case HIGH -> PlayerRespawnProxyHigh.INSTANCE;
            case NORMAL -> PlayerRespawnProxyNormal.INSTANCE;
            case LOW -> PlayerRespawnProxyLow.INSTANCE;
            case LOWEST -> PlayerRespawnProxyLowest.INSTANCE;
        };
    }

    private static abstract class PlayerRespawnProxy extends AbstractEventCommon {
        public PlayerRespawnProxy() {
            super(EventType.PLAYER_RESPAWN_EVENT);
        }

        @Override
        protected void registerToForge() {
            MinecraftForge.EVENT_BUS.register(this);
        }

        @Override
        protected void unregisterToForge() {
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        @Override
        protected ForgeEvent getForgeEventType(Event event) {
            return new ForgePlayerRespawnEvent(event);
        }

        protected void handle(PlayerEvent.PlayerRespawnEvent event) {
            super.onEvent(event);
        }
    }

    public static class PlayerRespawnProxyHighest extends PlayerRespawnProxy {
        static final PlayerRespawnProxyHighest INSTANCE = new PlayerRespawnProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerRespawnEvent e) { handle(e); }
    }

    public static class PlayerRespawnProxyHigh extends PlayerRespawnProxy {
        static final PlayerRespawnProxyHigh INSTANCE = new PlayerRespawnProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerRespawnEvent e) { handle(e); }
    }

    public static class PlayerRespawnProxyNormal extends PlayerRespawnProxy {
        static final PlayerRespawnProxyNormal INSTANCE = new PlayerRespawnProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerRespawnEvent e) { handle(e); }
    }

    public static class PlayerRespawnProxyLow extends PlayerRespawnProxy {
        static final PlayerRespawnProxyLow INSTANCE = new PlayerRespawnProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerRespawnEvent e) { handle(e); }
    }

    public static class PlayerRespawnProxyLowest extends PlayerRespawnProxy {
        static final PlayerRespawnProxyLowest INSTANCE = new PlayerRespawnProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerRespawnEvent e) { handle(e); }
    }
}