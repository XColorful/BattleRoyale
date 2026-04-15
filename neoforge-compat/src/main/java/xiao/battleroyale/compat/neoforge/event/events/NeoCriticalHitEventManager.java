package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoCriticalHitEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class NeoCriticalHitEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoCriticalHitProxyHighest.INSTANCE;
            case HIGH -> NeoCriticalHitProxyHigh.INSTANCE;
            case NORMAL -> NeoCriticalHitProxyNormal.INSTANCE;
            case LOW -> NeoCriticalHitProxyLow.INSTANCE;
            case LOWEST -> NeoCriticalHitProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoCriticalHitProxy extends AbstractNeoEventCommon {
        public NeoCriticalHitProxy() {
            super(EventType.CRITICAL_HIT_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoCriticalHitEvent(event); }

        protected void handle(CriticalHitEvent event) { super.onEvent(event); }
    }

    public static class NeoCriticalHitProxyHighest extends NeoCriticalHitProxy {
        static final NeoCriticalHitProxyHighest INSTANCE = new NeoCriticalHitProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(CriticalHitEvent e) { handle(e); }
    }

    public static class NeoCriticalHitProxyHigh extends NeoCriticalHitProxy {
        static final NeoCriticalHitProxyHigh INSTANCE = new NeoCriticalHitProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(CriticalHitEvent e) { handle(e); }
    }

    public static class NeoCriticalHitProxyNormal extends NeoCriticalHitProxy {
        static final NeoCriticalHitProxyNormal INSTANCE = new NeoCriticalHitProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(CriticalHitEvent e) { handle(e); }
    }

    public static class NeoCriticalHitProxyLow extends NeoCriticalHitProxy {
        static final NeoCriticalHitProxyLow INSTANCE = new NeoCriticalHitProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(CriticalHitEvent e) { handle(e); }
    }

    public static class NeoCriticalHitProxyLowest extends NeoCriticalHitProxy {
        static final NeoCriticalHitProxyLowest INSTANCE = new NeoCriticalHitProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(CriticalHitEvent e) { handle(e); }
    }
}