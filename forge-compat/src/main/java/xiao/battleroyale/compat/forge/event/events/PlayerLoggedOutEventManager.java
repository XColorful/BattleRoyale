package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgePlayerLoggedOutEvent;

public class PlayerLoggedOutEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> PlayerLoggedOutProxyHighest.INSTANCE;
            case HIGH -> PlayerLoggedOutProxyHigh.INSTANCE;
            case NORMAL -> PlayerLoggedOutProxyNormal.INSTANCE;
            case LOW -> PlayerLoggedOutProxyLow.INSTANCE;
            case LOWEST -> PlayerLoggedOutProxyLowest.INSTANCE;
        };
    }

    private static abstract class PlayerLoggedOutProxy extends AbstractEventCommon {
        public PlayerLoggedOutProxy() {
            super(EventType.PLAYER_LOGGED_OUT_EVENT);
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
            return new ForgePlayerLoggedOutEvent(event);
        }

        protected void handle(PlayerEvent.PlayerLoggedOutEvent event) {
            super.onEvent(event);
        }
    }

    public static class PlayerLoggedOutProxyHighest extends PlayerLoggedOutProxy {
        static final PlayerLoggedOutProxyHighest INSTANCE = new PlayerLoggedOutProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedOutEvent e) { handle(e); }
    }

    public static class PlayerLoggedOutProxyHigh extends PlayerLoggedOutProxy {
        static final PlayerLoggedOutProxyHigh INSTANCE = new PlayerLoggedOutProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedOutEvent e) { handle(e); }
    }

    public static class PlayerLoggedOutProxyNormal extends PlayerLoggedOutProxy {
        static final PlayerLoggedOutProxyNormal INSTANCE = new PlayerLoggedOutProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedOutEvent e) { handle(e); }
    }

    public static class PlayerLoggedOutProxyLow extends PlayerLoggedOutProxy {
        static final PlayerLoggedOutProxyLow INSTANCE = new PlayerLoggedOutProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedOutEvent e) { handle(e); }
    }

    public static class PlayerLoggedOutProxyLowest extends PlayerLoggedOutProxy {
        static final PlayerLoggedOutProxyLowest INSTANCE = new PlayerLoggedOutProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedOutEvent e) { handle(e); }
    }
}