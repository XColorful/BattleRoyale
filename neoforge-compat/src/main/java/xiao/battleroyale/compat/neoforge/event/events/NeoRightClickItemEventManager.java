package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoRightClickItemEvent;

public class NeoRightClickItemEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoRightClickItemProxyHighest.INSTANCE;
            case HIGH -> NeoRightClickItemProxyHigh.INSTANCE;
            case NORMAL -> NeoRightClickItemProxyNormal.INSTANCE;
            case LOW -> NeoRightClickItemProxyLow.INSTANCE;
            case LOWEST -> NeoRightClickItemProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoRightClickItemProxy extends AbstractNeoEventCommon {
        public NeoRightClickItemProxy() {
            super(EventType.RIGHT_CLICK_ITEM_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoRightClickItemEvent(event); }

        protected void handle(PlayerInteractEvent.RightClickItem event) { super.onEvent(event); }
    }

    public static class NeoRightClickItemProxyHighest extends NeoRightClickItemProxy {
        static final NeoRightClickItemProxyHighest INSTANCE = new NeoRightClickItemProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickItem e) { handle(e); }
    }

    public static class NeoRightClickItemProxyHigh extends NeoRightClickItemProxy {
        static final NeoRightClickItemProxyHigh INSTANCE = new NeoRightClickItemProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickItem e) { handle(e); }
    }

    public static class NeoRightClickItemProxyNormal extends NeoRightClickItemProxy {
        static final NeoRightClickItemProxyNormal INSTANCE = new NeoRightClickItemProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickItem e) { handle(e); }
    }

    public static class NeoRightClickItemProxyLow extends NeoRightClickItemProxy {
        static final NeoRightClickItemProxyLow INSTANCE = new NeoRightClickItemProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickItem e) { handle(e); }
    }

    public static class NeoRightClickItemProxyLowest extends NeoRightClickItemProxy {
        static final NeoRightClickItemProxyLowest INSTANCE = new NeoRightClickItemProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickItem e) { handle(e); }
    }
}