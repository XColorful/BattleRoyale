package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoItemEntityPickupEvent;

public class NeoItemEntityPickupEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoItemEntityPickupProxyHighest.INSTANCE;
            case HIGH -> NeoItemEntityPickupProxyHigh.INSTANCE;
            case NORMAL -> NeoItemEntityPickupProxyNormal.INSTANCE;
            case LOW -> NeoItemEntityPickupProxyLow.INSTANCE;
            case LOWEST -> NeoItemEntityPickupProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoItemEntityPickupProxy extends AbstractNeoEventCommon {
        public NeoItemEntityPickupProxy() {
            super(EventType.ITEM_ENTITY_PICKUP_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoItemEntityPickupEvent(event); }

        protected void handle(ItemEntityPickupEvent.Pre event) { super.onEvent(event); }
    }

    public static class NeoItemEntityPickupProxyHighest extends NeoItemEntityPickupProxy {
        static final NeoItemEntityPickupProxyHighest INSTANCE = new NeoItemEntityPickupProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(ItemEntityPickupEvent.Pre e) { handle(e); }
    }

    public static class NeoItemEntityPickupProxyHigh extends NeoItemEntityPickupProxy {
        static final NeoItemEntityPickupProxyHigh INSTANCE = new NeoItemEntityPickupProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(ItemEntityPickupEvent.Pre e) { handle(e); }
    }

    public static class NeoItemEntityPickupProxyNormal extends NeoItemEntityPickupProxy {
        static final NeoItemEntityPickupProxyNormal INSTANCE = new NeoItemEntityPickupProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(ItemEntityPickupEvent.Pre e) { handle(e); }
    }

    public static class NeoItemEntityPickupProxyLow extends NeoItemEntityPickupProxy {
        static final NeoItemEntityPickupProxyLow INSTANCE = new NeoItemEntityPickupProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(ItemEntityPickupEvent.Pre e) { handle(e); }
    }

    public static class NeoItemEntityPickupProxyLowest extends NeoItemEntityPickupProxy {
        static final NeoItemEntityPickupProxyLowest INSTANCE = new NeoItemEntityPickupProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(ItemEntityPickupEvent.Pre e) { handle(e); }
    }
}