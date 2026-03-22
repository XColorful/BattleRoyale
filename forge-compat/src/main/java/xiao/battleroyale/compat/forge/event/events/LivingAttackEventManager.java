package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeLivingAttackEvent;

public class LivingAttackEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> LivingAttackProxyHighest.INSTANCE;
            case HIGH -> LivingAttackProxyHigh.INSTANCE;
            case NORMAL -> LivingAttackProxyNormal.INSTANCE;
            case LOW -> LivingAttackProxyLow.INSTANCE;
            case LOWEST -> LivingAttackProxyLowest.INSTANCE;
        };
    }

    private static abstract class LivingAttackProxy extends AbstractEventCommon {
        public LivingAttackProxy() {
            super(EventType.LIVING_ATTACK_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeLivingAttackEvent(event); }

        protected void handle(LivingAttackEvent event) { super.onEvent(event); }
    }

    public static class LivingAttackProxyHighest extends LivingAttackProxy {
        static final LivingAttackProxyHighest INSTANCE = new LivingAttackProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(LivingAttackEvent e) { handle(e); }
    }

    public static class LivingAttackProxyHigh extends LivingAttackProxy {
        static final LivingAttackProxyHigh INSTANCE = new LivingAttackProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(LivingAttackEvent e) { handle(e); }
    }

    public static class LivingAttackProxyNormal extends LivingAttackProxy {
        static final LivingAttackProxyNormal INSTANCE = new LivingAttackProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(LivingAttackEvent e) { handle(e); }
    }

    public static class LivingAttackProxyLow extends LivingAttackProxy {
        static final LivingAttackProxyLow INSTANCE = new LivingAttackProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(LivingAttackEvent e) { handle(e); }
    }

    public static class LivingAttackProxyLowest extends LivingAttackProxy {
        static final LivingAttackProxyLowest INSTANCE = new LivingAttackProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(LivingAttackEvent e) { handle(e); }
    }
}