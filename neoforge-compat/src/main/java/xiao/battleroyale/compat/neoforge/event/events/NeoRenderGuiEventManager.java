package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoRenderGuiEvent;

public class NeoRenderGuiEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoRenderGuiProxyHighest.INSTANCE;
            case HIGH -> NeoRenderGuiProxyHigh.INSTANCE;
            case NORMAL -> NeoRenderGuiProxyNormal.INSTANCE;
            case LOW -> NeoRenderGuiProxyLow.INSTANCE;
            case LOWEST -> NeoRenderGuiProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoRenderGuiProxy extends AbstractNeoEventCommon {
        public NeoRenderGuiProxy() {
            super(EventType.RENDER_GUI_EVENT);
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
            return new NeoRenderGuiEvent(event);
        }

        protected void handle(RenderGuiEvent.Post event) {
            super.onEvent(event);
        }
    }

    public static class NeoRenderGuiProxyHighest extends NeoRenderGuiProxy {
        static final NeoRenderGuiProxyHighest INSTANCE = new NeoRenderGuiProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(RenderGuiEvent.Post e) { handle(e); }
    }

    public static class NeoRenderGuiProxyHigh extends NeoRenderGuiProxy {
        static final NeoRenderGuiProxyHigh INSTANCE = new NeoRenderGuiProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(RenderGuiEvent.Post e) { handle(e); }
    }

    public static class NeoRenderGuiProxyNormal extends NeoRenderGuiProxy {
        static final NeoRenderGuiProxyNormal INSTANCE = new NeoRenderGuiProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(RenderGuiEvent.Post e) { handle(e); }
    }

    public static class NeoRenderGuiProxyLow extends NeoRenderGuiProxy {
        static final NeoRenderGuiProxyLow INSTANCE = new NeoRenderGuiProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(RenderGuiEvent.Post e) { handle(e); }
    }

    public static class NeoRenderGuiProxyLowest extends NeoRenderGuiProxy {
        static final NeoRenderGuiProxyLowest INSTANCE = new NeoRenderGuiProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(RenderGuiEvent.Post e) { handle(e); }
    }
}