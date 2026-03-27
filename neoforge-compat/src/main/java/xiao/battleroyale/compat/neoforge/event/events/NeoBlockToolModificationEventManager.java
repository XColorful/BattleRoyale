package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoBlockToolModificationEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class NeoBlockToolModificationEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoBlockToolModificationProxyHighest.INSTANCE;
            case HIGH -> NeoBlockToolModificationProxyHigh.INSTANCE;
            case NORMAL -> NeoBlockToolModificationProxyNormal.INSTANCE;
            case LOW -> NeoBlockToolModificationProxyLow.INSTANCE;
            case LOWEST -> NeoBlockToolModificationProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoBlockToolModificationProxy extends AbstractNeoEventCommon {
        public NeoBlockToolModificationProxy() {
            super(EventType.BLOCK_TOOL_MODIFICATION_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoBlockToolModificationEvent(event); }

        protected void handle(BlockEvent.BlockToolModificationEvent event) { super.onEvent(event); }
    }

    public static class NeoBlockToolModificationProxyHighest extends NeoBlockToolModificationProxy {
        static final NeoBlockToolModificationProxyHighest INSTANCE = new NeoBlockToolModificationProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(BlockEvent.BlockToolModificationEvent e) { handle(e); }
    }

    public static class NeoBlockToolModificationProxyHigh extends NeoBlockToolModificationProxy {
        static final NeoBlockToolModificationProxyHigh INSTANCE = new NeoBlockToolModificationProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(BlockEvent.BlockToolModificationEvent e) { handle(e); }
    }

    public static class NeoBlockToolModificationProxyNormal extends NeoBlockToolModificationProxy {
        static final NeoBlockToolModificationProxyNormal INSTANCE = new NeoBlockToolModificationProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(BlockEvent.BlockToolModificationEvent e) { handle(e); }
    }

    public static class NeoBlockToolModificationProxyLow extends NeoBlockToolModificationProxy {
        static final NeoBlockToolModificationProxyLow INSTANCE = new NeoBlockToolModificationProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(BlockEvent.BlockToolModificationEvent e) { handle(e); }
    }

    public static class NeoBlockToolModificationProxyLowest extends NeoBlockToolModificationProxy {
        static final NeoBlockToolModificationProxyLowest INSTANCE = new NeoBlockToolModificationProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(BlockEvent.BlockToolModificationEvent e) { handle(e); }
    }
}