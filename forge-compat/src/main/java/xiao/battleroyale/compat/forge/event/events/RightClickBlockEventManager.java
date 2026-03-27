package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeRightClickBlockEvent;

public class RightClickBlockEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> RightClickBlockProxyHighest.INSTANCE;
            case HIGH -> RightClickBlockProxyHigh.INSTANCE;
            case NORMAL -> RightClickBlockProxyNormal.INSTANCE;
            case LOW -> RightClickBlockProxyLow.INSTANCE;
            case LOWEST -> RightClickBlockProxyLowest.INSTANCE;
        };
    }

    private static abstract class RightClickBlockProxy extends AbstractEventCommon {
        public RightClickBlockProxy() {
            super(EventType.RIGHT_CLICK_BLOCK_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeRightClickBlockEvent(event); }

        protected void handle(PlayerInteractEvent.RightClickBlock event) { super.onEvent(event); }
    }

    public static class RightClickBlockProxyHighest extends RightClickBlockProxy {
        static final RightClickBlockProxyHighest INSTANCE = new RightClickBlockProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickBlock e) { handle(e); }
    }

    public static class RightClickBlockProxyHigh extends RightClickBlockProxy {
        static final RightClickBlockProxyHigh INSTANCE = new RightClickBlockProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickBlock e) { handle(e); }
    }

    public static class RightClickBlockProxyNormal extends RightClickBlockProxy {
        static final RightClickBlockProxyNormal INSTANCE = new RightClickBlockProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickBlock e) { handle(e); }
    }

    public static class RightClickBlockProxyLow extends RightClickBlockProxy {
        static final RightClickBlockProxyLow INSTANCE = new RightClickBlockProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickBlock e) { handle(e); }
    }

    public static class RightClickBlockProxyLowest extends RightClickBlockProxy {
        static final RightClickBlockProxyLowest INSTANCE = new RightClickBlockProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.RightClickBlock e) { handle(e); }
    }
}