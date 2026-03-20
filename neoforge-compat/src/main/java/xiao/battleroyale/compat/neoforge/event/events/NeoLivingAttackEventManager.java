package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoLivingAttackEvent;

public class NeoLivingAttackEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoLivingAttackProxyHighest.INSTANCE;
            case HIGH -> NeoLivingAttackProxyHigh.INSTANCE;
            case NORMAL -> NeoLivingAttackProxyNormal.INSTANCE;
            case LOW -> NeoLivingAttackProxyLow.INSTANCE;
            case LOWEST -> NeoLivingAttackProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoLivingAttackProxy extends AbstractNeoEventCommon {
        public NeoLivingAttackProxy() {
            super(EventType.LIVING_ATTACK_EVENT);
        }

        @Override protected void registerToNeo() { NeoForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToNeo() { NeoForge.EVENT_BUS.unregister(this); }
        @Override protected NeoEvent getNeoEventType(Event event) { return new NeoLivingAttackEvent(event); }

        protected void handle(LivingIncomingDamageEvent event) { super.onEvent(event); }
    }

    public static class NeoLivingAttackProxyHighest extends NeoLivingAttackProxy {
        static final NeoLivingAttackProxyHighest INSTANCE = new NeoLivingAttackProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(LivingIncomingDamageEvent e) { handle(e); }
    }

    public static class NeoLivingAttackProxyHigh extends NeoLivingAttackProxy {
        static final NeoLivingAttackProxyHigh INSTANCE = new NeoLivingAttackProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(LivingIncomingDamageEvent e) { handle(e); }
    }

    public static class NeoLivingAttackProxyNormal extends NeoLivingAttackProxy {
        static final NeoLivingAttackProxyNormal INSTANCE = new NeoLivingAttackProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(LivingIncomingDamageEvent e) { handle(e); }
    }

    public static class NeoLivingAttackProxyLow extends NeoLivingAttackProxy {
        static final NeoLivingAttackProxyLow INSTANCE = new NeoLivingAttackProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(LivingIncomingDamageEvent e) { handle(e); }
    }

    public static class NeoLivingAttackProxyLowest extends NeoLivingAttackProxy {
        static final NeoLivingAttackProxyLowest INSTANCE = new NeoLivingAttackProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(LivingIncomingDamageEvent e) { handle(e); }
    }
}