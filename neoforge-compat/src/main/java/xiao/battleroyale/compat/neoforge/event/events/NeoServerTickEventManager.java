package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoServerTickEvent;

public class NeoServerTickEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoServerTickProxyHighest.INSTANCE;
            case HIGH -> NeoServerTickProxyHigh.INSTANCE;
            case NORMAL -> NeoServerTickProxyNormal.INSTANCE;
            case LOW -> NeoServerTickProxyLow.INSTANCE;
            case LOWEST -> NeoServerTickProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoServerTickProxy extends AbstractNeoEventCommon {
        public NeoServerTickProxy() {
            super(EventType.SERVER_TICK_EVENT);
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
            return new NeoServerTickEvent(event);
        }

        protected void handle(ServerTickEvent.Post event) {
            super.onEvent(event);
        }
    }

    public static class NeoServerTickProxyHighest extends NeoServerTickProxy {
        static final NeoServerTickProxyHighest INSTANCE = new NeoServerTickProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(ServerTickEvent.Post e) { handle(e); }
    }

    public static class NeoServerTickProxyHigh extends NeoServerTickProxy {
        static final NeoServerTickProxyHigh INSTANCE = new NeoServerTickProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(ServerTickEvent.Post e) { handle(e); }
    }

    public static class NeoServerTickProxyNormal extends NeoServerTickProxy {
        static final NeoServerTickProxyNormal INSTANCE = new NeoServerTickProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(ServerTickEvent.Post e) { handle(e); }
    }

    public static class NeoServerTickProxyLow extends NeoServerTickProxy {
        static final NeoServerTickProxyLow INSTANCE = new NeoServerTickProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(ServerTickEvent.Post e) { handle(e); }
    }

    public static class NeoServerTickProxyLowest extends NeoServerTickProxy {
        static final NeoServerTickProxyLowest INSTANCE = new NeoServerTickProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(ServerTickEvent.Post e) { handle(e); }
    }
}