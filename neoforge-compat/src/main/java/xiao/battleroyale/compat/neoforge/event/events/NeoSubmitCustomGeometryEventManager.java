package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.neoforged.neoforge.common.NeoForge;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoSubmitCustomGeometryEvent;

public class NeoSubmitCustomGeometryEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoSubmitCustomGeometryProxyHighest.INSTANCE;
            case HIGH -> NeoSubmitCustomGeometryProxyHigh.INSTANCE;
            case NORMAL -> NeoSubmitCustomGeometryProxyNormal.INSTANCE;
            case LOW -> NeoSubmitCustomGeometryProxyLow.INSTANCE;
            case LOWEST -> NeoSubmitCustomGeometryProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoSubmitCustomGeometryProxy extends AbstractNeoEventCommon {
        public NeoSubmitCustomGeometryProxy() {
            super(EventType.SUBMIT_CUSTOM_GEOMETRY_EVENT);
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
            return new NeoSubmitCustomGeometryEvent((SubmitCustomGeometryEvent) event);
        }

        protected void handle(SubmitCustomGeometryEvent event) {
            super.onEvent(event);
        }
    }

    public static class NeoSubmitCustomGeometryProxyHighest extends NeoSubmitCustomGeometryProxy {
        static final NeoSubmitCustomGeometryProxyHighest INSTANCE = new NeoSubmitCustomGeometryProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(SubmitCustomGeometryEvent e) { handle(e); }
    }

    public static class NeoSubmitCustomGeometryProxyHigh extends NeoSubmitCustomGeometryProxy {
        static final NeoSubmitCustomGeometryProxyHigh INSTANCE = new NeoSubmitCustomGeometryProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(SubmitCustomGeometryEvent e) { handle(e); }
    }

    public static class NeoSubmitCustomGeometryProxyNormal extends NeoSubmitCustomGeometryProxy {
        static final NeoSubmitCustomGeometryProxyNormal INSTANCE = new NeoSubmitCustomGeometryProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(SubmitCustomGeometryEvent e) { handle(e); }
    }

    public static class NeoSubmitCustomGeometryProxyLow extends NeoSubmitCustomGeometryProxy {
        static final NeoSubmitCustomGeometryProxyLow INSTANCE = new NeoSubmitCustomGeometryProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(SubmitCustomGeometryEvent e) { handle(e); }
    }

    public static class NeoSubmitCustomGeometryProxyLowest extends NeoSubmitCustomGeometryProxy {
        static final NeoSubmitCustomGeometryProxyLowest INSTANCE = new NeoSubmitCustomGeometryProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(SubmitCustomGeometryEvent e) { handle(e); }
    }
}
