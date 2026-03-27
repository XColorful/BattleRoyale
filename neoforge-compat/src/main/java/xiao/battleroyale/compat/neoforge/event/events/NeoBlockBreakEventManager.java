package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoBlockBreakEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class NeoBlockBreakEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoBlockBreakProxyHighest.INSTANCE;
            case HIGH -> NeoBlockBreakProxyHigh.INSTANCE;
            case NORMAL -> NeoBlockBreakProxyNormal.INSTANCE;
            case LOW -> NeoBlockBreakProxyLow.INSTANCE;
            case LOWEST -> NeoBlockBreakProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoBlockBreakProxy extends AbstractNeoEventCommon {
        public NeoBlockBreakProxy() {
            super(EventType.BLOCK_BREAK_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoBlockBreakEvent(event); }

        protected void handle(BlockEvent.BreakEvent event) { super.onEvent(event); }
    }

    public static class NeoBlockBreakProxyHighest extends NeoBlockBreakProxy {
        static final NeoBlockBreakProxyHighest INSTANCE = new NeoBlockBreakProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(BlockEvent.BreakEvent e) { handle(e); }
    }

    public static class NeoBlockBreakProxyHigh extends NeoBlockBreakProxy {
        static final NeoBlockBreakProxyHigh INSTANCE = new NeoBlockBreakProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(BlockEvent.BreakEvent e) { handle(e); }
    }

    public static class NeoBlockBreakProxyNormal extends NeoBlockBreakProxy {
        static final NeoBlockBreakProxyNormal INSTANCE = new NeoBlockBreakProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(BlockEvent.BreakEvent e) { handle(e); }
    }

    public static class NeoBlockBreakProxyLow extends NeoBlockBreakProxy {
        static final NeoBlockBreakProxyLow INSTANCE = new NeoBlockBreakProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(BlockEvent.BreakEvent e) { handle(e); }
    }

    public static class NeoBlockBreakProxyLowest extends NeoBlockBreakProxy {
        static final NeoBlockBreakProxyLowest INSTANCE = new NeoBlockBreakProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(BlockEvent.BreakEvent e) { handle(e); }
    }
}