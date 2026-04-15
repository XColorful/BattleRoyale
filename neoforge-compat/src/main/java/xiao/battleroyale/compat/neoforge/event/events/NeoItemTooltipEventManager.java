package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoItemTooltipEvent;

public class NeoItemTooltipEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoItemTooltipProxyHighest.INSTANCE;
            case HIGH -> NeoItemTooltipProxyHigh.INSTANCE;
            case NORMAL -> NeoItemTooltipProxyNormal.INSTANCE;
            case LOW -> NeoItemTooltipProxyLow.INSTANCE;
            case LOWEST -> NeoItemTooltipProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoItemTooltipProxy extends AbstractNeoEventCommon {
        public NeoItemTooltipProxy() {
            super(EventType.ITEM_TOOLTIP_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoItemTooltipEvent(event); }

        protected void handle(ItemTooltipEvent event) { super.onEvent(event); }
    }

    public static class NeoItemTooltipProxyHighest extends NeoItemTooltipProxy {
        static final NeoItemTooltipProxyHighest INSTANCE = new NeoItemTooltipProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(ItemTooltipEvent e) { handle(e); }
    }

    public static class NeoItemTooltipProxyHigh extends NeoItemTooltipProxy {
        static final NeoItemTooltipProxyHigh INSTANCE = new NeoItemTooltipProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(ItemTooltipEvent e) { handle(e); }
    }

    public static class NeoItemTooltipProxyNormal extends NeoItemTooltipProxy {
        static final NeoItemTooltipProxyNormal INSTANCE = new NeoItemTooltipProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(ItemTooltipEvent e) { handle(e); }
    }

    public static class NeoItemTooltipProxyLow extends NeoItemTooltipProxy {
        static final NeoItemTooltipProxyLow INSTANCE = new NeoItemTooltipProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(ItemTooltipEvent e) { handle(e); }
    }

    public static class NeoItemTooltipProxyLowest extends NeoItemTooltipProxy {
        static final NeoItemTooltipProxyLowest INSTANCE = new NeoItemTooltipProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(ItemTooltipEvent e) { handle(e); }
    }
}