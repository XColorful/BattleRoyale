package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoLeftClickBlockEvent;

public class NeoLeftClickBlockEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoLeftClickBlockProxyHighest.INSTANCE;
            case HIGH -> NeoLeftClickBlockProxyHigh.INSTANCE;
            case NORMAL -> NeoLeftClickBlockProxyNormal.INSTANCE;
            case LOW -> NeoLeftClickBlockProxyLow.INSTANCE;
            case LOWEST -> NeoLeftClickBlockProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoLeftClickBlockProxy extends AbstractNeoEventCommon {
        public NeoLeftClickBlockProxy() {
            super(EventType.LEFT_CLICK_BLOCK_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoLeftClickBlockEvent(event); }

        protected void handle(PlayerInteractEvent.LeftClickBlock event) { super.onEvent(event); }
    }

    public static class NeoLeftClickBlockProxyHighest extends NeoLeftClickBlockProxy {
        static final NeoLeftClickBlockProxyHighest INSTANCE = new NeoLeftClickBlockProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.LeftClickBlock e) { handle(e); }
    }

    public static class NeoLeftClickBlockProxyHigh extends NeoLeftClickBlockProxy {
        static final NeoLeftClickBlockProxyHigh INSTANCE = new NeoLeftClickBlockProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.LeftClickBlock e) { handle(e); }
    }

    public static class NeoLeftClickBlockProxyNormal extends NeoLeftClickBlockProxy {
        static final NeoLeftClickBlockProxyNormal INSTANCE = new NeoLeftClickBlockProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.LeftClickBlock e) { handle(e); }
    }

    public static class NeoLeftClickBlockProxyLow extends NeoLeftClickBlockProxy {
        static final NeoLeftClickBlockProxyLow INSTANCE = new NeoLeftClickBlockProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.LeftClickBlock e) { handle(e); }
    }

    public static class NeoLeftClickBlockProxyLowest extends NeoLeftClickBlockProxy {
        static final NeoLeftClickBlockProxyLowest INSTANCE = new NeoLeftClickBlockProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.LeftClickBlock e) { handle(e); }
    }
}