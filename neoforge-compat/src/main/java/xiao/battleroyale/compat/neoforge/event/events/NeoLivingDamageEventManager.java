package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoLivingDamageEvent;

public class NeoLivingDamageEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoLivingDamageProxyHighest.INSTANCE;
            case HIGH -> NeoLivingDamageProxyHigh.INSTANCE;
            case NORMAL -> NeoLivingDamageProxyNormal.INSTANCE;
            case LOW -> NeoLivingDamageProxyLow.INSTANCE;
            case LOWEST -> NeoLivingDamageProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoLivingDamageProxy extends AbstractNeoEventCommon {
        public NeoLivingDamageProxy() {
            super(EventType.LIVING_DAMAGE_EVENT);
        }

        @Override
        protected void registerToNeo() {
            NeoForge.EVENT_BUS.register(this);
        }

        @Override
        protected void unregisterToNeo() {
            NeoForge.EVENT_BUS.unregister(this);
        }

        @Override
        protected NeoEvent getNeoEventType(Event event) {
            return new NeoLivingDamageEvent(event);
        }

        protected void handle(LivingDamageEvent event) {
            super.onEvent(event);
        }
    }

    public static class NeoLivingDamageProxyHighest extends NeoLivingDamageProxy {
        static final NeoLivingDamageProxyHighest INSTANCE = new NeoLivingDamageProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class NeoLivingDamageProxyHigh extends NeoLivingDamageProxy {
        static final NeoLivingDamageProxyHigh INSTANCE = new NeoLivingDamageProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class NeoLivingDamageProxyNormal extends NeoLivingDamageProxy {
        static final NeoLivingDamageProxyNormal INSTANCE = new NeoLivingDamageProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class NeoLivingDamageProxyLow extends NeoLivingDamageProxy {
        static final NeoLivingDamageProxyLow INSTANCE = new NeoLivingDamageProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class NeoLivingDamageProxyLowest extends NeoLivingDamageProxy {
        static final NeoLivingDamageProxyLowest INSTANCE = new NeoLivingDamageProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }
}