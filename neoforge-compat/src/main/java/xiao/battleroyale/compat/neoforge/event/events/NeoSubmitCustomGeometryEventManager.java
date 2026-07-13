package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoSubmitCustomGeometryEvent;

@ApiStatus.AvailableSince("neoforge26.2")
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

        @Override protected void registerToNeo() {
            NeoForge.EVENT_BUS.register(this);
        }
        @Override protected void unregisterToNeo() {
            NeoForge.EVENT_BUS.unregister(this);
        }
        @Override protected NeoEvent getNeoEventType(Event event) {
            return new NeoSubmitCustomGeometryEvent(event);
        }

        protected void handle(Event event) { super.onEvent(event); }
    }

    public static class NeoSubmitCustomGeometryProxyHighest extends NeoSubmitCustomGeometryProxy {
        static final NeoSubmitCustomGeometryProxyHighest INSTANCE = new NeoSubmitCustomGeometryProxyHighest();
        public void onEvent(Event e) { handle(e); }
    }

    public static class NeoSubmitCustomGeometryProxyHigh extends NeoSubmitCustomGeometryProxy {
        static final NeoSubmitCustomGeometryProxyHigh INSTANCE = new NeoSubmitCustomGeometryProxyHigh();
        public void onEvent(Event e) { handle(e); }
    }

    public static class NeoSubmitCustomGeometryProxyNormal extends NeoSubmitCustomGeometryProxy {
        static final NeoSubmitCustomGeometryProxyNormal INSTANCE = new NeoSubmitCustomGeometryProxyNormal();
        public void onEvent(Event e) { handle(e); }
    }

    public static class NeoSubmitCustomGeometryProxyLow extends NeoSubmitCustomGeometryProxy {
        static final NeoSubmitCustomGeometryProxyLow INSTANCE = new NeoSubmitCustomGeometryProxyLow();
        public void onEvent(Event e) { handle(e); }
    }

    public static class NeoSubmitCustomGeometryProxyLowest extends NeoSubmitCustomGeometryProxy {
        static final NeoSubmitCustomGeometryProxyLowest INSTANCE = new NeoSubmitCustomGeometryProxyLowest();
        public void onEvent(Event e) { handle(e); }
    }
}