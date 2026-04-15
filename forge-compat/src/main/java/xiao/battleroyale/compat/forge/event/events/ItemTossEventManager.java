package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeItemTossEvent;

public class ItemTossEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> ItemTossProxyHighest.INSTANCE;
            case HIGH -> ItemTossProxyHigh.INSTANCE;
            case NORMAL -> ItemTossProxyNormal.INSTANCE;
            case LOW -> ItemTossProxyLow.INSTANCE;
            case LOWEST -> ItemTossProxyLowest.INSTANCE;
        };
    }

    private static abstract class ItemTossProxy extends AbstractEventCommon {
        public ItemTossProxy() {
            super(EventType.ITEM_TOSS_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeItemTossEvent(event); }

        protected void handle(ItemTossEvent event) { super.onEvent(event); }
    }

    public static class ItemTossProxyHighest extends ItemTossProxy {
        static final ItemTossProxyHighest INSTANCE = new ItemTossProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(ItemTossEvent e) { handle(e); }
    }

    public static class ItemTossProxyHigh extends ItemTossProxy {
        static final ItemTossProxyHigh INSTANCE = new ItemTossProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(ItemTossEvent e) { handle(e); }
    }

    public static class ItemTossProxyNormal extends ItemTossProxy {
        static final ItemTossProxyNormal INSTANCE = new ItemTossProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(ItemTossEvent e) { handle(e); }
    }

    public static class ItemTossProxyLow extends ItemTossProxy {
        static final ItemTossProxyLow INSTANCE = new ItemTossProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(ItemTossEvent e) { handle(e); }
    }

    public static class ItemTossProxyLowest extends ItemTossProxy {
        static final ItemTossProxyLowest INSTANCE = new ItemTossProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(ItemTossEvent e) { handle(e); }
    }
}