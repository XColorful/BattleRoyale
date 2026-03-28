package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeLeftClickBlockEvent;

public class LeftClickBlockEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> LeftClickBlockProxyHighest.INSTANCE;
            case HIGH -> LeftClickBlockProxyHigh.INSTANCE;
            case NORMAL -> LeftClickBlockProxyNormal.INSTANCE;
            case LOW -> LeftClickBlockProxyLow.INSTANCE;
            case LOWEST -> LeftClickBlockProxyLowest.INSTANCE;
        };
    }

    private static abstract class LeftClickBlockProxy extends AbstractEventCommon {
        public LeftClickBlockProxy() {
            super(EventType.LEFT_CLICK_BLOCK_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeLeftClickBlockEvent(event); }

        protected void handle(PlayerInteractEvent.LeftClickBlock event) { super.onEvent(event); }
    }

    public static class LeftClickBlockProxyHighest extends LeftClickBlockProxy {
        static final LeftClickBlockProxyHighest INSTANCE = new LeftClickBlockProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.LeftClickBlock e) { handle(e); }
    }

    public static class LeftClickBlockProxyHigh extends LeftClickBlockProxy {
        static final LeftClickBlockProxyHigh INSTANCE = new LeftClickBlockProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.LeftClickBlock e) { handle(e); }
    }

    public static class LeftClickBlockProxyNormal extends LeftClickBlockProxy {
        static final LeftClickBlockProxyNormal INSTANCE = new LeftClickBlockProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.LeftClickBlock e) { handle(e); }
    }

    public static class LeftClickBlockProxyLow extends LeftClickBlockProxy {
        static final LeftClickBlockProxyLow INSTANCE = new LeftClickBlockProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.LeftClickBlock e) { handle(e); }
    }

    public static class LeftClickBlockProxyLowest extends LeftClickBlockProxy {
        static final LeftClickBlockProxyLowest INSTANCE = new LeftClickBlockProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerInteractEvent.LeftClickBlock e) { handle(e); }
    }
}