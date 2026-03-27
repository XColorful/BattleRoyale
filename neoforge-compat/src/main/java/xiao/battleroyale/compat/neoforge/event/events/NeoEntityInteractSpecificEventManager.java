package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEntityInteractSpecificEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class NeoEntityInteractSpecificEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoEntityInteractSpecificProxyHighest.INSTANCE;
            case HIGH -> NeoEntityInteractSpecificProxyHigh.INSTANCE;
            case NORMAL -> NeoEntityInteractSpecificProxyNormal.INSTANCE;
            case LOW -> NeoEntityInteractSpecificProxyLow.INSTANCE;
            case LOWEST -> NeoEntityInteractSpecificProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoEntityInteractSpecificProxy extends AbstractNeoEventCommon {
        public NeoEntityInteractSpecificProxy() {
            super(EventType.ENTITY_INTERACT_SPECIFIC_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoEntityInteractSpecificEvent(event); }

        protected void handle(PlayerInteractEvent.EntityInteractSpecific event) { super.onEvent(event); }
    }

    public static class NeoEntityInteractSpecificProxyHighest extends NeoEntityInteractSpecificProxy {
        static final NeoEntityInteractSpecificProxyHighest INSTANCE = new NeoEntityInteractSpecificProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteractSpecific e) { handle(e); }
    }

    public static class NeoEntityInteractSpecificProxyHigh extends NeoEntityInteractSpecificProxy {
        static final NeoEntityInteractSpecificProxyHigh INSTANCE = new NeoEntityInteractSpecificProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteractSpecific e) { handle(e); }
    }

    public static class NeoEntityInteractSpecificProxyNormal extends NeoEntityInteractSpecificProxy {
        static final NeoEntityInteractSpecificProxyNormal INSTANCE = new NeoEntityInteractSpecificProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteractSpecific e) { handle(e); }
    }

    public static class NeoEntityInteractSpecificProxyLow extends NeoEntityInteractSpecificProxy {
        static final NeoEntityInteractSpecificProxyLow INSTANCE = new NeoEntityInteractSpecificProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteractSpecific e) { handle(e); }
    }

    public static class NeoEntityInteractSpecificProxyLowest extends NeoEntityInteractSpecificProxy {
        static final NeoEntityInteractSpecificProxyLowest INSTANCE = new NeoEntityInteractSpecificProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteractSpecific e) { handle(e); }
    }
}