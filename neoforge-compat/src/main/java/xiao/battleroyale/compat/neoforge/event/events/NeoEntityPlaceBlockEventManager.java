package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEntityPlaceBlockEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class NeoEntityPlaceBlockEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoEntityPlaceBlockProxyHighest.INSTANCE;
            case HIGH -> NeoEntityPlaceBlockProxyHigh.INSTANCE;
            case NORMAL -> NeoEntityPlaceBlockProxyNormal.INSTANCE;
            case LOW -> NeoEntityPlaceBlockProxyLow.INSTANCE;
            case LOWEST -> NeoEntityPlaceBlockProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoEntityPlaceBlockProxy extends AbstractNeoEventCommon {
        public NeoEntityPlaceBlockProxy() {
            super(EventType.ENTITY_PLACE_BLOCK_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoEntityPlaceBlockEvent(event); }

        protected void handle(BlockEvent.EntityPlaceEvent event) { super.onEvent(event); }
    }

    public static class NeoEntityPlaceBlockProxyHighest extends NeoEntityPlaceBlockProxy {
        static final NeoEntityPlaceBlockProxyHighest INSTANCE = new NeoEntityPlaceBlockProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(BlockEvent.EntityPlaceEvent e) { handle(e); }
    }

    public static class NeoEntityPlaceBlockProxyHigh extends NeoEntityPlaceBlockProxy {
        static final NeoEntityPlaceBlockProxyHigh INSTANCE = new NeoEntityPlaceBlockProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(BlockEvent.EntityPlaceEvent e) { handle(e); }
    }

    public static class NeoEntityPlaceBlockProxyNormal extends NeoEntityPlaceBlockProxy {
        static final NeoEntityPlaceBlockProxyNormal INSTANCE = new NeoEntityPlaceBlockProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(BlockEvent.EntityPlaceEvent e) { handle(e); }
    }

    public static class NeoEntityPlaceBlockProxyLow extends NeoEntityPlaceBlockProxy {
        static final NeoEntityPlaceBlockProxyLow INSTANCE = new NeoEntityPlaceBlockProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(BlockEvent.EntityPlaceEvent e) { handle(e); }
    }

    public static class NeoEntityPlaceBlockProxyLowest extends NeoEntityPlaceBlockProxy {
        static final NeoEntityPlaceBlockProxyLowest INSTANCE = new NeoEntityPlaceBlockProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(BlockEvent.EntityPlaceEvent e) { handle(e); }
    }
}