package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeFarmlandTrampleEvent;

public class FarmlandTrampleEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> FarmlandTrampleProxyHighest.INSTANCE;
            case HIGH -> FarmlandTrampleProxyHigh.INSTANCE;
            case NORMAL -> FarmlandTrampleProxyNormal.INSTANCE;
            case LOW -> FarmlandTrampleProxyLow.INSTANCE;
            case LOWEST -> FarmlandTrampleProxyLowest.INSTANCE;
        };
    }

    private static abstract class FarmlandTrampleProxy extends AbstractEventCommon {
        public FarmlandTrampleProxy() {
            super(EventType.FARMLAND_TRAMPLE_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeFarmlandTrampleEvent(event); }

        protected void handle(BlockEvent.FarmlandTrampleEvent event) { super.onEvent(event); }
    }

    public static class FarmlandTrampleProxyHighest extends FarmlandTrampleProxy {
        static final FarmlandTrampleProxyHighest INSTANCE = new FarmlandTrampleProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(BlockEvent.FarmlandTrampleEvent e) { handle(e); }
    }

    public static class FarmlandTrampleProxyHigh extends FarmlandTrampleProxy {
        static final FarmlandTrampleProxyHigh INSTANCE = new FarmlandTrampleProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(BlockEvent.FarmlandTrampleEvent e) { handle(e); }
    }

    public static class FarmlandTrampleProxyNormal extends FarmlandTrampleProxy {
        static final FarmlandTrampleProxyNormal INSTANCE = new FarmlandTrampleProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(BlockEvent.FarmlandTrampleEvent e) { handle(e); }
    }

    public static class FarmlandTrampleProxyLow extends FarmlandTrampleProxy {
        static final FarmlandTrampleProxyLow INSTANCE = new FarmlandTrampleProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(BlockEvent.FarmlandTrampleEvent e) { handle(e); }
    }

    public static class FarmlandTrampleProxyLowest extends FarmlandTrampleProxy {
        static final FarmlandTrampleProxyLowest INSTANCE = new FarmlandTrampleProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(BlockEvent.FarmlandTrampleEvent e) { handle(e); }
    }
}