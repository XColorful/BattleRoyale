package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgePlayerLoggedInEvent;

public class PlayerLoggedInEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> PlayerLoggedInProxyHighest.INSTANCE;
            case HIGH -> PlayerLoggedInProxyHigh.INSTANCE;
            case NORMAL -> PlayerLoggedInProxyNormal.INSTANCE;
            case LOW -> PlayerLoggedInProxyLow.INSTANCE;
            case LOWEST -> PlayerLoggedInProxyLowest.INSTANCE;
        };
    }

    private static abstract class PlayerLoggedInProxy extends AbstractEventCommon {
        public PlayerLoggedInProxy() {
            super(EventType.PLAYER_LOGGED_IN_EVENT);
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
            return new ForgePlayerLoggedInEvent(event);
        }

        protected void handle(PlayerEvent.PlayerLoggedInEvent event) {
            super.onEvent(event);
        }
    }

    public static class PlayerLoggedInProxyHighest extends PlayerLoggedInProxy {
        static final PlayerLoggedInProxyHighest INSTANCE = new PlayerLoggedInProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class PlayerLoggedInProxyHigh extends PlayerLoggedInProxy {
        static final PlayerLoggedInProxyHigh INSTANCE = new PlayerLoggedInProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class PlayerLoggedInProxyNormal extends PlayerLoggedInProxy {
        static final PlayerLoggedInProxyNormal INSTANCE = new PlayerLoggedInProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class PlayerLoggedInProxyLow extends PlayerLoggedInProxy {
        static final PlayerLoggedInProxyLow INSTANCE = new PlayerLoggedInProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class PlayerLoggedInProxyLowest extends PlayerLoggedInProxy {
        static final PlayerLoggedInProxyLowest INSTANCE = new PlayerLoggedInProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }
}