package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ServerChatEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoServerChatEvent;

public class NeoServerChatEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoServerChatProxyHighest.INSTANCE;
            case HIGH -> NeoServerChatProxyHigh.INSTANCE;
            case NORMAL -> NeoServerChatProxyNormal.INSTANCE;
            case LOW -> NeoServerChatProxyLow.INSTANCE;
            case LOWEST -> NeoServerChatProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoServerChatProxy extends AbstractNeoEventCommon {
        public NeoServerChatProxy() {
            super(EventType.SERVER_CHAT_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoServerChatEvent(event); }

        protected void handle(ServerChatEvent event) { super.onEvent(event); }
    }

    public static class NeoServerChatProxyHighest extends NeoServerChatProxy {
        static final NeoServerChatProxyHighest INSTANCE = new NeoServerChatProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(ServerChatEvent e) { handle(e); }
    }

    public static class NeoServerChatProxyHigh extends NeoServerChatProxy {
        static final NeoServerChatProxyHigh INSTANCE = new NeoServerChatProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(ServerChatEvent e) { handle(e); }
    }

    public static class NeoServerChatProxyNormal extends NeoServerChatProxy {
        static final NeoServerChatProxyNormal INSTANCE = new NeoServerChatProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(ServerChatEvent e) { handle(e); }
    }

    public static class NeoServerChatProxyLow extends NeoServerChatProxy {
        static final NeoServerChatProxyLow INSTANCE = new NeoServerChatProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(ServerChatEvent e) { handle(e); }
    }

    public static class NeoServerChatProxyLowest extends NeoServerChatProxy {
        static final NeoServerChatProxyLowest INSTANCE = new NeoServerChatProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(ServerChatEvent e) { handle(e); }
    }
}