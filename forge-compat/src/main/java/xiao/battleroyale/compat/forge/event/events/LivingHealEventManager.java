package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeLivingHealEvent;

public class LivingHealEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> LivingHealProxyHighest.INSTANCE;
            case HIGH -> LivingHealProxyHigh.INSTANCE;
            case NORMAL -> LivingHealProxyNormal.INSTANCE;
            case LOW -> LivingHealProxyLow.INSTANCE;
            case LOWEST -> LivingHealProxyLowest.INSTANCE;
        };
    }

    private static abstract class LivingHealProxy extends AbstractEventCommon {
        public LivingHealProxy() {
            super(EventType.LIVING_HEAL_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeLivingHealEvent(event); }

        protected void handle(LivingHealEvent event) { super.onEvent(event); }
    }

    public static class LivingHealProxyHighest extends LivingHealProxy {
        static final LivingHealProxyHighest INSTANCE = new LivingHealProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(LivingHealEvent e) { handle(e); }
    }

    public static class LivingHealProxyHigh extends LivingHealProxy {
        static final LivingHealProxyHigh INSTANCE = new LivingHealProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(LivingHealEvent e) { handle(e); }
    }

    public static class LivingHealProxyNormal extends LivingHealProxy {
        static final LivingHealProxyNormal INSTANCE = new LivingHealProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(LivingHealEvent e) { handle(e); }
    }

    public static class LivingHealProxyLow extends LivingHealProxy {
        static final LivingHealProxyLow INSTANCE = new LivingHealProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(LivingHealEvent e) { handle(e); }
    }

    public static class LivingHealProxyLowest extends LivingHealProxy {
        static final LivingHealProxyLowest INSTANCE = new LivingHealProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(LivingHealEvent e) { handle(e); }
    }
}