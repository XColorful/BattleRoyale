package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeBlockBreakEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class BlockBreakEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> BlockBreakProxyHighest.INSTANCE;
            case HIGH -> BlockBreakProxyHigh.INSTANCE;
            case NORMAL -> BlockBreakProxyNormal.INSTANCE;
            case LOW -> BlockBreakProxyLow.INSTANCE;
            case LOWEST -> BlockBreakProxyLowest.INSTANCE;
        };
    }

    private static abstract class BlockBreakProxy extends AbstractEventCommon {
        public BlockBreakProxy() {
            super(EventType.BLOCK_BREAK_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeBlockBreakEvent(event); }

        protected void handle(BlockEvent.BreakEvent event) { super.onEvent(event); }
    }

    public static class BlockBreakProxyHighest extends BlockBreakProxy {
        static final BlockBreakProxyHighest INSTANCE = new BlockBreakProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(BlockEvent.BreakEvent e) { handle(e); }
    }

    public static class BlockBreakProxyHigh extends BlockBreakProxy {
        static final BlockBreakProxyHigh INSTANCE = new BlockBreakProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(BlockEvent.BreakEvent e) { handle(e); }
    }

    public static class BlockBreakProxyNormal extends BlockBreakProxy {
        static final BlockBreakProxyNormal INSTANCE = new BlockBreakProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(BlockEvent.BreakEvent e) { handle(e); }
    }

    public static class BlockBreakProxyLow extends BlockBreakProxy {
        static final BlockBreakProxyLow INSTANCE = new BlockBreakProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(BlockEvent.BreakEvent e) { handle(e); }
    }

    public static class BlockBreakProxyLowest extends BlockBreakProxy {
        static final BlockBreakProxyLowest INSTANCE = new BlockBreakProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(BlockEvent.BreakEvent e) { handle(e); }
    }
}