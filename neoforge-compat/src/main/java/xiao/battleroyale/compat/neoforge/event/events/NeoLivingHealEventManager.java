package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoLivingHealEvent;

public class NeoLivingHealEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoLivingHealProxyHighest.INSTANCE;
            case HIGH -> NeoLivingHealProxyHigh.INSTANCE;
            case NORMAL -> NeoLivingHealProxyNormal.INSTANCE;
            case LOW -> NeoLivingHealProxyLow.INSTANCE;
            case LOWEST -> NeoLivingHealProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoLivingHealProxy extends AbstractNeoEventCommon {
        public NeoLivingHealProxy() {
            super(EventType.LIVING_HEAL_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoLivingHealEvent(event); }

        protected void handle(LivingHealEvent event) { super.onEvent(event); }
    }

    public static class NeoLivingHealProxyHighest extends NeoLivingHealProxy {
        static final NeoLivingHealProxyHighest INSTANCE = new NeoLivingHealProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(LivingHealEvent e) { handle(e); }
    }

    public static class NeoLivingHealProxyHigh extends NeoLivingHealProxy {
        static final NeoLivingHealProxyHigh INSTANCE = new NeoLivingHealProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(LivingHealEvent e) { handle(e); }
    }

    public static class NeoLivingHealProxyNormal extends NeoLivingHealProxy {
        static final NeoLivingHealProxyNormal INSTANCE = new NeoLivingHealProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(LivingHealEvent e) { handle(e); }
    }

    public static class NeoLivingHealProxyLow extends NeoLivingHealProxy {
        static final NeoLivingHealProxyLow INSTANCE = new NeoLivingHealProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(LivingHealEvent e) { handle(e); }
    }

    public static class NeoLivingHealProxyLowest extends NeoLivingHealProxy {
        static final NeoLivingHealProxyLowest INSTANCE = new NeoLivingHealProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(LivingHealEvent e) { handle(e); }
    }
}