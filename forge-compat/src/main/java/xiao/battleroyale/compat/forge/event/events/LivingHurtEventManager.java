package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeLivingHurtEvent;

public class LivingHurtEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> LivingHurtProxyHighest.INSTANCE;
            case HIGH -> LivingHurtProxyHigh.INSTANCE;
            case NORMAL -> LivingHurtProxyNormal.INSTANCE;
            case LOW -> LivingHurtProxyLow.INSTANCE;
            case LOWEST -> LivingHurtProxyLowest.INSTANCE;
        };
    }

    private static abstract class LivingHurtProxy extends AbstractEventCommon {
        public LivingHurtProxy() {
            super(EventType.LIVING_HURT_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeLivingHurtEvent(event); }

        protected void handle(LivingHurtEvent event) { super.onEvent(event); }
    }

    public static class LivingHurtProxyHighest extends LivingHurtProxy {
        static final LivingHurtProxyHighest INSTANCE = new LivingHurtProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(LivingHurtEvent e) { handle(e); }
    }

    public static class LivingHurtProxyHigh extends LivingHurtProxy {
        static final LivingHurtProxyHigh INSTANCE = new LivingHurtProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(LivingHurtEvent e) { handle(e); }
    }

    public static class LivingHurtProxyNormal extends LivingHurtProxy {
        static final LivingHurtProxyNormal INSTANCE = new LivingHurtProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(LivingHurtEvent e) { handle(e); }
    }

    public static class LivingHurtProxyLow extends LivingHurtProxy {
        static final LivingHurtProxyLow INSTANCE = new LivingHurtProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(LivingHurtEvent e) { handle(e); }
    }

    public static class LivingHurtProxyLowest extends LivingHurtProxy {
        static final LivingHurtProxyLowest INSTANCE = new LivingHurtProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(LivingHurtEvent e) { handle(e); }
    }
}