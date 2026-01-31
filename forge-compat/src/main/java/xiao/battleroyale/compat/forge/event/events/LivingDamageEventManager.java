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
        return getProxy(priority).addEventHander(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> Highest.INSTANCE;
            case HIGH -> High.INSTANCE;
            case NORMAL -> Normal.INSTANCE;
            case LOW -> Low.INSTANCE;
            case LOWEST -> Lowest.INSTANCE;
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

    public static class Highest extends LivingDamageProxy {
        static final Highest INSTANCE = new Highest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class High extends LivingDamageProxy {
        static final High INSTANCE = new High();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class Normal extends LivingDamageProxy {
        static final Normal INSTANCE = new Normal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class Low extends LivingDamageProxy {
        static final Low INSTANCE = new Low();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }

    public static class Lowest extends LivingDamageProxy {
        static final Lowest INSTANCE = new Lowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(LivingDamageEvent e) { handle(e); }
    }
}