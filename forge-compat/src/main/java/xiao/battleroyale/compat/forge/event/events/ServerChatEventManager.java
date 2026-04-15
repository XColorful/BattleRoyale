package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeServerChatEvent;

public class ServerChatEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> ServerChatProxyHighest.INSTANCE;
            case HIGH -> ServerChatProxyHigh.INSTANCE;
            case NORMAL -> ServerChatProxyNormal.INSTANCE;
            case LOW -> ServerChatProxyLow.INSTANCE;
            case LOWEST -> ServerChatProxyLowest.INSTANCE;
        };
    }

    private static abstract class ServerChatProxy extends AbstractEventCommon {
        public ServerChatProxy() {
            super(EventType.SERVER_CHAT_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeServerChatEvent(event); }

        protected void handle(ServerChatEvent event) { super.onEvent(event); }
    }

    public static class ServerChatProxyHighest extends ServerChatProxy {
        static final ServerChatProxyHighest INSTANCE = new ServerChatProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(ServerChatEvent e) { handle(e); }
    }

    public static class ServerChatProxyHigh extends ServerChatProxy {
        static final ServerChatProxyHigh INSTANCE = new ServerChatProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(ServerChatEvent e) { handle(e); }
    }

    public static class ServerChatProxyNormal extends ServerChatProxy {
        static final ServerChatProxyNormal INSTANCE = new ServerChatProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(ServerChatEvent e) { handle(e); }
    }

    public static class ServerChatProxyLow extends ServerChatProxy {
        static final ServerChatProxyLow INSTANCE = new ServerChatProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(ServerChatEvent e) { handle(e); }
    }

    public static class ServerChatProxyLowest extends ServerChatProxy {
        static final ServerChatProxyLowest INSTANCE = new ServerChatProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(ServerChatEvent e) { handle(e); }
    }
}