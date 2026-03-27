package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEntityInteractEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class EntityInteractEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> EntityInteractProxyHighest.INSTANCE;
            case HIGH -> EntityInteractProxyHigh.INSTANCE;
            case NORMAL -> EntityInteractProxyNormal.INSTANCE;
            case LOW -> EntityInteractProxyLow.INSTANCE;
            case LOWEST -> EntityInteractProxyLowest.INSTANCE;
        };
    }

    private static abstract class EntityInteractProxy extends AbstractEventCommon {
        public EntityInteractProxy() {
            super(EventType.ENTITY_INTERACT_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeEntityInteractEvent(event); }

        protected void handle(PlayerInteractEvent.EntityInteract event) { super.onEvent(event); }
    }

    public static class EntityInteractProxyHighest extends EntityInteractProxy {
        static final EntityInteractProxyHighest INSTANCE = new EntityInteractProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteract e) { handle(e); }
    }

    public static class EntityInteractProxyHigh extends EntityInteractProxy {
        static final EntityInteractProxyHigh INSTANCE = new EntityInteractProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteract e) { handle(e); }
    }

    public static class EntityInteractProxyNormal extends EntityInteractProxy {
        static final EntityInteractProxyNormal INSTANCE = new EntityInteractProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteract e) { handle(e); }
    }

    public static class EntityInteractProxyLow extends EntityInteractProxy {
        static final EntityInteractProxyLow INSTANCE = new EntityInteractProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteract e) { handle(e); }
    }

    public static class EntityInteractProxyLowest extends EntityInteractProxy {
        static final EntityInteractProxyLowest INSTANCE = new EntityInteractProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.EntityInteract e) { handle(e); }
    }
}