package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoRightClickBlockEvent;

public class NeoRightClickBlockEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoRightClickBlockProxyHighest.INSTANCE;
            case HIGH -> NeoRightClickBlockProxyHigh.INSTANCE;
            case NORMAL -> NeoRightClickBlockProxyNormal.INSTANCE;
            case LOW -> NeoRightClickBlockProxyLow.INSTANCE;
            case LOWEST -> NeoRightClickBlockProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoRightClickBlockProxy extends AbstractNeoEventCommon {
        public NeoRightClickBlockProxy() {
            super(EventType.RIGHT_CLICK_BLOCK_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoRightClickBlockEvent(event); }

        protected void handle(PlayerInteractEvent.RightClickBlock event) { super.onEvent(event); }
    }

    public static class NeoRightClickBlockProxyHighest extends NeoRightClickBlockProxy {
        static final NeoRightClickBlockProxyHighest INSTANCE = new NeoRightClickBlockProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickBlock e) { handle(e); }
    }

    public static class NeoRightClickBlockProxyHigh extends NeoRightClickBlockProxy {
        static final NeoRightClickBlockProxyHigh INSTANCE = new NeoRightClickBlockProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickBlock e) { handle(e); }
    }

    public static class NeoRightClickBlockProxyNormal extends NeoRightClickBlockProxy {
        static final NeoRightClickBlockProxyNormal INSTANCE = new NeoRightClickBlockProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickBlock e) { handle(e); }
    }

    public static class NeoRightClickBlockProxyLow extends NeoRightClickBlockProxy {
        static final NeoRightClickBlockProxyLow INSTANCE = new NeoRightClickBlockProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickBlock e) { handle(e); }
    }

    public static class NeoRightClickBlockProxyLowest extends NeoRightClickBlockProxy {
        static final NeoRightClickBlockProxyLowest INSTANCE = new NeoRightClickBlockProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickBlock e) { handle(e); }
    }
}