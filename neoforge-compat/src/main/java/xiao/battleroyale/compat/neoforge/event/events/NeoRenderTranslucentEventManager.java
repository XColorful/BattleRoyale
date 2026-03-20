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

public class NeoRenderTranslucentEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoRenderTranslucentProxyHighest.INSTANCE;
            case HIGH -> NeoRenderTranslucentProxyHigh.INSTANCE;
            case NORMAL -> NeoRenderTranslucentProxyNormal.INSTANCE;
            case LOW -> NeoRenderTranslucentProxyLow.INSTANCE;
            case LOWEST -> NeoRenderTranslucentProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoRenderTranslucentProxy extends AbstractNeoEventCommon {
        public NeoRenderTranslucentProxy() {
            super(EventType.RENDER_TRANSLUCENT_EVENT);
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
            return new NeoRenderLevelStageEvent((RenderLevelStageEvent.AfterTranslucentBlocks) event);
        }

        protected void handle(RenderLevelStageEvent.AfterTranslucentBlocks event) {
            super.onEvent(event);
        }
    }

    public static class NeoRenderTranslucentProxyHighest extends NeoRenderTranslucentProxy {
        static final NeoRenderTranslucentProxyHighest INSTANCE = new NeoRenderTranslucentProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent.AfterTranslucentBlocks e) { handle(e); }
    }

    public static class NeoRenderTranslucentProxyHigh extends NeoRenderTranslucentProxy {
        static final NeoRenderTranslucentProxyHigh INSTANCE = new NeoRenderTranslucentProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent.AfterTranslucentBlocks e) { handle(e); }
    }

    public static class NeoRenderTranslucentProxyNormal extends NeoRenderTranslucentProxy {
        static final NeoRenderTranslucentProxyNormal INSTANCE = new NeoRenderTranslucentProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent.AfterTranslucentBlocks e) { handle(e); }
    }

    public static class NeoRenderTranslucentProxyLow extends NeoRenderTranslucentProxy {
        static final NeoRenderTranslucentProxyLow INSTANCE = new NeoRenderTranslucentProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent.AfterTranslucentBlocks e) { handle(e); }
    }

    public static class NeoRenderTranslucentProxyLowest extends NeoRenderTranslucentProxy {
        static final NeoRenderTranslucentProxyLowest INSTANCE = new NeoRenderTranslucentProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent.AfterTranslucentBlocks e) { handle(e); }
    }
}