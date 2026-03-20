package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeLivingDeathEvent;

public class LivingDeathEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> LivingDeathProxyHighest.INSTANCE;
            case HIGH -> LivingDeathProxyHigh.INSTANCE;
            case NORMAL -> LivingDeathProxyNormal.INSTANCE;
            case LOW -> LivingDeathProxyLow.INSTANCE;
            case LOWEST -> LivingDeathProxyLowest.INSTANCE;
        };
    }

    private static abstract class LivingDeathProxy extends AbstractEventCommon {
        public LivingDeathProxy() {
            super(EventType.LIVING_DEATH_EVENT);
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
            return new ForgeLivingDeathEvent(event);
        }

        protected void handle(LivingDeathEvent event) {
            super.onEvent(event);
        }
    }

    public static class LivingDeathProxyHighest extends LivingDeathProxy {
        static final LivingDeathProxyHighest INSTANCE = new LivingDeathProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(LivingDeathEvent e) { handle(e); }
    }

    public static class LivingDeathProxyHigh extends LivingDeathProxy {
        static final LivingDeathProxyHigh INSTANCE = new LivingDeathProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(LivingDeathEvent e) { handle(e); }
    }

    public static class LivingDeathProxyNormal extends LivingDeathProxy {
        static final LivingDeathProxyNormal INSTANCE = new LivingDeathProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(LivingDeathEvent e) { handle(e); }
    }

    public static class LivingDeathProxyLow extends LivingDeathProxy {
        static final LivingDeathProxyLow INSTANCE = new LivingDeathProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(LivingDeathEvent e) { handle(e); }
    }

    public static class LivingDeathProxyLowest extends LivingDeathProxy {
        static final LivingDeathProxyLowest INSTANCE = new LivingDeathProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(LivingDeathEvent e) { handle(e); }
    }
}