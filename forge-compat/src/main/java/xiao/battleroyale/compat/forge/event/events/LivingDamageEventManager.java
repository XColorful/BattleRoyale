package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeLivingDamageEvent;

public class LivingDamageEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> LivingDamageProxyHighest.INSTANCE;
            case HIGH -> LivingDamageProxyHigh.INSTANCE;
            case NORMAL -> LivingDamageProxyNormal.INSTANCE;
            case LOW -> LivingDamageProxyLow.INSTANCE;
            case LOWEST -> LivingDamageProxyLowest.INSTANCE;
        };
    }

    private static abstract class LivingDamageProxy extends AbstractEventCommon {
        public LivingDamageProxy() {
            super(EventType.LIVING_DAMAGE_EVENT);
        }

        @Override
        protected void registerToForge() {
            MinecraftForge.EVENT_BUS.register(this);
        }

        @Override
        protected void unregisterToForge() {
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        @Override
        protected ForgeEvent getForgeEventType(Event event) {
            return new ForgeLivingDamageEvent(event);
        }

        protected void handle(LivingDamageEvent event) {
            super.onEvent(event);
        }
    }

    public static class LivingDamageProxyHighest extends LivingDamageProxy {
        static final LivingDamageProxyHighest INSTANCE = new LivingDamageProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class LivingDamageProxyHigh extends LivingDamageProxy {
        static final LivingDamageProxyHigh INSTANCE = new LivingDamageProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class LivingDamageProxyNormal extends LivingDamageProxy {
        static final LivingDamageProxyNormal INSTANCE = new LivingDamageProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class LivingDamageProxyLow extends LivingDamageProxy {
        static final LivingDamageProxyLow INSTANCE = new LivingDamageProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class LivingDamageProxyLowest extends LivingDamageProxy {
        static final LivingDamageProxyLowest INSTANCE = new LivingDamageProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }
}