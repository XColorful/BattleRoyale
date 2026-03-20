package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.RenderLevelStage;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeRenderLevelStage;
import xiao.battleroyale.compat.forge.event.ForgeRenderLevelStageEvent;

public class RenderTranslucentEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> RenderTranslucentProxyHighest.INSTANCE;
            case HIGH -> RenderTranslucentProxyHigh.INSTANCE;
            case NORMAL -> RenderTranslucentProxyNormal.INSTANCE;
            case LOW -> RenderTranslucentProxyLow.INSTANCE;
            case LOWEST -> RenderTranslucentProxyLowest.INSTANCE;
        };
    }

    private static abstract class RenderTranslucentProxy extends AbstractEventCommon {
        public RenderTranslucentProxy() {
            super(EventType.RENDER_TRANSLUCENT_EVENT);
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
            if (ForgeRenderLevelStage.fromStage(event.getStage()) == RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS) {
                super.onEvent(event);
            }
        }
    }

    public static class RenderTranslucentProxyHighest extends RenderTranslucentProxy {
        static final RenderTranslucentProxyHighest INSTANCE = new RenderTranslucentProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class RenderTranslucentProxyHigh extends RenderTranslucentProxy {
        static final RenderTranslucentProxyHigh INSTANCE = new RenderTranslucentProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class RenderTranslucentProxyNormal extends RenderTranslucentProxy {
        static final RenderTranslucentProxyNormal INSTANCE = new RenderTranslucentProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class RenderTranslucentProxyLow extends RenderTranslucentProxy {
        static final RenderTranslucentProxyLow INSTANCE = new RenderTranslucentProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class RenderTranslucentProxyLowest extends RenderTranslucentProxy {
        static final RenderTranslucentProxyLowest INSTANCE = new RenderTranslucentProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }
}