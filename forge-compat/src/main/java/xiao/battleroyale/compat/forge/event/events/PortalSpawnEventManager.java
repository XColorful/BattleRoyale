package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgePortalSpawnEvent;

public class PortalSpawnEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> PortalSpawnProxyHighest.INSTANCE;
            case HIGH -> PortalSpawnProxyHigh.INSTANCE;
            case NORMAL -> PortalSpawnProxyNormal.INSTANCE;
            case LOW -> PortalSpawnProxyLow.INSTANCE;
            case LOWEST -> PortalSpawnProxyLowest.INSTANCE;
        };
    }

    private static abstract class PortalSpawnProxy extends AbstractEventCommon {
        public PortalSpawnProxy() {
            super(EventType.PORTAL_SPAWN_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgePortalSpawnEvent(event); }

        protected void handle(BlockEvent.PortalSpawnEvent event) { super.onEvent(event); }
    }

    public static class PortalSpawnProxyHighest extends PortalSpawnProxy {
        static final PortalSpawnProxyHighest INSTANCE = new PortalSpawnProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(BlockEvent.PortalSpawnEvent e) { handle(e); }
    }

    public static class PortalSpawnProxyHigh extends PortalSpawnProxy {
        static final PortalSpawnProxyHigh INSTANCE = new PortalSpawnProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(BlockEvent.PortalSpawnEvent e) { handle(e); }
    }

    public static class PortalSpawnProxyNormal extends PortalSpawnProxy {
        static final PortalSpawnProxyNormal INSTANCE = new PortalSpawnProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(BlockEvent.PortalSpawnEvent e) { handle(e); }
    }

    public static class PortalSpawnProxyLow extends PortalSpawnProxy {
        static final PortalSpawnProxyLow INSTANCE = new PortalSpawnProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(BlockEvent.PortalSpawnEvent e) { handle(e); }
    }

    public static class PortalSpawnProxyLowest extends PortalSpawnProxy {
        static final PortalSpawnProxyLowest INSTANCE = new PortalSpawnProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(BlockEvent.PortalSpawnEvent e) { handle(e); }
    }
}