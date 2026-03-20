package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeRenderLevelStageEvent;

public class RenderLevelStageEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> RenderLevelStageProxyHighest.INSTANCE;
            case HIGH -> RenderLevelStageProxyHigh.INSTANCE;
            case NORMAL -> RenderLevelStageProxyNormal.INSTANCE;
            case LOW -> RenderLevelStageProxyLow.INSTANCE;
            case LOWEST -> RenderLevelStageProxyLowest.INSTANCE;
        };
    }

    private static abstract class RenderLevelStageProxy extends AbstractEventCommon {
        public RenderLevelStageProxy() {
            super(EventType.RENDER_LEVEL_STAGE_EVENT);
        }

        @Override
        protected void registerToForge() {
            MinecraftForge.EVENT_BUS.register(this);
        }

        @Override
        protected void unregisterToForge() {
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        @Override
        protected ForgeEvent getForgeEventType(Event event) {
            return new ForgeRenderLevelStageEvent((RenderLevelStageEvent) event);
        }

        protected void handle(RenderLevelStageEvent event) {
            super.onEvent(event);
        }
    }

    public static class RenderLevelStageProxyHighest extends RenderLevelStageProxy {
        static final RenderLevelStageProxyHighest INSTANCE = new RenderLevelStageProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class RenderLevelStageProxyHigh extends RenderLevelStageProxy {
        static final RenderLevelStageProxyHigh INSTANCE = new RenderLevelStageProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class RenderLevelStageProxyNormal extends RenderLevelStageProxy {
        static final RenderLevelStageProxyNormal INSTANCE = new RenderLevelStageProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class RenderLevelStageProxyLow extends RenderLevelStageProxy {
        static final RenderLevelStageProxyLow INSTANCE = new RenderLevelStageProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class RenderLevelStageProxyLowest extends RenderLevelStageProxy {
        static final RenderLevelStageProxyLowest INSTANCE = new RenderLevelStageProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }
}