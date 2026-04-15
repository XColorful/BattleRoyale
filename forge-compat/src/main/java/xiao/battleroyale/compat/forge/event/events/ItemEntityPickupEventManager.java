package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeItemEntityPickupEvent;

public class ItemEntityPickupEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> ItemEntityPickupProxyHighest.INSTANCE;
            case HIGH -> ItemEntityPickupProxyHigh.INSTANCE;
            case NORMAL -> ItemEntityPickupProxyNormal.INSTANCE;
            case LOW -> ItemEntityPickupProxyLow.INSTANCE;
            case LOWEST -> ItemEntityPickupProxyLowest.INSTANCE;
        };
    }

    private static abstract class ItemEntityPickupProxy extends AbstractEventCommon {
        public ItemEntityPickupProxy() {
            super(EventType.ITEM_ENTITY_PICKUP_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeItemEntityPickupEvent(event); }

        protected void handle(EntityItemPickupEvent event) { super.onEvent(event); }
    }

    public static class ItemEntityPickupProxyHighest extends ItemEntityPickupProxy {
        static final ItemEntityPickupProxyHighest INSTANCE = new ItemEntityPickupProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(EntityItemPickupEvent e) { handle(e); }
    }

    public static class ItemEntityPickupProxyHigh extends ItemEntityPickupProxy {
        static final ItemEntityPickupProxyHigh INSTANCE = new ItemEntityPickupProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(EntityItemPickupEvent e) { handle(e); }
    }

    public static class ItemEntityPickupProxyNormal extends ItemEntityPickupProxy {
        static final ItemEntityPickupProxyNormal INSTANCE = new ItemEntityPickupProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(EntityItemPickupEvent e) { handle(e); }
    }

    public static class ItemEntityPickupProxyLow extends ItemEntityPickupProxy {
        static final ItemEntityPickupProxyLow INSTANCE = new ItemEntityPickupProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(EntityItemPickupEvent e) { handle(e); }
    }

    public static class ItemEntityPickupProxyLowest extends ItemEntityPickupProxy {
        static final ItemEntityPickupProxyLowest INSTANCE = new ItemEntityPickupProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(EntityItemPickupEvent e) { handle(e); }
    }
}