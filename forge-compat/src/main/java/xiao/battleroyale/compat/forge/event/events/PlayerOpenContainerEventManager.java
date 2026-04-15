package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgePlayerOpenContainerEvent;

public class PlayerOpenContainerEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> PlayerOpenContainerProxyHighest.INSTANCE;
            case HIGH -> PlayerOpenContainerProxyHigh.INSTANCE;
            case NORMAL -> PlayerOpenContainerProxyNormal.INSTANCE;
            case LOW -> PlayerOpenContainerProxyLow.INSTANCE;
            case LOWEST -> PlayerOpenContainerProxyLowest.INSTANCE;
        };
    }

    private static abstract class PlayerOpenContainerProxy extends AbstractEventCommon {
        public PlayerOpenContainerProxy() {
            super(EventType.PLAYER_OPEN_CONTAINER_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgePlayerOpenContainerEvent(event); }

        protected void handle(PlayerContainerEvent.Open event) { super.onEvent(event); }
    }

    public static class PlayerOpenContainerProxyHighest extends PlayerOpenContainerProxy {
        static final PlayerOpenContainerProxyHighest INSTANCE = new PlayerOpenContainerProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Open e) { handle(e); }
    }

    public static class PlayerOpenContainerProxyHigh extends PlayerOpenContainerProxy {
        static final PlayerOpenContainerProxyHigh INSTANCE = new PlayerOpenContainerProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Open e) { handle(e); }
    }

    public static class PlayerOpenContainerProxyNormal extends PlayerOpenContainerProxy {
        static final PlayerOpenContainerProxyNormal INSTANCE = new PlayerOpenContainerProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Open e) { handle(e); }
    }

    public static class PlayerOpenContainerProxyLow extends PlayerOpenContainerProxy {
        static final PlayerOpenContainerProxyLow INSTANCE = new PlayerOpenContainerProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Open e) { handle(e); }
    }

    public static class PlayerOpenContainerProxyLowest extends PlayerOpenContainerProxy {
        static final PlayerOpenContainerProxyLowest INSTANCE = new PlayerOpenContainerProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Open e) { handle(e); }
    }
}