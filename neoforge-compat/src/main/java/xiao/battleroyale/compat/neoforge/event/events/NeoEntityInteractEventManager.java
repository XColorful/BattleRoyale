package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEntityInteractEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class NeoEntityInteractEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoEntityInteractProxyHighest.INSTANCE;
            case HIGH -> NeoEntityInteractProxyHigh.INSTANCE;
            case NORMAL -> NeoEntityInteractProxyNormal.INSTANCE;
            case LOW -> NeoEntityInteractProxyLow.INSTANCE;
            case LOWEST -> NeoEntityInteractProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoEntityInteractProxy extends AbstractNeoEventCommon {
        public NeoEntityInteractProxy() {
            super(EventType.ENTITY_INTERACT_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoEntityInteractEvent(event); }

        protected void handle(PlayerInteractEvent.EntityInteract event) { super.onEvent(event); }
    }

    public static class NeoEntityInteractProxyHighest extends NeoEntityInteractProxy {
        static final NeoEntityInteractProxyHighest INSTANCE = new NeoEntityInteractProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteract e) { handle(e); }
    }

    public static class NeoEntityInteractProxyHigh extends NeoEntityInteractProxy {
        static final NeoEntityInteractProxyHigh INSTANCE = new NeoEntityInteractProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteract e) { handle(e); }
    }

    public static class NeoEntityInteractProxyNormal extends NeoEntityInteractProxy {
        static final NeoEntityInteractProxyNormal INSTANCE = new NeoEntityInteractProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteract e) { handle(e); }
    }

    public static class NeoEntityInteractProxyLow extends NeoEntityInteractProxy {
        static final NeoEntityInteractProxyLow INSTANCE = new NeoEntityInteractProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteract e) { handle(e); }
    }

    public static class NeoEntityInteractProxyLowest extends NeoEntityInteractProxy {
        static final NeoEntityInteractProxyLowest INSTANCE = new NeoEntityInteractProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteract e) { handle(e); }
    }
}