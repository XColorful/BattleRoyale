package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoItemTossEvent;

public class NeoItemTossEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoItemTossProxyHighest.INSTANCE;
            case HIGH -> NeoItemTossProxyHigh.INSTANCE;
            case NORMAL -> NeoItemTossProxyNormal.INSTANCE;
            case LOW -> NeoItemTossProxyLow.INSTANCE;
            case LOWEST -> NeoItemTossProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoItemTossProxy extends AbstractNeoEventCommon {
        public NeoItemTossProxy() {
            super(EventType.ITEM_TOSS_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoItemTossEvent(event); }

        protected void handle(ItemTossEvent event) { super.onEvent(event); }
    }

    public static class NeoItemTossProxyHighest extends NeoItemTossProxy {
        static final NeoItemTossProxyHighest INSTANCE = new NeoItemTossProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(ItemTossEvent e) { handle(e); }
    }

    public static class NeoItemTossProxyHigh extends NeoItemTossProxy {
        static final NeoItemTossProxyHigh INSTANCE = new NeoItemTossProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(ItemTossEvent e) { handle(e); }
    }

    public static class NeoItemTossProxyNormal extends NeoItemTossProxy {
        static final NeoItemTossProxyNormal INSTANCE = new NeoItemTossProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(ItemTossEvent e) { handle(e); }
    }

    public static class NeoItemTossProxyLow extends NeoItemTossProxy {
        static final NeoItemTossProxyLow INSTANCE = new NeoItemTossProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(ItemTossEvent e) { handle(e); }
    }

    public static class NeoItemTossProxyLowest extends NeoItemTossProxy {
        static final NeoItemTossProxyLowest INSTANCE = new NeoItemTossProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(ItemTossEvent e) { handle(e); }
    }
}