package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoRenderLevelStageEvent;

public class NeoRenderLevelStageEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoRenderLevelStageProxyHighest.INSTANCE;
            case HIGH -> NeoRenderLevelStageProxyHigh.INSTANCE;
            case NORMAL -> NeoRenderLevelStageProxyNormal.INSTANCE;
            case LOW -> NeoRenderLevelStageProxyLow.INSTANCE;
            case LOWEST -> NeoRenderLevelStageProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoRenderLevelStageProxy extends AbstractNeoEventCommon {
        public NeoRenderLevelStageProxy() {
            super(EventType.RENDER_LEVEL_STAGE_EVENT);
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
            return new NeoRenderLevelStageEvent((RenderLevelStageEvent) event);
        }

        protected void handle(RenderLevelStageEvent event) {
            super.onEvent(event);
        }
    }

    public static class NeoRenderLevelStageProxyHighest extends NeoRenderLevelStageProxy {
        static final NeoRenderLevelStageProxyHighest INSTANCE = new NeoRenderLevelStageProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class NeoRenderLevelStageProxyHigh extends NeoRenderLevelStageProxy {
        static final NeoRenderLevelStageProxyHigh INSTANCE = new NeoRenderLevelStageProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class NeoRenderLevelStageProxyNormal extends NeoRenderLevelStageProxy {
        static final NeoRenderLevelStageProxyNormal INSTANCE = new NeoRenderLevelStageProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class NeoRenderLevelStageProxyLow extends NeoRenderLevelStageProxy {
        static final NeoRenderLevelStageProxyLow INSTANCE = new NeoRenderLevelStageProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class NeoRenderLevelStageProxyLowest extends NeoRenderLevelStageProxy {
        static final NeoRenderLevelStageProxyLowest INSTANCE = new NeoRenderLevelStageProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }
}