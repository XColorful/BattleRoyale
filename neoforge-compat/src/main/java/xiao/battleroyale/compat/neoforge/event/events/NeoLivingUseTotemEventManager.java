package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoLivingUseTotemEvent;

public class NeoLivingUseTotemEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoLivingUseTotemProxyHighest.INSTANCE;
            case HIGH -> NeoLivingUseTotemProxyHigh.INSTANCE;
            case NORMAL -> NeoLivingUseTotemProxyNormal.INSTANCE;
            case LOW -> NeoLivingUseTotemProxyLow.INSTANCE;
            case LOWEST -> NeoLivingUseTotemProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoLivingUseTotemProxy extends AbstractNeoEventCommon {
        public NeoLivingUseTotemProxy() {
            super(EventType.LIVING_USE_TOTEM_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoLivingUseTotemEvent(event); }

        protected void handle(LivingUseTotemEvent event) { super.onEvent(event); }
    }

    public static class NeoLivingUseTotemProxyHighest extends NeoLivingUseTotemProxy {
        static final NeoLivingUseTotemProxyHighest INSTANCE = new NeoLivingUseTotemProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(LivingUseTotemEvent e) { handle(e); }
    }

    public static class NeoLivingUseTotemProxyHigh extends NeoLivingUseTotemProxy {
        static final NeoLivingUseTotemProxyHigh INSTANCE = new NeoLivingUseTotemProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(LivingUseTotemEvent e) { handle(e); }
    }

    public static class NeoLivingUseTotemProxyNormal extends NeoLivingUseTotemProxy {
        static final NeoLivingUseTotemProxyNormal INSTANCE = new NeoLivingUseTotemProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(LivingUseTotemEvent e) { handle(e); }
    }

    public static class NeoLivingUseTotemProxyLow extends NeoLivingUseTotemProxy {
        static final NeoLivingUseTotemProxyLow INSTANCE = new NeoLivingUseTotemProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(LivingUseTotemEvent e) { handle(e); }
    }

    public static class NeoLivingUseTotemProxyLowest extends NeoLivingUseTotemProxy {
        static final NeoLivingUseTotemProxyLowest INSTANCE = new NeoLivingUseTotemProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(LivingUseTotemEvent e) { handle(e); }
    }
}