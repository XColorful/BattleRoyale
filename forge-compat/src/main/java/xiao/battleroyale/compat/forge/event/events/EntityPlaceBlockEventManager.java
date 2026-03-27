package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEntityPlaceBlockEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class EntityPlaceBlockEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> EntityPlaceBlockProxyHighest.INSTANCE;
            case HIGH -> EntityPlaceBlockProxyHigh.INSTANCE;
            case NORMAL -> EntityPlaceBlockProxyNormal.INSTANCE;
            case LOW -> EntityPlaceBlockProxyLow.INSTANCE;
            case LOWEST -> EntityPlaceBlockProxyLowest.INSTANCE;
        };
    }

    private static abstract class EntityPlaceBlockProxy extends AbstractEventCommon {
        public EntityPlaceBlockProxy() {
            super(EventType.ENTITY_PLACE_BLOCK_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeEntityPlaceBlockEvent(event); }

        protected void handle(BlockEvent.EntityPlaceEvent event) { super.onEvent(event); }
    }

    public static class EntityPlaceBlockProxyHighest extends EntityPlaceBlockProxy {
        static final EntityPlaceBlockProxyHighest INSTANCE = new EntityPlaceBlockProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(BlockEvent.EntityPlaceEvent e) { handle(e); }
    }

    public static class EntityPlaceBlockProxyHigh extends EntityPlaceBlockProxy {
        static final EntityPlaceBlockProxyHigh INSTANCE = new EntityPlaceBlockProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(BlockEvent.EntityPlaceEvent e) { handle(e); }
    }

    public static class EntityPlaceBlockProxyNormal extends EntityPlaceBlockProxy {
        static final EntityPlaceBlockProxyNormal INSTANCE = new EntityPlaceBlockProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(BlockEvent.EntityPlaceEvent e) { handle(e); }
    }

    public static class EntityPlaceBlockProxyLow extends EntityPlaceBlockProxy {
        static final EntityPlaceBlockProxyLow INSTANCE = new EntityPlaceBlockProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(BlockEvent.EntityPlaceEvent e) { handle(e); }
    }

    public static class EntityPlaceBlockProxyLowest extends EntityPlaceBlockProxy {
        static final EntityPlaceBlockProxyLowest INSTANCE = new EntityPlaceBlockProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(BlockEvent.EntityPlaceEvent e) { handle(e); }
    }
}