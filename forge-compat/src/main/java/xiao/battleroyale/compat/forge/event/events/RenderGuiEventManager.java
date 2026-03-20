package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeRenderGuiEvent;

public class RenderGuiEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> RenderGuiProxyHighest.INSTANCE;
            case HIGH -> RenderGuiProxyHigh.INSTANCE;
            case NORMAL -> RenderGuiProxyNormal.INSTANCE;
            case LOW -> RenderGuiProxyLow.INSTANCE;
            case LOWEST -> RenderGuiProxyLowest.INSTANCE;
        };
    }

    private static abstract class RenderGuiProxy extends AbstractEventCommon {
        public RenderGuiProxy() {
            super(EventType.RENDER_GUI_EVENT);
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
            return new ForgeRenderGuiEvent(event);
        }

        protected void handle(CustomizeGuiOverlayEvent event) {
            super.onEvent(event);
        }
    }

    public static class RenderGuiProxyHighest extends RenderGuiProxy {
        static final RenderGuiProxyHighest INSTANCE = new RenderGuiProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(CustomizeGuiOverlayEvent e) { handle(e); }
    }

    public static class RenderGuiProxyHigh extends RenderGuiProxy {
        static final RenderGuiProxyHigh INSTANCE = new RenderGuiProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(CustomizeGuiOverlayEvent e) { handle(e); }
    }

    public static class RenderGuiProxyNormal extends RenderGuiProxy {
        static final RenderGuiProxyNormal INSTANCE = new RenderGuiProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(CustomizeGuiOverlayEvent e) { handle(e); }
    }

    public static class RenderGuiProxyLow extends RenderGuiProxy {
        static final RenderGuiProxyLow INSTANCE = new RenderGuiProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(CustomizeGuiOverlayEvent e) { handle(e); }
    }

    public static class RenderGuiProxyLowest extends RenderGuiProxy {
        static final RenderGuiProxyLowest INSTANCE = new RenderGuiProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(CustomizeGuiOverlayEvent e) { handle(e); }
    }
}