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
            case HIGHEST -> Highest.INSTANCE;
            case HIGH -> High.INSTANCE;
            case NORMAL -> Normal.INSTANCE;
            case LOW -> Low.INSTANCE;
            case LOWEST -> Lowest.INSTANCE;
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

    public static class Highest extends RenderTranslucentProxy {
        static final Highest INSTANCE = new Highest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class High extends RenderTranslucentProxy {
        static final High INSTANCE = new High();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class Normal extends RenderTranslucentProxy {
        static final Normal INSTANCE = new Normal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class Low extends RenderTranslucentProxy {
        static final Low INSTANCE = new Low();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }

    public static class Lowest extends RenderTranslucentProxy {
        static final Lowest INSTANCE = new Lowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(RenderLevelStageEvent e) { handle(e); }
    }
}