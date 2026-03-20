package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoLivingDeathEvent;

public class NeoLivingDeathEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> NeoLivingDeathProxyHighest.INSTANCE;
            case HIGH -> NeoLivingDeathProxyHigh.INSTANCE;
            case NORMAL -> NeoLivingDeathProxyNormal.INSTANCE;
            case LOW -> NeoLivingDeathProxyLow.INSTANCE;
            case LOWEST -> NeoLivingDeathProxyLowest.INSTANCE;
        };
    }

    private static abstract class NeoLivingDeathProxy extends AbstractNeoEventCommon {
        public NeoLivingDeathProxy() {
            super(EventType.LIVING_DEATH_EVENT);
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
            return new NeoLivingDeathEvent(event);
        }

        protected void handle(LivingDeathEvent event) {
            super.onEvent(event);
        }
    }

    public static class NeoLivingDeathProxyHighest extends NeoLivingDeathProxy {
        static final NeoLivingDeathProxyHighest INSTANCE = new NeoLivingDeathProxyHighest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(LivingDeathEvent e) { handle(e); }
    }

    public static class NeoLivingDeathProxyHigh extends NeoLivingDeathProxy {
        static final NeoLivingDeathProxyHigh INSTANCE = new NeoLivingDeathProxyHigh();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(LivingDeathEvent e) { handle(e); }
    }

    public static class NeoLivingDeathProxyNormal extends NeoLivingDeathProxy {
        static final NeoLivingDeathProxyNormal INSTANCE = new NeoLivingDeathProxyNormal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(LivingDeathEvent e) { handle(e); }
    }

    public static class NeoLivingDeathProxyLow extends NeoLivingDeathProxy {
        static final NeoLivingDeathProxyLow INSTANCE = new NeoLivingDeathProxyLow();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(LivingDeathEvent e) { handle(e); }
    }

    public static class NeoLivingDeathProxyLowest extends NeoLivingDeathProxy {
        static final NeoLivingDeathProxyLowest INSTANCE = new NeoLivingDeathProxyLowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(LivingDeathEvent e) { handle(e); }
    }
}