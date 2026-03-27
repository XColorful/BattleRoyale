package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoFarmlandTrampleEvent;

public class NeoFarmlandTrampleEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoFarmlandTrampleProxyHighest.INSTANCE;
            case HIGH -> NeoFarmlandTrampleProxyHigh.INSTANCE;
            case NORMAL -> NeoFarmlandTrampleProxyNormal.INSTANCE;
            case LOW -> NeoFarmlandTrampleProxyLow.INSTANCE;
            case LOWEST -> NeoFarmlandTrampleProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoFarmlandTrampleProxy extends AbstractNeoEventCommon {
        public NeoFarmlandTrampleProxy() {
            super(EventType.FARMLAND_TRAMPLE_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoFarmlandTrampleEvent(event); }

        protected void handle(BlockEvent.FarmlandTrampleEvent event) { super.onEvent(event); }
    }

    public static class NeoFarmlandTrampleProxyHighest extends NeoFarmlandTrampleProxy {
        static final NeoFarmlandTrampleProxyHighest INSTANCE = new NeoFarmlandTrampleProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(BlockEvent.FarmlandTrampleEvent e) { handle(e); }
    }

    public static class NeoFarmlandTrampleProxyHigh extends NeoFarmlandTrampleProxy {
        static final NeoFarmlandTrampleProxyHigh INSTANCE = new NeoFarmlandTrampleProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(BlockEvent.FarmlandTrampleEvent e) { handle(e); }
    }

    public static class NeoFarmlandTrampleProxyNormal extends NeoFarmlandTrampleProxy {
        static final NeoFarmlandTrampleProxyNormal INSTANCE = new NeoFarmlandTrampleProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(BlockEvent.FarmlandTrampleEvent e) { handle(e); }
    }

    public static class NeoFarmlandTrampleProxyLow extends NeoFarmlandTrampleProxy {
        static final NeoFarmlandTrampleProxyLow INSTANCE = new NeoFarmlandTrampleProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(BlockEvent.FarmlandTrampleEvent e) { handle(e); }
    }

    public static class NeoFarmlandTrampleProxyLowest extends NeoFarmlandTrampleProxy {
        static final NeoFarmlandTrampleProxyLowest INSTANCE = new NeoFarmlandTrampleProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(BlockEvent.FarmlandTrampleEvent e) { handle(e); }
    }
}