package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeRightClickItemEvent;

public class RightClickItemEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> RightClickItemProxyHighest.INSTANCE;
            case HIGH -> RightClickItemProxyHigh.INSTANCE;
            case NORMAL -> RightClickItemProxyNormal.INSTANCE;
            case LOW -> RightClickItemProxyLow.INSTANCE;
            case LOWEST -> RightClickItemProxyLowest.INSTANCE;
        };
    }

    private static abstract class RightClickItemProxy extends AbstractEventCommon {
        public RightClickItemProxy() {
            super(EventType.RIGHT_CLICK_ITEM_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeRightClickItemEvent(event); }

        protected void handle(PlayerInteractEvent.RightClickItem event) { super.onEvent(event); }
    }

    public static class RightClickItemProxyHighest extends RightClickItemProxy {
        static final RightClickItemProxyHighest INSTANCE = new RightClickItemProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickItem e) { handle(e); }
    }

    public static class RightClickItemProxyHigh extends RightClickItemProxy {
        static final RightClickItemProxyHigh INSTANCE = new RightClickItemProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickItem e) { handle(e); }
    }

    public static class RightClickItemProxyNormal extends RightClickItemProxy {
        static final RightClickItemProxyNormal INSTANCE = new RightClickItemProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickItem e) { handle(e); }
    }

    public static class RightClickItemProxyLow extends RightClickItemProxy {
        static final RightClickItemProxyLow INSTANCE = new RightClickItemProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickItem e) { handle(e); }
    }

    public static class RightClickItemProxyLowest extends RightClickItemProxy {
        static final RightClickItemProxyLowest INSTANCE = new RightClickItemProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickItem e) { handle(e); }
    }
}