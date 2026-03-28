package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEntityInteractSpecificEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class EntityInteractSpecificEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> EntityInteractSpecificProxyHighest.INSTANCE;
            case HIGH -> EntityInteractSpecificProxyHigh.INSTANCE;
            case NORMAL -> EntityInteractSpecificProxyNormal.INSTANCE;
            case LOW -> EntityInteractSpecificProxyLow.INSTANCE;
            case LOWEST -> EntityInteractSpecificProxyLowest.INSTANCE;
        };
    }

    private static abstract class EntityInteractSpecificProxy extends AbstractEventCommon {
        public EntityInteractSpecificProxy() {
            super(EventType.ENTITY_INTERACT_SPECIFIC_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeEntityInteractSpecificEvent(event); }

        protected void handle(PlayerInteractEvent.EntityInteractSpecific event) { super.onEvent(event); }
    }

    public static class EntityInteractSpecificProxyHighest extends EntityInteractSpecificProxy {
        static final EntityInteractSpecificProxyHighest INSTANCE = new EntityInteractSpecificProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteractSpecific e) { handle(e); }
    }

    public static class EntityInteractSpecificProxyHigh extends EntityInteractSpecificProxy {
        static final EntityInteractSpecificProxyHigh INSTANCE = new EntityInteractSpecificProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteractSpecific e) { handle(e); }
    }

    public static class EntityInteractSpecificProxyNormal extends EntityInteractSpecificProxy {
        static final EntityInteractSpecificProxyNormal INSTANCE = new EntityInteractSpecificProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteractSpecific e) { handle(e); }
    }

    public static class EntityInteractSpecificProxyLow extends EntityInteractSpecificProxy {
        static final EntityInteractSpecificProxyLow INSTANCE = new EntityInteractSpecificProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteractSpecific e) { handle(e); }
    }

    public static class EntityInteractSpecificProxyLowest extends EntityInteractSpecificProxy {
        static final EntityInteractSpecificProxyLowest INSTANCE = new EntityInteractSpecificProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteractSpecific e) { handle(e); }
    }
}