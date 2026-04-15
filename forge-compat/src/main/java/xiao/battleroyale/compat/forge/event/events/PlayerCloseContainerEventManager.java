package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgePlayerCloseContainerEvent;

public class PlayerCloseContainerEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> PlayerCloseContainerProxyHighest.INSTANCE;
            case HIGH -> PlayerCloseContainerProxyHigh.INSTANCE;
            case NORMAL -> PlayerCloseContainerProxyNormal.INSTANCE;
            case LOW -> PlayerCloseContainerProxyLow.INSTANCE;
            case LOWEST -> PlayerCloseContainerProxyLowest.INSTANCE;
        };
    }

    private static abstract class PlayerCloseContainerProxy extends AbstractEventCommon {
        public PlayerCloseContainerProxy() {
            super(EventType.PLAYER_CLOSE_CONTAINER_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgePlayerCloseContainerEvent(event); }

        protected void handle(PlayerContainerEvent.Close event) { super.onEvent(event); }
    }

    public static class PlayerCloseContainerProxyHighest extends PlayerCloseContainerProxy {
        static final PlayerCloseContainerProxyHighest INSTANCE = new PlayerCloseContainerProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Close e) { handle(e); }
    }

    public static class PlayerCloseContainerProxyHigh extends PlayerCloseContainerProxy {
        static final PlayerCloseContainerProxyHigh INSTANCE = new PlayerCloseContainerProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Close e) { handle(e); }
    }

    public static class PlayerCloseContainerProxyNormal extends PlayerCloseContainerProxy {
        static final PlayerCloseContainerProxyNormal INSTANCE = new PlayerCloseContainerProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Close e) { handle(e); }
    }

    public static class PlayerCloseContainerProxyLow extends PlayerCloseContainerProxy {
        static final PlayerCloseContainerProxyLow INSTANCE = new PlayerCloseContainerProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Close e) { handle(e); }
    }

    public static class PlayerCloseContainerProxyLowest extends PlayerCloseContainerProxy {
        static final PlayerCloseContainerProxyLowest INSTANCE = new PlayerCloseContainerProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerContainerEvent.Close e) { handle(e); }
    }
}