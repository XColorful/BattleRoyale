package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeCriticalHitEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class CriticalHitEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> CriticalHitProxyHighest.INSTANCE;
            case HIGH -> CriticalHitProxyHigh.INSTANCE;
            case NORMAL -> CriticalHitProxyNormal.INSTANCE;
            case LOW -> CriticalHitProxyLow.INSTANCE;
            case LOWEST -> CriticalHitProxyLowest.INSTANCE;
        };
    }

    private static abstract class CriticalHitProxy extends AbstractEventCommon {
        public CriticalHitProxy() {
            super(EventType.CRITICAL_HIT_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeCriticalHitEvent(event); }

        protected void handle(CriticalHitEvent event) { super.onEvent(event); }
    }

    public static class CriticalHitProxyHighest extends CriticalHitProxy {
        static final CriticalHitProxyHighest INSTANCE = new CriticalHitProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(CriticalHitEvent e) { handle(e); }
    }

    public static class CriticalHitProxyHigh extends CriticalHitProxy {
        static final CriticalHitProxyHigh INSTANCE = new CriticalHitProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(CriticalHitEvent e) { handle(e); }
    }

    public static class CriticalHitProxyNormal extends CriticalHitProxy {
        static final CriticalHitProxyNormal INSTANCE = new CriticalHitProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(CriticalHitEvent e) { handle(e); }
    }

    public static class CriticalHitProxyLow extends CriticalHitProxy {
        static final CriticalHitProxyLow INSTANCE = new CriticalHitProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(CriticalHitEvent e) { handle(e); }
    }

    public static class CriticalHitProxyLowest extends CriticalHitProxy {
        static final CriticalHitProxyLowest INSTANCE = new CriticalHitProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(CriticalHitEvent e) { handle(e); }
    }
}